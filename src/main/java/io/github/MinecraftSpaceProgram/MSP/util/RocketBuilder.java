package io.github.MinecraftSpaceProgram.MSP.util;

import io.github.MinecraftSpaceProgram.MSP.Main;
import io.github.MinecraftSpaceProgram.MSP.init.BlockLoader;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class RocketBuilder {
    protected final World world;
    protected ArrayList<BlockPos> rocket_blocks = new ArrayList<>();

    public RocketBuilder(World world, BlockPos startingBlockPos) {
        this.world = world;

        FindBlocks(startingBlockPos);

        Main.LOGGER.debug(String.format("Rocket in construction : found %d connected blocks", rocket_blocks.size()));
    }

    private void FindBlocks(BlockPos blockPos) {
        int[] bogey = new int[] {-1,1};
        for (int delta : bogey) {
            AddBlocks(new BlockPos(
                    blockPos.getX() + delta,
                    blockPos.getY(),
                    blockPos.getZ()
            ));
            AddBlocks(new BlockPos(
                    blockPos.getX(),
                    blockPos.getY() + delta,
                    blockPos.getZ()
            ));
            AddBlocks(new BlockPos(
                    blockPos.getX(),
                    blockPos.getY(),
                    blockPos.getZ() + delta
            ));
        }
    }

    private void AddBlocks(BlockPos blockPos) {
        if (world.getBlockState(blockPos).getBlock() == BlockLoader.rocket_generic) {
            if (!rocket_blocks.contains(blockPos)) {
                rocket_blocks.add(blockPos);
                FindBlocks(blockPos);
            }
        }
    }

    public ArrayList<BlockPos> getBlocks () {
        return rocket_blocks;
    }
}
