package com.blamejared.compat.betterwithmods;


import betterwithmods.common.registry.bulk.manager.CrucibleManager;
import betterwithmods.common.registry.bulk.recipes.CrucibleRecipe;
import com.blamejared.ModTweaker;
import com.blamejared.compat.betterwithmods.util.BulkAdd;
import com.blamejared.compat.betterwithmods.util.BulkRemove;
import com.blamejared.mtlib.helpers.InputHelper;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.betterwithmods.Crucible")
@ModOnly("betterwithmods")
@ZenRegister
public class Crucible {
    
    @ZenMethod
    public static void add(IItemStack output, @Optional IItemStack secondaryOutput, @NotNull IIngredient[] inputs) {
        CrucibleRecipe r = new CrucibleRecipe(InputHelper.toStack(output), InputHelper.toStack(secondaryOutput), InputHelper.toObjects(inputs));
        ModTweaker.LATE_ADDITIONS.add(new BulkAdd("Set Crucible Recipe", CrucibleManager.getInstance(), r));
    }
    
    @ZenMethod
    public static void remove(IItemStack output) {
        ModTweaker.LATE_REMOVALS.add(new BulkRemove("Set Crucible Recipe", CrucibleManager.getInstance(), InputHelper.toStack(output), ItemStack.EMPTY));
    }
    
    @ZenMethod
    public static void remove(IItemStack output, IItemStack secondary, IIngredient[] inputs) {
        ModTweaker.LATE_REMOVALS.add(new BulkRemove("Remove Crucible Recipe", CrucibleManager.getInstance(), InputHelper.toStack(output), secondary != null ? InputHelper.toStack(secondary) : ItemStack.EMPTY, InputHelper.toObjects(inputs)));
    }
}
