package modtweaker2.mods.auracore.handlers;

import static modtweaker2.helpers.InputHelper.toStacks;
import static modtweaker2.helpers.InputHelper.toStack;
import static modtweaker2.helpers.InputHelper.toIItemStack;
import static modtweaker2.helpers.StackHelper.matches;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import dev.tilera.auracore.api.AuracoreRecipes;
import dev.tilera.auracore.api.crafting.IInfusionRecipe;
import dev.tilera.auracore.api.crafting.ShapedInfusionCraftingRecipe;
import dev.tilera.auracore.api.crafting.ShapelessInfusionCraftingRecipe;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import modtweaker2.helpers.LogHelper;
import modtweaker2.mods.thaumcraft.ThaumcraftHelper;
import modtweaker2.utils.BaseListAddition;
import modtweaker2.utils.BaseListRemoval;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.auracore.Infusion")
public class Infusion {

    public static final String name = "Auracore Infusion Altar";
    
    @ZenMethod
	public static void addShaped(String key, String research, int cost, IItemStack output, String aspects, IItemStack[][] ingredients) {
		List<ItemStack> stacks = new ArrayList<>();
		int width = 0;
		int height = 0;
		for(IItemStack[] row : ingredients) {
			height++;
			width = Math.max(width, row.length);
			for (IItemStack i : row) {
				stacks.add(toStack(i));
			}
		}
		MineTweakerAPI.apply(new Add(new ShapedInfusionCraftingRecipe(key, research, width, height, stacks.toArray(new ItemStack[stacks.size()]), toStack(output), cost, ThaumcraftHelper.parseAspects(aspects))));
	}

	@ZenMethod
	public static void addShapeless(String key, String research, int cost, IItemStack output, String aspects, IItemStack[] ingredients) {
	    MineTweakerAPI.apply(new Add(new ShapelessInfusionCraftingRecipe(key, research, toStack(output), Arrays.asList(toStacks(ingredients)), cost, ThaumcraftHelper.parseAspects(aspects))));
	}

    private static class Add extends BaseListAddition<IInfusionRecipe> {
		public Add(IInfusionRecipe recipe) {
			super(Infusion.name, AuracoreRecipes.getInfusionRecipes());
			recipes.add(recipe);
		}

		@Override
		protected String getRecipeInfo(IInfusionRecipe recipe) {
		    ItemStack stack = recipe.getRecipeOutput();
		    
		    if(stack instanceof ItemStack)
		        return LogHelper.getStackDescription(stack);
		    else
		        return "Unknown output";
		}
	}

	@ZenMethod
	public static void removeRecipe(IItemStack output) {
	    List<IInfusionRecipe> recipes = new LinkedList<IInfusionRecipe>();
	    
	    for(IInfusionRecipe recipe : AuracoreRecipes.getInfusionRecipes()) {
	        if(recipe.getRecipeOutput() != null && matches(output, toIItemStack(recipe.getRecipeOutput()))) {
	            recipes.add(recipe);
	        }
	    }
	    
	    if(!recipes.isEmpty()) {
	        MineTweakerAPI.apply(new Remove(recipes));
	    } else {
	        LogHelper.logWarning(String.format("No %s Recipe found for %s. Command Ignored", Infusion.name, output.toString()));
	    }
	}

	private static class Remove extends BaseListRemoval<IInfusionRecipe> {
		public Remove(List<IInfusionRecipe> recipes) {
			super(Infusion.name, AuracoreRecipes.getInfusionRecipes(), recipes);
		}

        @Override
        protected String getRecipeInfo(IInfusionRecipe recipe) {
            ItemStack stack = recipe.getRecipeOutput();
            
            if(stack instanceof ItemStack)
                return LogHelper.getStackDescription(stack);
            else
                return "Unknown output";
        }
	}

}
