package org.technocracy.spacestation.utils;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockStateReplacer {

    public static BlockState replaceBlockState(BlockState newState, World world, BlockPos pos) {
        BlockState oldState = world.getBlockState(pos);

        for (Property<?> property : oldState.getProperties()) {
            if (!newState.contains(property)) {
                continue;
            }

            newState = copyProperty(oldState, newState, property);
        }
        return newState;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(
            BlockState oldState,
            BlockState newState,
            Property<T> property
    ) {
        T value = oldState.get(property);

        if (property.getValues().contains(value)) {
            return newState.with(property, value);
        }

        return newState;
    }

}
