package com.blamejared.compat.tconstruct.handlers;

import com.blamejared.api.annotations.*;
import com.blamejared.compat.tconstruct.TConstructHelper;
import com.blamejared.mtlib.helpers.*;
import com.blamejared.mtlib.utils.*;
//import mezz.jei.api.gui.IDrawable;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.*;
import minetweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.smeltery.*;
import slimeknights.tconstruct.plugin.jei.CastingRecipeWrapper;
import slimeknights.tconstruct.plugin.jei.JEIPlugin;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.*;

import java.util.*;

import static com.blamejared.mtlib.helpers.InputHelper.*;
import static com.blamejared.mtlib.helpers.StackHelper.matches;

/**
 * Created by Jared for 1.10.2.
 * Adapted by Rinart73 on 24.07.17 for 1.11.2
 */
@ZenClass("mods.tconstruct.Casting")
@Handler("tconstruct")
public class Casting {

    protected static final String name = "TConstruct Casting";


    /**********************************************
     * TConstruct Basic Casting Recipes
     **********************************************/

    // Adding a TConstruct Basic Casting recipe
    @ZenMethod
    @Document({"output", "liquid", "cast", "consumeCast", "timeInTicks"})
    public static void addBasinRecipe(IItemStack output, ILiquidStack liquid, @Optional IItemStack cast, @Optional boolean consumeCast, @Optional int timeInTicks) {
        Object castingOn = null;
        if(TConstructHelper.isJei) {
            if(TConstructHelper.castingRecipeCategory == null)
                TConstructHelper.castingRecipeCategory = TConstructHelper.getCastingRecipeCategory(JEIPlugin.jeiHelpers.getGuiHelper());
            castingOn = TConstructHelper.castingRecipeCategory.castingBasin;
        }
        addRecipe(output, liquid, cast, consumeCast, timeInTicks, TConstructHelper.basinCasting, castingOn);
    }

    // Adding a TConstruct Table Casting recipe
    @ZenMethod
    @Document({"output", "liquid", "cast", "consumeCast", "timeInTicks"})
    public static void addTableRecipe(IItemStack output, ILiquidStack liquid, @Optional IItemStack cast, @Optional boolean consumeCast, @Optional int timeInTicks) {
        Object castingOn = null;
        if(TConstructHelper.isJei) {
            if(TConstructHelper.castingRecipeCategory == null)
                TConstructHelper.castingRecipeCategory = TConstructHelper.getCastingRecipeCategory(JEIPlugin.jeiHelpers.getGuiHelper());
            castingOn = TConstructHelper.castingRecipeCategory.castingTable;
        }
        addRecipe(output, liquid, cast, consumeCast, timeInTicks, TConstructHelper.tableCasting, castingOn);
    }

    public static void addRecipe(IItemStack output, ILiquidStack liquid, IItemStack cast, boolean consumeCast, int timeInTicks, LinkedList<ICastingRecipe> list, Object castingOn) {
        if(liquid == null || output == null) {
            LogHelper.logError(String.format("Required parameters missing for %s Recipe.", name));
            return;
        }

        RecipeMatch match = null;
        if(cast != null) {
            match = RecipeMatch.of(toStack(cast));
        }

        FluidStack fluid = toFluid(liquid);

        if(timeInTicks <= 0) {
            timeInTicks = CastingRecipe.calcCooldownTime(fluid.getFluid(), fluid.amount);
        }

        CastingRecipe recipe = new CastingRecipe(toStack(output), match, fluid, timeInTicks, consumeCast, false);
        MineTweakerAPI.apply(new Add(recipe, list, castingOn));
    }


    //Passes the list to the base list implementation, and adds the recipe
    private static class Add extends BaseListAddition<ICastingRecipe> {

        Object castingOn;
        CastingRecipeWrapper wrapper;

        public Add(CastingRecipe recipe, LinkedList<ICastingRecipe> list, Object castingOn) {
            super(Casting.name, list);
            this.recipes.add(recipe);
            this.castingOn = castingOn;
        }

        @Override
        public void apply() {
            for(ICastingRecipe recipe : recipes) {
                if(recipe != null) {
                    if(list.add(recipe)) {
                        successful.add(recipe);
                        if(TConstructHelper.isJei) {
                            wrapper = new CastingRecipeWrapper((CastingRecipe) recipe, (mezz.jei.api.gui.IDrawable)castingOn);
                            MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(wrapper);
                            TConstructHelper.castingRecipeWrappers.put(recipe, wrapper);
                        }
                    } else {
                        LogHelper.logError(String.format("Error adding %s Recipe for %s", name, getRecipeInfo(recipe)));
                    }
                } else {
                    LogHelper.logError(String.format("Error adding %s Recipe: null object", name));
                }
            }
        }

        @Override
        public void undo() {
            for(ICastingRecipe recipe : successful) {
                if(recipe != null) {
                    if(!list.remove(recipe)) {
                        LogHelper.logError(String.format("Error removing %s Recipe for %s", name, this.getRecipeInfo(recipe)));
                    } else if(TConstructHelper.isJei) {
                        MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(wrapper);
                        TConstructHelper.castingRecipeWrappers.remove(recipe);
                    }
                } else {
                    LogHelper.logError(String.format("Error removing %s Recipe: null object", name));
                }
            }
        }

        @Override
        protected String getRecipeInfo(ICastingRecipe recipe) {
            return LogHelper.getStackDescription(((CastingRecipe) recipe).getResult());
        }

        @Override
        public String getJEICategory(ICastingRecipe recipe) {
            return "tconstruct.casting_table";
        }
    }


    // Removing a TConstruct Basin Casting recipe
    @ZenMethod
    @Document({"output", "liquid", "cast"})
    public static void removeBasinRecipe(IIngredient output, @Optional IIngredient liquid, @Optional IItemStack cast) {
        removeRecipe(output, liquid, cast, TConstructHelper.basinCasting);
    }

    // Removing a TConstruct Table Casting recipe
    @ZenMethod
    @Document({"output", "liquid", "cast"})
    public static void removeTableRecipe(IIngredient output, @Optional IIngredient liquid, @Optional IItemStack cast) {
        removeRecipe(output, liquid, cast, TConstructHelper.tableCasting);
    }

    public static void removeRecipe(IIngredient output, IIngredient liquid, IItemStack cast, LinkedList<ICastingRecipe> list) {
        if(output == null) {
            LogHelper.logError(String.format("Required parameters missing for %s Recipe.", name));
            return;
        }

        if(liquid == null) {
            liquid = IngredientAny.INSTANCE;
        }

        NonNullList<ItemStack> match = null;
        if(cast != null) {
            match = NonNullList.create();
            match.add(toStack(cast));
        }

        List<ICastingRecipe> recipes = new LinkedList<>();

        for(ICastingRecipe recipe : list) {
            if(recipe instanceof CastingRecipe) {
                CastingRecipe rec = (CastingRecipe) recipe;

                if(rec.getResult() == null)
                    continue;
                if(!matches(output, toIItemStack(rec.getResult())))
                    continue;
                if(!matches(liquid, toILiquidStack(rec.getFluid())))
                    continue;
                if(cast != null) {
                    if(rec.cast == null || rec.cast.matches(match) == null)
                        continue;
                }

                recipes.add(recipe);
            }
        }

        if(!recipes.isEmpty()) {
            MineTweakerAPI.apply(new Remove(list, recipes));
        } else {
            LogHelper.logWarning(String.format("No %s Recipe found for output %s, material %s and cast %s. Command ignored!", Casting.name, output.toString(), liquid.toString(), cast != null ? cast.toString() : null));
        }
    }

    // Removes all matching recipes, apply is never the same for anything, so will always need to override it
    private static class Remove extends BaseListRemoval<ICastingRecipe> {

        public Remove(List<ICastingRecipe> list, List<ICastingRecipe> recipes) {
            super(Casting.name, list, recipes);
        }

        @Override
        public void apply() {
            for (ICastingRecipe recipe : this.recipes) {
                if (recipe != null) {
                    if (this.list.remove(recipe)) {
                        successful.add(recipe);
                        CastingRecipeWrapper wrapper = TConstructHelper.castingRecipeWrappers.get(recipe);
                        if(wrapper != null)
                            MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(wrapper);
                    } else {
                        LogHelper.logError(String.format("Error removing %s Recipe for %s", name, getRecipeInfo(recipe)));
                    }
                } else {
                    LogHelper.logError(String.format("Error removing %s Recipe: null object", name));
                }
            }
        }

        @Override
        public void undo() {
            for (ICastingRecipe recipe : successful) {
                if (recipe != null) {
                    if (!list.add(recipe)) {
                        LogHelper.logError(String.format("Error restoring %s Recipe for %s", name, getRecipeInfo(recipe)));
                    } else {
                        CastingRecipeWrapper wrapper = TConstructHelper.castingRecipeWrappers.get(recipe);
                        if(wrapper != null)
                            MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(wrapper);
                    }
                } else {
                    LogHelper.logError(String.format("Error restoring %s Recipe: null object", name));
                }
            }
        }

        @Override
        protected String getRecipeInfo(ICastingRecipe recipe) {
            return LogHelper.getStackDescription(((CastingRecipe) recipe).getResult());
        }

        @Override
        public String getJEICategory(ICastingRecipe recipe) {
            return "tconstruct.casting_table";
        }

    }
}