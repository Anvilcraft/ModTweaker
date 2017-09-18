package com.blamejared.compat.forestry;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAddForestry;
import com.blamejared.mtlib.utils.BaseMapAddition;
import com.blamejared.mtlib.utils.BaseMapRemoval;
import com.blamejared.mtlib.utils.BaseRemoveForestry;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.factory.recipes.FermenterRecipe;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.blamejared.mtlib.helpers.InputHelper.*;
import static com.blamejared.mtlib.helpers.StackHelper.matches;

@ZenClass("mods.forestry.Fermenter")
@ModOnly("forestry")
@ZenRegister
public class Fermenter {

	public static final String name = "Forestry Fermenter";
	public static final String nameFuel = name + " (Fuel)";
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Adds recipe to fermenter
	 * Amount of fluid output: fermentationValue * fluidOutputModifier
	 * Note: the actual consumption of fluid input depends on the fermentation fuel
	 * 
	 * @param fluidOutput type of fluid produced
	 * @param resource organic item
	 * @param fluidInput type of fluid required in input
	 * @param fermentationValue amount of inputFluid on organic item requires
	 * @param fluidOutputModifier Output multiplier (this is usually a from the input fluid)
	 */
	@ZenMethod
	public static void addRecipe(ILiquidStack fluidOutput, IItemStack resource, ILiquidStack fluidInput, int fermentationValue, float fluidOutputModifier) {
		ModTweaker.LATE_ADDITIONS.add(new Add(new FermenterRecipe(toStack(resource), fermentationValue, fluidOutputModifier, getFluid(fluidOutput), toFluid(fluidInput))));
	}

	private static class Add extends BaseAddForestry<IFermenterRecipe> {
		public Add(IFermenterRecipe recipe) {
			super(Fermenter.name, RecipeManagers.fermenterManager, recipe);
		}

		@Override
		public String getRecipeInfo() {
			return LogHelper.getStackDescription(recipe.getOutput());
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes recipe from Fermenter
	 *
	 * @param input type of item in input
	 */
	@ZenMethod
	public static void removeRecipe(IIngredient input) {
		ModTweaker.LATE_REMOVALS.add(new Remove(input));
	}

	private static class Remove extends BaseRemoveForestry<IFermenterRecipe> {

		private IIngredient input;

		public Remove(IIngredient input) {
			super(Fermenter.name, RecipeManagers.fermenterManager);
			this.input = input;
		}

		@Override
		protected String getRecipeInfo() {
			return input.toString();
		}

		@Override
		public boolean checkIsRecipe(IFermenterRecipe recipe) {
			// check for input items
			if(recipe != null && matches(input, toIItemStack(recipe.getResource()))) {
				return true;
			}

			// check for input liquids
			return recipe != null && matches(input, toILiquidStack(recipe.getFluidResource()));
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Adds fermenter fuel
	 * Note: the actual consumption of fluid input depends on the fermentation fuel
	 * 
	 * @param item Item that is a valid fuel for the fermenter
	 * @param fermentPerCycle How much is fermented per work cycle, i.e. how much fluid of the input is consumed.
	 * @param burnDuration Amount of work cycles a single item of this fuel lasts before expiring.
	 */
	@ZenMethod
	public static void addFuel(IItemStack item, int fermentPerCycle, int burnDuration) {
		ModTweaker.LATE_ADDITIONS.add(new AddFuel(new FermenterFuel(toStack(item), fermentPerCycle, burnDuration)));
	}
	
	private static class AddFuel extends BaseMapAddition<ItemStack, FermenterFuel> {
		public AddFuel(FermenterFuel fuelEntry) {
			super(Fermenter.nameFuel, FuelManager.fermenterFuel);
			recipes.put(fuelEntry.getItem(), fuelEntry);
		}
		
		@Override
		public String getRecipeInfo(Entry<ItemStack, FermenterFuel> fuelEntry) {
			return LogHelper.getStackDescription(fuelEntry.getKey());
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Removes fermenter fuel
	 * 
	 * @param fermenterItem Item that is a valid fuel for the fermenter
	 */
	@ZenMethod
	public static void removeFuel(IIngredient fermenterItem) {
		Map<ItemStack, FermenterFuel> fuelItems = new HashMap<>();
		
		for(Entry<ItemStack, FermenterFuel> fuelItem : FuelManager.fermenterFuel.entrySet()) {
			if(fuelItem != null && matches(fermenterItem, toIItemStack(fuelItem.getValue().getItem()))) {
				fuelItems.put(fuelItem.getKey(), fuelItem.getValue());
			}
		}

		if(!fuelItems.isEmpty()) {
			ModTweaker.LATE_REMOVALS.add(new RemoveFuel(fuelItems));
		} else {
			LogHelper.logWarning(String.format("No %s Recipe found for %s. Command ignored!", Fermenter.name, fermenterItem.toString()));
		}
	}
	
	private static class RemoveFuel extends BaseMapRemoval<ItemStack, FermenterFuel> {
		public RemoveFuel(Map<ItemStack, FermenterFuel> recipes) {
			super(Fermenter.nameFuel, FuelManager.fermenterFuel, recipes);
		}
		
		@Override
		public String getRecipeInfo(Entry<ItemStack, FermenterFuel> fuelEntry) {
			return LogHelper.getStackDescription(fuelEntry.getKey());
		}
	}
}

