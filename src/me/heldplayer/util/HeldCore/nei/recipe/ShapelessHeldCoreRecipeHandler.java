
package me.heldplayer.util.HeldCore.nei.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.heldplayer.util.HeldCore.crafting.ShapelessHeldCoreRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import codechicken.core.gui.GuiDraw;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.ShapedRecipeHandler;

@SuppressWarnings("unchecked")
public class ShapelessHeldCoreRecipeHandler extends ShapedRecipeHandler {

    public int[][] stackorder = new int[][] { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 }, { 0, 2 }, { 1, 2 }, { 2, 0 }, { 2, 1 }, { 2, 2 } };

    public class CachedShapelessRecipe extends CachedRecipe {

        public ArrayList<PositionedStack> ingredients;
        public List<ItemStack> input;
        public PositionedStack result;
        public ShapelessHeldCoreRecipe recipe;

        public CachedShapelessRecipe(ShapelessHeldCoreRecipe recipe) {
            this.recipe = recipe;
            this.ingredients = new ArrayList<PositionedStack>();
            this.input = new ArrayList<ItemStack>();
            this.setResult(recipe.getOutput());
            this.setIngredients(recipe);
        }

        public void setIngredients(List<List<ItemStack>> items) {
            this.ingredients.clear();
            this.input.clear();
            for (int ingred = 0; ingred < items.size(); ingred++) {
                PositionedStack stack = new PositionedStack(items.get(ingred), 25 + ShapelessHeldCoreRecipeHandler.this.stackorder[ingred][0] * 18, 6 + ShapelessHeldCoreRecipeHandler.this.stackorder[ingred][1] * 18);
                stack.setMaxSize(1);
                this.ingredients.add(stack);
            }

            this.getCycledIngredients(ShapelessHeldCoreRecipeHandler.this.cycleticks / 20, this.ingredients);
            this.setResult(this.recipe.handler.getOutput(this.recipe, this.input));
        }

        public void setIngredients(ShapelessHeldCoreRecipe recipe) {
            ArrayList<List<ItemStack>> items = recipe.ingredients;

            this.setIngredients(items);
        }

        public void setResult(ItemStack output) {
            if (this.result != null) {
                this.result.item = output;
                this.result.items = new ItemStack[] { output };
            }
            else {
                this.result = new PositionedStack(output, 119, 24);
            }
        }

        @Override
        public ArrayList<PositionedStack> getIngredients() {
            return this.ingredients;
        }

        @Override
        public PositionedStack getResult() {
            return this.result;
        }

        // Strangeness @Override
        public ArrayList<PositionedStack> getCycledIngredients(int cycle, ArrayList<PositionedStack> ingredients) {
            ArrayList<PositionedStack> result = (ArrayList<PositionedStack>) super.getCycledIngredients(cycle, ingredients);

            this.input.clear();

            for (PositionedStack stack : result) {
                this.input.add(stack.item);
            }

            return result;
        }

        @Override
        public void setIngredientPermutation(Collection<PositionedStack> ingredients, ItemStack ingredient) {
            for (PositionedStack stack : ingredients) {
                for (int i = 0; i < stack.items.length; i++) {
                    if (NEIServerUtils.areStacksSameTypeCrafting(ingredient, stack.items[i])) {
                        stack.item = ingredient;
                        stack.item.setItemDamage(ingredient.getItemDamage());
                        stack.items = new ItemStack[] { stack.item };
                        stack.setPermutationToRender(0);
                        break;
                    }
                }
            }
        }

    }

    @Override
    public String getRecipeName() {
        return NEIClientUtils.translate("recipe.shapeless.heldcore");
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("crafting") && this.getClass() == ShapelessHeldCoreRecipeHandler.class) {
            List<IRecipe> allrecipes = CraftingManager.getInstance().getRecipeList();
            for (IRecipe irecipe : allrecipes) {
                CachedShapelessRecipe recipe = null;
                if (irecipe instanceof ShapelessHeldCoreRecipe) {
                    recipe = new CachedShapelessRecipe((ShapelessHeldCoreRecipe) irecipe);
                }

                if (recipe == null) {
                    continue;
                }

                this.arecipes.add(recipe);
            }
        }
        else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        List<IRecipe> allrecipes = CraftingManager.getInstance().getRecipeList();
        for (IRecipe irecipe : allrecipes) {
            if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getRecipeOutput(), result)) {
                CachedShapelessRecipe recipe = null;
                if (irecipe instanceof ShapelessHeldCoreRecipe) {
                    recipe = new CachedShapelessRecipe((ShapelessHeldCoreRecipe) irecipe);
                }
                if (recipe == null) {
                    continue;
                }
                this.arecipes.add(recipe);
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        List<IRecipe> allrecipes = CraftingManager.getInstance().getRecipeList();
        for (IRecipe irecipe : allrecipes) {
            CachedShapelessRecipe recipe = null;
            if (irecipe instanceof ShapelessHeldCoreRecipe) {
                recipe = new CachedShapelessRecipe((ShapelessHeldCoreRecipe) irecipe);
            }
            if (recipe == null) {
                continue;
            }
            if (recipe.contains(recipe.ingredients, ingredient)) {
                recipe.setIngredientPermutation(recipe.ingredients, ingredient);
                this.arecipes.add(recipe);
            }
        }
    }

    @Override
    public boolean isRecipe2x2(int recipe) {
        return this.getIngredientStacks(recipe).size() <= 4;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!NEIClientUtils.shiftKey() && this.cycleticks % 20 == 0) {
            for (CachedRecipe cachedRecipe : this.arecipes) {
                CachedShapelessRecipe recipe = (CachedShapelessRecipe) cachedRecipe;

                recipe.getCycledIngredients(this.cycleticks / 20, recipe.ingredients);

                recipe.setResult(recipe.recipe.handler.getOutput(recipe.recipe, recipe.input));
            }
        }
    }

    @Override
    public void drawExtras(int recipeId) {
        super.drawExtras(recipeId);
        CachedShapelessRecipe recipe = (CachedShapelessRecipe) this.arecipes.get(recipeId);

        if (recipe != null && recipe.recipe != null && recipe.recipe.handler != null) {
            GuiDraw.drawStringC(recipe.recipe.handler.getOwningModName(), 124   , 8, 0x404040, false);
        }
    }

}