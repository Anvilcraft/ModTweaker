package com.blamejared.compat.betterwithmods;


import betterwithmods.common.BWRegistry;
import betterwithmods.common.registry.bulk.manager.CraftingManagerBulk;
import betterwithmods.common.registry.bulk.recipes.CookingPotRecipe;
import com.blamejared.compat.betterwithmods.base.bulkrecipes.CookingPotBuilder;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.function.Supplier;

@ZenClass("mods.betterwithmods.Cauldron")
@ModOnly("betterwithmods")
@ZenRegister
public class Cauldron extends CookingPotBuilder {

    public static Cauldron INSTANCE = new Cauldron(() -> BWRegistry.CAULDRON, "Cauldron");

    protected Cauldron(Supplier<CraftingManagerBulk<CookingPotRecipe>> registry, String name) {
        super(registry, name);
    }

    @ZenMethod
    public static Cauldron builder() {
        return INSTANCE;
    }

    @ZenMethod
    public static void addStoked(IIngredient[] inputs, IItemStack[] outputs) {
        INSTANCE.buildRecipe(inputs, outputs).setHeat(STOKED).build();
    }

    @ZenMethod
    public static void addUnstoked(IIngredient[] inputs, IItemStack[] outputs) {
        INSTANCE.buildRecipe(inputs, outputs).setHeat(UNSTOKED).build();
    }

    @Deprecated
    @ZenMethod
    public static void add(IItemStack output, IItemStack secondaryOutput, @NotNull IIngredient[] inputs) {
        addUnstoked(inputs, new IItemStack[]{output, secondaryOutput});
    }

    @Deprecated
    @ZenMethod
    public static void add(IItemStack output, @NotNull IIngredient[] inputs) {
        addUnstoked(inputs, new IItemStack[]{output});
    }

    @ZenMethod
    public static void remove(IItemStack[] output) {
        INSTANCE.removeRecipe(output);
    }
}
