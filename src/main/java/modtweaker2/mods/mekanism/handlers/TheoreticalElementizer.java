package modtweaker2.mods.mekanism.handlers;

import java.util.LinkedList;
import java.util.List;

import mekanism.common.recipe.ElementizerRecipeHandler;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import static modtweaker2.helpers.InputHelper.toIItemStack;
import static modtweaker2.helpers.InputHelper.toStack;
import static modtweaker2.helpers.StackHelper.matches;
import modtweaker2.helpers.LogHelper;
import modtweaker2.utils.BaseListAddition;
import modtweaker2.utils.BaseListRemoval;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.mekanism.Elementizer")
public class TheoreticalElementizer {

    public static final String name = "Theoretical Elementizer";
    
    @ZenMethod
    public static void addOutput(IItemStack itemOutput) {
        if(itemOutput == null) {
            LogHelper.logError(String.format("Required parameters missing for %s Recipe.", name));
            return;
        }
        MineTweakerAPI.apply(new AddOutput(toStack(itemOutput)));
    }

    private static class AddOutput extends BaseListAddition<ItemStack> {

		public AddOutput(ItemStack recipe) {
			super(TheoreticalElementizer.name, null);
			recipes.add(recipe);
		}

		public void apply() {
		    for(ItemStack recipe : recipes) {
                boolean applied = ElementizerRecipeHandler.outputItems.add(recipe);
		        
		        if(applied) {
		            successful.add(recipe);
		        }
		    }
		}

		public void undo() {
		    for(ItemStack recipe : successful) {
                ElementizerRecipeHandler.outputItems.remove(recipe);
		    }
		}
		
		@Override
		protected boolean equals(ItemStack recipe, ItemStack otherRecipe) {
		    return recipe != null && recipe.isItemEqual(otherRecipe);
		}
		
		@Override
		protected String getRecipeInfo(ItemStack recipe) {
		    return LogHelper.getStackDescription(recipe);
		}
	}

    @ZenMethod
	public static void removeOutput(IIngredient input) {
	    List<ItemStack> recipes = new LinkedList<ItemStack>();
	    
	    for(ItemStack recipe : ElementizerRecipeHandler.outputItems) {
	        if(recipe != null && matches(input, toIItemStack(recipe))) {
	            recipes.add(recipe);
	        }
	    }
	    
	    if(!recipes.isEmpty()) {
			MineTweakerAPI.apply(new RemoveOutput(recipes));
	    } else {
	        LogHelper.logWarning(String.format("No %s Recipe found for %s.", name, input.toString()));
	    }
	}

	private static class RemoveOutput extends BaseListRemoval<ItemStack> {

	    public RemoveOutput(List<ItemStack> recipes) {
			super(TheoreticalElementizer.name, null, recipes);
		}

		public void apply() {
		    for(ItemStack recipe : recipes) {
		        boolean removed = ElementizerRecipeHandler.outputItems.remove(recipe);
		        
		        if(removed) {
		            successful.add(recipe);
		        }
		    }
		}

		public void undo() {
            for(ItemStack recipe : successful) {
                ElementizerRecipeHandler.outputItems.add(recipe);
            }
		}
		
        @Override
        protected boolean equals(ItemStack recipe, ItemStack otherRecipe) {
            return recipe != null && recipe.isItemEqual(otherRecipe);
        }
		
        @Override
        protected String getRecipeInfo(ItemStack recipe) {
            return LogHelper.getStackDescription(recipe);
        }
	}

    @ZenMethod
    public static void addFuel(IItemStack itemFuel) {
        if(itemFuel == null) {
            LogHelper.logError(String.format("Required parameters missing for %s Recipe.", name));
            return;
        }
        MineTweakerAPI.apply(new AddFuel(toStack(itemFuel)));
    }

    private static class AddFuel extends BaseListAddition<ItemStack> {

		public AddFuel(ItemStack recipe) {
			super(TheoreticalElementizer.name, null);
			recipes.add(recipe);
		}

		public void apply() {
		    for(ItemStack recipe : recipes) {
                boolean applied = ElementizerRecipeHandler.fuelItems.add(recipe);
		        
		        if(applied) {
		            successful.add(recipe);
		        }
		    }
		}

		public void undo() {
		    for(ItemStack recipe : successful) {
                ElementizerRecipeHandler.fuelItems.remove(recipe);
		    }
		}
		
		@Override
		protected boolean equals(ItemStack recipe, ItemStack otherRecipe) {
		    return recipe != null && recipe.isItemEqual(otherRecipe);
		}
		
		@Override
		protected String getRecipeInfo(ItemStack recipe) {
		    return LogHelper.getStackDescription(recipe);
		}
	}

    @ZenMethod
	public static void removeFuel(IIngredient input) {
	    List<ItemStack> recipes = new LinkedList<ItemStack>();
	    
	    for(ItemStack recipe : ElementizerRecipeHandler.fuelItems) {
	        if(recipe != null && matches(input, toIItemStack(recipe))) {
	            recipes.add(recipe);
	        }
	    }
	    
	    if(!recipes.isEmpty()) {
			MineTweakerAPI.apply(new RemoveFuel(recipes));
	    } else {
	        LogHelper.logWarning(String.format("No %s Recipe found for %s.", name, input.toString()));
	    }
	}

	private static class RemoveFuel extends BaseListRemoval<ItemStack> {

	    public RemoveFuel(List<ItemStack> recipes) {
			super(TheoreticalElementizer.name, null, recipes);
		}

		public void apply() {
		    for(ItemStack recipe : recipes) {
		        boolean removed = ElementizerRecipeHandler.fuelItems.remove(recipe);
		        
		        if(removed) {
		            successful.add(recipe);
		        }
		    }
		}

		public void undo() {
            for(ItemStack recipe : successful) {
                ElementizerRecipeHandler.fuelItems.add(recipe);
            }
		}
		
        @Override
        protected boolean equals(ItemStack recipe, ItemStack otherRecipe) {
            return recipe != null && recipe.isItemEqual(otherRecipe);
        }
		
        @Override
        protected String getRecipeInfo(ItemStack recipe) {
            return LogHelper.getStackDescription(recipe);
        }
	}

}
