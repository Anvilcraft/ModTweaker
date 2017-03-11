package modtweaker.mods.forestry.handlers;

import com.blamejared.mtlib.helpers.LogHelper;
import forestry.api.recipes.*;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.factory.recipes.jei.fabricator.FabricatorRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapper;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.*;
import modtweaker.mods.forestry.*;
import modtweaker.mods.forestry.recipes.*;
import net.minecraftforge.fluids.FluidRegistry;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.*;

import java.util.*;

import static com.blamejared.mtlib.helpers.InputHelper.*;
import static com.blamejared.mtlib.helpers.StackHelper.matches;

@ZenClass("mods.forestry.ThermionicFabricator")
public class ThermionicFabricator {
    
    public static final String nameSmelting = "Forestry Thermionic Fabricator (Smelting)";
    public static final String nameCasting = "Forestry Thermionic Fabricator (Casting)";
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Adds smelting recipe to Thermionic Fabricator
     *
     * @param fluidOutput  recipe fluid amount
     * @param itemInput    recipe input input
     * @param meltingPoint point at where itemInput melts down
     */
    @ZenMethod
    public static void addSmelting(int fluidOutput, IItemStack itemInput, int meltingPoint) {
        //The machines internal tank accept only liquid glass, therefor this function only accept the amount and hardcode the fluid to glass
        MineTweakerAPI.apply(new AddSmelting(new FabricatorSmeltingRecipe(toStack(itemInput), FluidRegistry.getFluidStack("glass", fluidOutput), meltingPoint)));
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Adds casting recipe to Thermionic Fabricator
     *
     * @param output      recipe output item
     * @param ingredients list of input items
     * @param fluidInput  recipe fluid input
     *                    * @param plan            recipe plan item (optional)
     *                    * @param remainingItems  no idea(optional)
     */
    @ZenMethod
    public static void addCast(IItemStack output, IIngredient[][] ingredients, int fluidInput, @Optional IItemStack plan) {
        IDescriptiveRecipe recipe = new ShapedRecipeCustom(toStack(output), toShapedObjects(ingredients));
        MineTweakerAPI.apply(new AddCast(new FabricatorRecipe(toStack(plan), FluidRegistry.getFluidStack("glass", fluidInput), recipe)));
    }
    
    /*
    Implements the actions to add a recipe
    Since the machine has two crafting Steps, this is a constructors for both
    */
    private static class AddSmelting extends ForestryListAddition<IFabricatorSmeltingRecipe> {
        
        public AddSmelting(IFabricatorSmeltingRecipe recipe) {
            super(ThermionicFabricator.nameSmelting, RecipeManagers.fabricatorSmeltingManager);
            recipes.add(recipe);
        }
        
        @Override
        public String getRecipeInfo(IFabricatorSmeltingRecipe recipe) {
            return LogHelper.getStackDescription(recipe.getResource());
        }
        
        //It's not clear to me how the smelting recipes should be wrapped
        @Override
        public IRecipeWrapper wrapRecipe(IFabricatorSmeltingRecipe recipe) {
            return null;
        }
    }
    
    private static class AddCast extends ForestryListAddition<IFabricatorRecipe> {
        
        public AddCast(IFabricatorRecipe recipe) {
            super(ThermionicFabricator.nameCasting, RecipeManagers.fabricatorManager);
            recipes.add(recipe);
        }
        
        @Override
        public String getRecipeInfo(IFabricatorRecipe recipe) {
            return LogHelper.getStackDescription(recipe.getRecipeOutput());
        }
        
        @Override
        public IRecipeWrapper wrapRecipe(IFabricatorRecipe recipe) {
            return new FabricatorRecipeWrapper(recipe);
        }
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Removes smelting recipe from Thermionic Fabricator
     *
     * @param itemInput = item input
     */
    @ZenMethod
    public static void removeSmelting(IIngredient itemInput) {
        List<IFabricatorSmeltingRecipe> recipes = new LinkedList<IFabricatorSmeltingRecipe>();
        
        for(IFabricatorSmeltingRecipe r : RecipeManagers.fabricatorSmeltingManager.recipes()) {
            if(r != null && r.getResource() != null && matches(itemInput, toIItemStack(r.getResource()))) {
                recipes.add(r);
            }
        }
        
        if(!recipes.isEmpty()) {
            MineTweakerAPI.apply(new RemoveSmelting(recipes));
        } else {
            LogHelper.logWarning(String.format("No %s Recipe found for %s. Command ignored!", ThermionicFabricator.nameSmelting, itemInput.toString()));
        }
    }
    
    /**
     * Removes casting recipe from Thermionic Fabricator
     *
     * @param product = recipe result
     */
    @ZenMethod
    public static void removeCast(IIngredient product) {
        List<IFabricatorRecipe> recipes = new LinkedList<IFabricatorRecipe>();
        
        for(IFabricatorRecipe r : RecipeManagers.fabricatorManager.recipes()) {
            if(r != null && r.getRecipeOutput() != null && matches(product, toIItemStack(r.getRecipeOutput()))) {
                recipes.add(r);
            }
        }
        
        if(!recipes.isEmpty()) {
            MineTweakerAPI.apply(new RemoveCasts(recipes));
        } else {
            LogHelper.logWarning(String.format("No %s Recipe found for %s. Command ignored!", ThermionicFabricator.nameSmelting, product.toString()));
        }
    }
    
    private static class RemoveSmelting extends ForestryListRemoval<IFabricatorSmeltingRecipe, IFabricatorSmeltingManager> {
        
        public RemoveSmelting(List<IFabricatorSmeltingRecipe> recipes) {
            super(ThermionicFabricator.nameSmelting, RecipeManagers.fabricatorSmeltingManager, recipes);
        }
        
        @Override
        public String getRecipeInfo(IFabricatorSmeltingRecipe recipe) {
            return LogHelper.getStackDescription(recipe.getResource());
        }
        
        //It's not clear to me how the smelting recipes should be wrapped
        @Override
        public IRecipeWrapper wrapRecipe(IFabricatorSmeltingRecipe recipe) {
            return null;
        }
    }
    
    private static class RemoveCasts extends ForestryListRemoval<IFabricatorRecipe, IFabricatorManager> {
        
        public RemoveCasts(List<IFabricatorRecipe> recipes) {
            super(ThermionicFabricator.nameCasting, RecipeManagers.fabricatorManager, recipes);
        }
        
        @Override
        public String getRecipeInfo(IFabricatorRecipe recipe) {
            return LogHelper.getStackDescription(recipe.getRecipeOutput());
        }
        
        @Override
        public IRecipeWrapper wrapRecipe(IFabricatorRecipe recipe) {
            return new FabricatorRecipeWrapper(recipe);
        }
    }
}
