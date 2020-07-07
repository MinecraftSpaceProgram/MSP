package io.github.MinecraftSpaceProgram.MSP.util;

import io.github.MinecraftSpaceProgram.MSP.init.BlockLoader;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class Rocket {
    protected final ArrayList<BlockPos> rocketBlocksPos;
    protected final ArrayList<BlockPos> borderBlocksPos;
    protected final World world;

    public final boolean ROCKET_BORDER_COATED;

    public Rocket(ArrayList<BlockPos> rocketBlocksPos, ArrayList<BlockPos> borderBlocksPos, World world) {
        this.borderBlocksPos = borderBlocksPos;
        this.rocketBlocksPos = rocketBlocksPos;
        this.world = world;

        this.ROCKET_BORDER_COATED = enforceRocketBorderCoated();
    }

    public static Rocket createFromHangar(Hangar hangar, World world) {
        return new RocketBuilder(hangar, world).findRocket();
    }

    public static boolean enforceRocketBorderCoated(World world, ArrayList<BlockPos> borderBlocksPos) {
        for (BlockPos blockPos : borderBlocksPos) {
            if (world.getBlockState(blockPos).getBlock() != BlockLoader.ROCKET_GENERIC.get())
                return false;
        }
        return true;
    }

    public boolean enforceRocketBorderCoated() {
        return enforceRocketBorderCoated(this.world, this.borderBlocksPos);
    }

    public ArrayList<BlockPos> getBorderBlocks() {
        return this.borderBlocksPos;
    }

    public ArrayList<BlockPos> getRocketBlocks() {
        return this.rocketBlocksPos;
    }
}
