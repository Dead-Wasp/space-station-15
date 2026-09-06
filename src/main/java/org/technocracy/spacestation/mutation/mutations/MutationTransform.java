package org.technocracy.spacestation.mutation.mutations;

import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.technocracy.spacestation.mutation.Mutation;

import java.util.List;

import static org.technocracy.spacestation.utils.BlockStateReplacer.replaceBlockState;

public class MutationTransform extends Mutation {

    public record WeightedBlock(BlockState state, double weight) {}

    private final List<WeightedBlock> blocks;
    private final double totalWeight;

    public MutationTransform(List<WeightedBlock> blocks) {
        if (blocks.isEmpty()) {
            throw new IllegalArgumentException("TransformMutation requires at least one block.");
        }

        this.blocks = List.copyOf(blocks);
        this.totalWeight = blocks.stream()
                .mapToDouble(WeightedBlock::weight)
                .sum();

        if (totalWeight <= 0) {
            throw new IllegalArgumentException("Total weight must be greater than zero.");
        }
    }

    private BlockState chooseBlock(Random random) {
        double roll = random.nextDouble() * totalWeight;

        double current = 0.0;
        for (WeightedBlock block : blocks) {
            current += block.weight();
            if (roll <= current) {
                return block.state();
            }
        }

        return blocks.getLast().state();
    }

    @Override
    public boolean apply(MutationContext context) {
        Random random = context.world().getRandom();
        BlockState selected = chooseBlock(random);
        BlockState newState = replaceBlockState(selected, context.world(), context.pos());
        context.world().setBlockState(context.pos(), newState);
        return true;
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

    public List<WeightedBlock> getBlocks() {
        return blocks;
    }
}
