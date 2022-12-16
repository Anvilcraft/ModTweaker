package modtweaker2.mods.auracore;

import minetweaker.MineTweakerAPI;
import modtweaker2.mods.auracore.handlers.Crucible;
import modtweaker2.utils.TweakerPlugin;

public class AuraCore extends TweakerPlugin {
    
    public AuraCore() {
        MineTweakerAPI.registerClass(Crucible.class);
    }

}
