package com.blamejared.compat.actuallyaddition;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.blamejared.mtlib.utils.BaseListRemoval;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.LensConversionRecipe;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Collections;
import java.util.List;

@ZenClass("mods.actuallyadditions.AtomicReconstructor")
@ModOnly("actuallyadditions")
@ZenRegister
public class AtomicReconstructor {
    
    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack input, int energyUsed) {
        ModTweaker.LATE_ADDITIONS.add(new Add(Collections.singletonList(new LensConversionRecipe(InputHelper.toStack(input), InputHelper.toStack(output), energyUsed, ActuallyAdditionsAPI.lensDefaultConversion))));
    }
    
    @ZenMethod
    public static void removeRecipe(IItemStack output) {
        ModTweaker.LATE_ADDITIONS.add(new Remove(output));
    }
    
    private static class Add extends BaseListAddition<LensConversionRecipe> {
        
        protected Add(List<LensConversionRecipe> recipies) {
            super("Atomic Reconstructor", ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES, recipies);
        }
        
        @Override
        protected String getRecipeInfo(LensConversionRecipe recipe) {
            return LogHelper.getStackDescription(recipe.outputStack);
        }
        
    }
    
    private static class Remove extends BaseListRemoval<LensConversionRecipe> {
        
        private IItemStack output;
        
        protected Remove(IItemStack output) {
            super("Atomic Reconstructor", ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES);
            this.output = output;
        }
        
        @Override
        public void apply() {
            ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES.stream().filter(recipe -> output.matches(InputHelper.toIItemStack(recipe.outputStack))).forEach(recipes::add);
            super.apply();
        }
        
        @Override
        protected String getRecipeInfo(LensConversionRecipe recipe) {
            return LogHelper.getStackDescription(recipe.outputStack);
        }
        
    }
    
}