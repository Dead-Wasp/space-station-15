package org.technocracy.spacestation.mutation.mutations;

import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.technocracy.spacestation.mutation.Mutation;

import static org.technocracy.spacestation.utils.BlockStateReplacer.replaceBlockState;

public class MutationDeath extends Mutation {

    private final BlockState state;

    public MutationDeath(BlockState state) {
        this.state = state;
    }

    @Override
    public boolean apply(Mutation.MutationContext context) {
        BlockState newState = replaceBlockState(this.state, context.world(), context.pos());
        context.world().setBlockState(context.pos(), newState);
        return true;
    }

    @Override
    public void spawnParticles(MutationContext context) {
        ServerWorld world = (ServerWorld) context.world();
        BlockPos pos = context.pos();

        world.spawnParticles(
                ParticleTypes.SMOKE,
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
