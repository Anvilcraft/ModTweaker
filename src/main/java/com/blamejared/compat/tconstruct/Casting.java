package com.blamejared.compat.tconstruct;

import com.blamejared.ModTweaker;
import com.blamejared.compat.tconstruct.recipes.CastingRecipeTweaker;
import com.blamejared.mtlib.helpers.*;
import com.blamejared.mtlib.utils.BaseUndoable;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.*;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.events.TinkerRegisterEvent;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.*;

import java.util.*;

@ZenClass("mods.tconstruct.Casting")
@ZenRegister
@ModOnly("tconstruct")
public class Casting {
    
    public static final Map<IItemStack, ILiquidStack> REMOVED_RECIPES_TABLE = new LinkedHashMap<>();
    public static final Map<IItemStack, ILiquidStack> REMOVED_RECIPES_BASIN = new LinkedHashMap<>();
    
    private static boolean init = false;
    
    private static void init() {
        if(!init) {
            MinecraftForge.EVENT_BUS.register(new Casting());
            init = true;
        }
    }
    
    @ZenMethod
    public static void addTableRecipe(IItemStack output, IItemStack cast, ILiquidStack fluid, int amount, @Optional boolean consumeCast, @Optional int time) {
        init();
        ModTweaker.LATE_ADDITIONS.add(new Add(InputHelper.toStack(output), InputHelper.toStack(cast), InputHelper.toFluid(fluid), amount, true, consumeCast, time));
    }
    
    @ZenMethod
    public static void addBasinRecipe(IItemStack output, IItemStack cast, ILiquidStack fluid, int amount, @Optional boolean consumeCast, @Optional int time) {
        init();
        ModTweaker.LATE_ADDITIONS.add(new Add(InputHelper.toStack(output), InputHelper.toStack(cast), InputHelper.toFluid(fluid), amount, false, consumeCast, time));
    }
    
    @ZenMethod
    public static void removeTableRecipe(IItemStack output, @Optional ILiquidStack input) {
        init();
        CraftTweakerAPI.apply(new Remove(output, input, true));
    }
    
    @ZenMethod
    public static void removeBasinRecipe(IItemStack output, @Optional ILiquidStack input) {
        init();
        CraftTweakerAPI.apply(new Remove(output, input, false));
    }
    
    private static class Remove extends BaseUndoable {
        
        private IItemStack output;
        private ILiquidStack input;
        private boolean table;
        
        protected Remove(IItemStack output, ILiquidStack input, boolean table) {
            super("Casting");
            this.output = output;
            this.input = input;
            this.table = table;
        }
        
        @Override
        public void apply() {
            if(table)
                REMOVED_RECIPES_TABLE.put(output, input);
            else
                REMOVED_RECIPES_BASIN.put(output, input);
        }
        
        @Override
        protected String getRecipeInfo() {
            return LogHelper.getStackDescription(output);
        }
    }
    
    @SubscribeEvent
    public void onTinkerRegister(TinkerRegisterEvent.TableCastingRegisterEvent event) {
        if(event.getRecipe() instanceof CastingRecipeTweaker || !(event.getRecipe() instanceof CastingRecipe)) {
            return;
        }
        for(Map.Entry<IItemStack, ILiquidStack> ent : REMOVED_RECIPES_TABLE.entrySet()) {
            IItemStack stack = ent.getKey();
            if(stack == null || ((CastingRecipe) event.getRecipe()).getResult() == null) {
                continue;
            }
            if(ent.getValue() == null) {
                if(stack.matches(InputHelper.toIItemStack(((CastingRecipe) event.getRecipe()).getResult()))) {
                    event.setCanceled(true);
                }
            } else {
                if(stack.matches(InputHelper.toIItemStack(((CastingRecipe) event.getRecipe()).getResult())) && ((CastingRecipe) event.getRecipe()).getFluid().isFluidEqual(((FluidStack) ent.getValue().getInternal()))) {
                    event.setCanceled(true);
                }
            }
            
        }
        
    }
    
    @SubscribeEvent
    public void onTinkerRegister(TinkerRegisterEvent.BasinCastingRegisterEvent event) {
        if(event.getRecipe() instanceof CastingRecipeTweaker || !(event.getRecipe() instanceof CastingRecipe)) {
            return;
        }
        for(Map.Entry<IItemStack, ILiquidStack> ent : REMOVED_RECIPES_BASIN.entrySet()) {
            IItemStack stack = ent.getKey();
            if(stack == null || ((CastingRecipe) event.getRecipe()).getResult() == null) {
                continue;
            }
            
            if(stack.matches(InputHelper.toIItemStack(((CastingRecipe) event.getRecipe()).getResult()))) {
                if(ent.getValue() != null) {
                    if(((CastingRecipe) event.getRecipe()).getFluid().isFluidEqual(((FluidStack) ent.getValue().getInternal()))) {
                        event.setCanceled(true);
                    }
                } else
                    event.setCanceled(true);
            }
            
            
        }
        
    }
    
    private static class Add extends BaseUndoable {
        
        private ItemStack output, cast;
        private FluidStack fluid;
        private int amount;
        private boolean table;
        private boolean consumeCast;
        private int time;
        
        public Add(ItemStack output, ItemStack cast, FluidStack fluid, int amount, boolean table, boolean consumeCast, int time) {
            super("Casting");
            this.output = output;
            this.cast = cast;
            this.fluid = fluid;
            this.amount = amount;
            this.table = table;
            this.consumeCast = consumeCast;
            this.time = (time != 0) ? time : CastingRecipe.calcCooldownTime(fluid.getFluid(), amount);
        }
        
        @Override
        public void apply() {
            RecipeMatch rm = null;
            if(cast != ItemStack.EMPTY) {
                rm = RecipeMatch.ofNBT(cast);
            }
            if(table)
                TinkerRegistry.registerTableCasting(new CastingRecipeTweaker(output, rm, new FluidStack(fluid, amount), time, consumeCast, false));
            else
                TinkerRegistry.registerBasinCasting(new CastingRecipeTweaker(output, rm, new FluidStack(fluid, amount), time, consumeCast, false));
        }
        
        @Override
        protected String getRecipeInfo() {
            return LogHelper.getStackDescription(output);
        }
    }
}
