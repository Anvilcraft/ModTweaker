package com.blamejared.compat.forestry;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAddForestry;
import com.blamejared.mtlib.utils.BaseRemoveForestry;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.mc1120.item.MCItemStack;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.factory.recipes.SqueezerRecipe;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static com.blamejared.mtlib.helpers.InputHelper.*;
import static com.blamejared.mtlib.helpers.StackHelper.matches;

@ZenClass("mods.forestry.Squeezer")
@ModOnly("forestry")
@ZenRegister
public class Squeezer {
	
	public static final String name = "Forestry Squeezer";
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Adds recipe to Squeezer
	 *
	 * @param fluidOutput recipe fluid amount
	 * @param ingredients recipe ingredients
	 * @param timePerItem time per crafting operation
	 *                    * @param itemOutput recipe output (optional)
	 */
	@ZenMethod
	public static void addRecipe(ILiquidStack fluidOutput, IItemStack[] ingredients, int timePerItem, @Optional WeightedItemStack itemOutput) {
		ModTweaker.LATE_ADDITIONS.add(new Add(new SqueezerRecipe(timePerItem, toNonNullList(toStacks(ingredients)), toFluid(fluidOutput), itemOutput != null ? toStack(itemOutput.getStack()) : ItemStack.EMPTY, itemOutput != null ? itemOutput.getChance() : 0)));
	}
	
	private static class Add extends BaseAddForestry<ISqueezerRecipe> {
		
		public Add(ISqueezerRecipe recipe) {
			super(Squeezer.name, RecipeManagers.squeezerManager, recipe);
		}
		
		@Override
		public String getRecipeInfo() {
			return LogHelper.getStackDescription(recipe.getFluidOutput());
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Removes a recipe for the Centrifuge
	 *
	 * @param liquid liquid output
	 *               * @param ingredients list of ingredients (optional)
	 */
	@ZenMethod
	public static void removeRecipe(ILiquidStack liquid, @Optional IIngredient[] ingredients) {
		ModTweaker.LATE_REMOVALS.add(new Remove(liquid, ingredients));
	}
	
	private static class Remove extends BaseRemoveForestry<ISqueezerRecipe> {

        private final ILiquidStack liquid;
        private final IIngredient[] ingredients;

        public Remove(ILiquidStack liquid, IIngredient[] ingredients) {
			super(Squeezer.name, RecipeManagers.squeezerManager);
            this.liquid = liquid;
            this.ingredients = ingredients;
        }
		
		@Override
		public String getRecipeInfo() {
			return liquid.getDisplayName();
		}


        @Override
		public boolean checkIsRecipe(ISqueezerRecipe recipe) {
            // optional check for ingredients
            if(ingredients != null) {
                boolean matched = false;
                for(int i = 0; i < ingredients.length; i++) {
                    if(matches(ingredients[i], toIItemStack(recipe.getResources().get(i))))
                        matched = true;
                    else {
                        matched = false;
                        // if one ingredients doesn't match abort all further checks
                        break;
                    }
                }
                // if some ingredient doesn't match, the last one is false
                if(matched)
                    return true;
            } else {
                return true;
            }

            return false;
        }

    }
}
