package com.blamejared.compat.tconstruct.actions;

import com.blamejared.brackets.util.IMaterial;
import minetweaker.IUndoableAction;
import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;

/**
 * Created by Rinart73 on 7/25/2017.
 */
public class SetDrawspeedAction implements IUndoableAction {

    private final IMaterial material;
    private final String stat;
    private final float newValue;
    private final float oldValue;

    public SetDrawspeedAction(IMaterial material, String stat, float newValue) {
        this.material = material;
        this.stat = stat;
        this.newValue = newValue;
        this.oldValue = ((BowMaterialStats) ((Material) material.getInternal()).getStats(stat)).drawspeed;
    }

    private void set(Material material, String stat, float drawspeed) {
        IMaterialStats oldStat = material.getStats(stat);
        if (oldStat instanceof BowMaterialStats) {
            BowMaterialStats bowStat = (BowMaterialStats) oldStat;
            BowMaterialStats newBow = new BowMaterialStats(drawspeed, bowStat.range, bowStat.bonusDamage);
            material.addStats(newBow);
        }
    }

    @Override
    public void apply() {
        set((Material) material.getInternal(), stat, newValue);
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public void undo() {
        set((Material) material.getInternal(), stat, oldValue);
    }

    @Override
    public String describe() {
        return "Setting Drawspeed of " + material.getName() + " to " + newValue + " for " + stat;
    }

    @Override
    public String describeUndo() {
        return "Reverting Drawspeed of " + material.getName() + " to " + oldValue + " for " + stat;
    }

    @Override
    public Object getOverrideKey() {
        return null;
    }
}