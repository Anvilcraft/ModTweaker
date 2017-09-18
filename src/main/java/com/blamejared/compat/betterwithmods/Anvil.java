package com.blamejared.compat.betterwithmods;


import betterwithmods.common.registry.anvil.AnvilCraftingManager;
import betterwithmods.common.registry.anvil.ShapedAnvilRecipe;
import betterwithmods.common.registry.anvil.ShapelessAnvilRecipe;
import betterwithmods.module.gameplay.AnvilRecipes;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseUndoable;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Iterator;

@ZenClass("mods.betterwithmods.Anvil")
@ModOnly("betterwithmods")
@ZenRegister
public class Anvil {
    
    @ZenMethod
    public static void addShaped(IItemStack output, IIngredient[][] inputs) {
        CraftTweakerAPI.apply(new AddShaped(output, inputs));
    }
    
    @ZenMethod
    public static void addShapeless(IItemStack output, IIngredient[] inputs) {
        CraftTweakerAPI.apply(new AddShapeless(output, inputs));
    }
    
    @ZenMethod
    public static void removeShaped(IItemStack output, @Optional IIngredient[][] ingredients) {
        CraftTweakerAPI.apply(new RemoveShaped(output, ingredients));
    }
    
    @ZenMethod
    public static void removeShapeless(IItemStack output, @Optional IIngredient[] ingredients) {
        CraftTweakerAPI.apply(new RemoveShapeless(output, ingredients));
    }
    
    public static class AddShaped extends BaseUndoable {
        
        private final IItemStack output;
        private final IIngredient[][] ingredients;
        
        public AddShaped(IItemStack output, IIngredient[][] ingredients) {
            super("Add Anvil Shaped Recipe");
            this.output = output;
            this.ingredients = ingredients;
        }
        
        @Override
        public void apply() {
            AnvilRecipes.addSteelShapedRecipe(new ResourceLocation("crafttweaker", this.name), InputHelper.toStack(output), toShapedAnvilObjects(ingredients));
        }
        
        @Override
        protected String getRecipeInfo() {
            return output.getDisplayName();
        }
    }
    
    public static class AddShapeless extends BaseUndoable {
        
        private final IItemStack output;
        private final IIngredient[] ingredients;
        
        public AddShapeless(IItemStack output, IIngredient[] ingredients) {
            super("Add Anvil Shapeless Recipe");
            this.output = output;
            this.ingredients = ingredients;
        }
        
        @Override
        public void apply() {
            AnvilRecipes.addSteelShapelessRecipe(new ResourceLocation("crafttweaker", this.name), InputHelper.toStack(output), InputHelper.toObjects(ingredients));
        }
        
        @Override
        protected String getRecipeInfo() {
            return output.getDisplayName();
        }
    }
    
    public static Object[] toShapedAnvilObjects(IIngredient[][] ingredients) {
        if(ingredients == null)
            return null;
        else {
            ArrayList prep = new ArrayList();
            if(ingredients.length > 0)
                prep.add("abcd");
            if(ingredients.length > 1)
                prep.add("efgh");
            if(ingredients.length > 2)
                prep.add("ijkl");
            if(ingredients.length > 3)
                prep.add("mnop");
            char[][] map = new char[][]{{'a', 'b', 'c', 'd'}, {'e', 'f', 'g', 'h'}, {'i', 'j', 'k', 'l'}, {'m', 'n', 'o', 'p'}};
            for(int x = 0; x < ingredients.length; x++) {
                if(ingredients[x] != null) {
                    for(int y = 0; y < ingredients[x].length; y++) {
                        if(ingredients[x][y] != null && x < map.length && y < map[x].length) {
                            prep.add(map[x][y]);
                            prep.add(InputHelper.toObject(ingredients[x][y]));
                        }
                    }
                }
            }
            return prep.toArray();
        }
    }
    
    public static class RemoveShaped extends BaseUndoable {
        
        private final IItemStack output;
        private final IIngredient[][] ingredients;
        
        protected RemoveShaped(IItemStack output, IIngredient[][] ingredients) {
            super("Remove Shaped Anvil");
            this.output = output;
            this.ingredients = ingredients;
        }
        
        @Override
        public void apply() {
            if(ingredients != null) {
                IRecipe removal = new ShapedAnvilRecipe(new ResourceLocation("crafttweaker", this.name), InputHelper.toStack(output), toShapedAnvilObjects(ingredients));
                for(Iterator<IRecipe> iterator = AnvilCraftingManager.ANVIL_CRAFTING.iterator(); iterator.hasNext(); ) {
                    IRecipe recipe = iterator.next();
                    if(recipe.getRecipeOutput().isItemEqual(removal.getRecipeOutput()) && removal.getIngredients().equals(recipe.getIngredients()))
                        iterator.remove();
                }
            } else {
                for(Iterator<IRecipe> iterator = AnvilCraftingManager.ANVIL_CRAFTING.iterator(); iterator.hasNext(); ) {
                    IRecipe recipe = iterator.next();
                    if(recipe.getRecipeOutput().isItemEqual(InputHelper.toStack(output))) {
                        iterator.remove();
                    }
                }
            }
        }
        
        
        @Override
        protected String getRecipeInfo() {
            return output.getDisplayName();
        }
    }
    
    public static class RemoveShapeless extends BaseUndoable {
        
        private final IItemStack output;
        private final IIngredient[] ingredients;
        
        protected RemoveShapeless(IItemStack output, IIngredient[] ingredients) {
            super("Remove Shapeless Anvil");
            this.output = output;
            this.ingredients = ingredients;
        }
        
        @Override
        public void apply() {
            if(ingredients != null) {
                IRecipe removal = new ShapelessAnvilRecipe(new ResourceLocation("crafttweaker", this.name), InputHelper.toStack(output), InputHelper.toObjects(ingredients));
                for(Iterator<IRecipe> iterator = AnvilCraftingManager.ANVIL_CRAFTING.iterator(); iterator.hasNext(); ) {
                    IRecipe recipe = iterator.next();
                    if(recipe.getRecipeOutput().isItemEqual(removal.getRecipeOutput()) && removal.getIngredients().equals(recipe.getIngredients()))
                        iterator.remove();
                }
            } else {
                for(Iterator<IRecipe> iterator = AnvilCraftingManager.ANVIL_CRAFTING.iterator(); iterator.hasNext(); ) {
                    IRecipe recipe = iterator.next();
                    if(recipe.getRecipeOutput().isItemEqual(InputHelper.toStack(output))) {
                        iterator.remove();
                    }
                }
            }
        }
        
        
        @Override
        protected String getRecipeInfo() {
            return output.getDisplayName();
        }
    }
}
