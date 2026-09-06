package org.technocracy.spacestation.mutation;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class Mutation {

    public record MutationContext(World world, BlockPos pos) {}

    public abstract boolean apply(MutationContext context);

    public abstract void spawnParticles(MutationContext context);

    public boolean canApply(MutationContext context) {
        return true;
    }

}