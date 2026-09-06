package org.technocracy.spacestation.mutation.mutations;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.technocracy.spacestation.mutation.Mutation;

public class MutationNothing extends Mutation {

    @Override
    public boolean apply(MutationContext context) {
        // Literally nothing
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
