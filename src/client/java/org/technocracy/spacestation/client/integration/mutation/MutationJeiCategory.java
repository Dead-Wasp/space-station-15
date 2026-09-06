package org.technocracy.spacestation.client.integration.mutation;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class MutationJeiCategory implements IRecipeCategory<MutationJeiRecipe> {
    private final IDrawable icon;
    private final RecipeType<MutationJeiRecipe> type;
    private final String titleKey;

    public MutationJeiCategory(
            IGuiHelper guiHelper,
            RecipeType<MutationJeiRecipe> type,
            String titleKey,
            Item iconItem
    ) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(iconItem));
        this.type = type;
        this.titleKey = titleKey;
    }

    @Override
    public RecipeType<MutationJeiRecipe> getRecipeType() {
        return type;
    }

    @Override
    public Text getTitle() {
        return Text.translatable(titleKey);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 160;
    }

    @Override
    public int getHeight() {
        return 80;
    }

    @Override
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            MutationJeiRecipe recipe,
            IFocusGroup focuses
    ) {
        builder.addInputSlot(8, 24)
                .addItemStack(recipe.input());

        for (int i = 0; i < recipe.outputs().size(); i++) {
            builder.addOutputSlot(
                    112 + (i % 2) * 24,
                    24 + (i / 2) * 24
            ).addItemStack(recipe.outputs().get(i));
        }
    }
}