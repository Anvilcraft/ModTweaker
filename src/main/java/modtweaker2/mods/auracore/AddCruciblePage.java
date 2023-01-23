package modtweaker2.mods.auracore;

import dev.tilera.auracore.api.AuracoreRecipes;
import dev.tilera.auracore.api.crafting.CrucibleRecipe;
import dev.tilera.auracore.api.research.ResearchPageCrucible;
import minetweaker.IUndoableAction;
import modtweaker2.mods.thaumcraft.ThaumcraftHelper;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchPage;

public class AddCruciblePage implements IUndoableAction {
    String key;
    String tab;
    ResearchPage page;
    ResearchPage[] oldPages;
    String recipeKey;
    
    public AddCruciblePage(String research, String key) {
        this.key = research;
        this.tab = ThaumcraftHelper.getResearchTab(this.key);
        this.recipeKey = key;
    }

    @Override
    public void apply() {
        for (CrucibleRecipe rec : AuracoreRecipes.getCrucibleRecipes()) {
            if (rec.key.equals(recipeKey)) {
                page = new ResearchPageCrucible(rec);
                break;
            }
        }
        if (page == null) return;
        oldPages = ResearchCategories.researchCategories.get(tab).research.get(key).getPages();
        if (oldPages == null) oldPages = new ResearchPage[0];
        ResearchPage[] newPages = new ResearchPage[oldPages.length + 1];
        for (int x = 0; x < oldPages.length; x++) {
            newPages[x] = oldPages[x];
        }
        newPages[oldPages.length] = page;
        ResearchCategories.researchCategories.get(tab).research.get(key).setPages(newPages);
    }

    @Override
    public String describe() {
        return "Adding Research Page to " + key;
    }

    @Override
    public boolean canUndo() {
        return oldPages != null;
    }

    @Override
    public void undo() {
        ResearchCategories.researchCategories.get(tab).research.get(key).setPages(oldPages);
    }

    @Override
    public String describeUndo() {
        return "Removing Page from " + key;
    }

    @Override
    public String getOverrideKey() {
        return null;
    }

}
