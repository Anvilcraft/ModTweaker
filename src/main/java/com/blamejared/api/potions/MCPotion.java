package com.blamejared.api.potions;

import minetweaker.mc1112.brackets.EntityBracketHandler;
import net.minecraft.potion.Potion;

public class MCPotion implements IPotion {
    
    private final Potion potion;
    
    public MCPotion(Potion potion) {
        this.potion = potion;
    }
    
    @Override
    public String name() {
        return potion.getName();
    }
    
    @Override
    public boolean isBadEffect() {
        return potion.isBadEffect();
    }
    
    @Override
    public int getLiquidColor() {
        return potion.getLiquidColor();
    }
    
    @Override
    public Object getInternal() {
        return potion;
    }
}
