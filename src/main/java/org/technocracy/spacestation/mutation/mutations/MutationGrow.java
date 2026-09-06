package org.technocracy.spacestation.mutation.mutations;

import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.technocracy.spacestation.mutation.Mutation;

public class MutationGrow extends Mutation {

    @Override
    public boolean canApply(MutationContext context) {
        if (!(context.world() instanceof ServerWorld serverWorld)) {
            return false;
        }

        BlockPos pos = context.pos();
        BlockState state = serverWorld.getBlockState(pos);

        if (!(state.getBlock() instanceof Fertilizable fertilizable)) {
            return false;
        }

        return fertilizable.isFertilizable(serverWorld, pos, state);
    }

    @Override
    public boolean apply(MutationContext context) {
        if (!(context.world() instanceof ServerWorld serverWorld)) {
            return false;
        }

        BlockPos pos = context.pos();
        boolean applied = false;

        while (true) {
            BlockState state = serverWorld.getBlockState(pos);

            if (!(state.getBlock() instanceof Fertilizable fertilizable)) {
                break;
            }

            if (!fertilizable.isFertilizable(serverWorld, pos, state)) {
                break;
            }

            fertilizable.grow(
                    serverWorld,
                    serverWorld.getRandom(),
                    pos,
                    state
            );
            applied = true;
        }

        return applied;
    }

    @Override
    public void spawnParticles(MutationContext context) {
        ServerWorld world = (ServerWorld) context.world();
        BlockPos pos = context.pos();

        world.spawnParticles(
                ParticleTypes.HAPPY_VILLAGER,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                8,
                0.3,
                0.5,
                0.3,
                0.1
        );
    }
}
