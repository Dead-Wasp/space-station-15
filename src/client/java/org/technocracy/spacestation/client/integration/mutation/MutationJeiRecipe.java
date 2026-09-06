package org.technocracy.spacestation.client.integration.mutation;

import net.minecraft.item.ItemStack;

import java.util.List;

public record MutationJeiRecipe(
        ItemStack input,
        List<ItemStack> outputs,
        List<Double> weights
) {
}