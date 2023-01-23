package modtweaker2.mods.auracore.handlers;

import minetweaker.MineTweakerAPI;
import modtweaker2.mods.auracore.AddCruciblePage;
import modtweaker2.mods.auracore.AddInfusionPage;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.auracore.Research")
public class Research {
    
    @ZenMethod
	public static void addInfusionPage(String research, String recipe) {
		MineTweakerAPI.apply(new AddInfusionPage(research, recipe));
	}

    @ZenMethod
	public static void addCruciblePage(String research, String recipe) {
		MineTweakerAPI.apply(new AddCruciblePage(research, recipe));
	}

}
