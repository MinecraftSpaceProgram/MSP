package io.github.MinecraftSpaceProgram.MSP.util;

import io.github.MinecraftSpaceProgram.MSP.init.BlockInit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class HangarBuilder {
    protected final World world;
    protected final BlockPos startingBlockPos;
    private Hangar hangar;

    private static final int horizontal_limit = 256;
    private static final int vertical_limit = 256;

    public HangarBuilder(final World world, final BlockPos startingBlockPos) {
        this.world = world;
        this.startingBlockPos = startingBlockPos;

        int x = FindAlongX();
        if (x != 0) {
            int y = FindAlongY();
            if (y != 0) {
                int z = FindAlongZ();
                if (z != 0) {
                    if (CheckXYZ(x, y, z)) {
                        hangar = new Hangar(startingBlockPos, x, y, z);
                    }
                }
            }
        }
    }

    /**
     * Find corner along axis X, up to horizontal_limit (256) blocks away
     */
    private int FindAlongX() {
        for (int dx = 1; dx < horizontal_limit; dx++) {
            if (world.getBlockState(new BlockPos(startingBlockPos.getX() + dx, startingBlockPos.getY(), startingBlockPos.getZ())).getBlock() == BlockInit.HANGAR_CORNER.get())
                return dx;
            if (world.getBlockState(new BlockPos(startingBlockPos.getX() - dx, startingBlockPos.getY(), startingBlockPos.getZ())).getBlock() == BlockInit.HANGAR_CORNER.get())
                return -dx;
        }
        return 0;
    }

    /**
     * Find corner along axis Y, checks for 0 < y + dy < 256
     */
    private int FindAlongY() {
        int y = startingBlockPos.getY();
        for (int dy = 1; dy < vertical_limit; dy++) {
            if (y + dy < 256 && world.getBlockState(new BlockPos(startingBlockPos.getX(), startingBlockPos.getY() + dy, startingBlockPos.getZ())).getBlock() == BlockInit.HANGAR_CORNER.get())
                return dy;
            if (y - dy > 0 && world.getBlockState(new BlockPos(startingBlockPos.getX(), startingBlockPos.getY() - dy, startingBlockPos.getZ())).getBlock() == BlockInit.HANGAR_CORNER.get())
                return -dy;
        }
        return 0;
    }

    /**
     * Find corner along axis Z, up to horizontal_limit (256) blocks away
     */
    private int FindAlongZ() {
        int z = startingBlockPos.getZ();
        for (int dz = 1; dz < horizontal_limit; dz++) {
            if (world.getBlockState(new BlockPos(startingBlockPos.getX(), startingBlockPos.getY(), startingBlockPos.getZ() + dz)).getBlock() == BlockInit.HANGAR_CORNER.get())
                return dz;
            if (world.getBlockState(new BlockPos(startingBlockPos.getX(), startingBlockPos.getY(), startingBlockPos.getZ() - dz)).getBlock() == BlockInit.HANGAR_CORNER.get())
                return -dz;
        }
        return 0;
    }

    /**
     * Having 5 corners, check the remaining 3
     */
    private boolean CheckXYZ(final int x, final int y, final int z) {
        boolean xy = world.getBlockState(new BlockPos(startingBlockPos.getX() + x, startingBlockPos.getY() + y, startingBlockPos.getZ())).getBlock() == BlockInit.HANGAR_CORNER.get();
        boolean zx = world.getBlockState(new BlockPos(startingBlockPos.getX() + x, startingBlockPos.getY(), startingBlockPos.getZ() + z)).getBlock() == BlockInit.HANGAR_CORNER.get();
        boolean zy = world.getBlockState(new BlockPos(startingBlockPos.getX(), startingBlockPos.getY() + y, startingBlockPos.getZ() + z)).getBlock() == BlockInit.HANGAR_CORNER.get();
        boolean zxy = world.getBlockState(new BlockPos(startingBlockPos.getX() + x, startingBlockPos.getY() + y, startingBlockPos.getZ() + z)).getBlock() == BlockInit.HANGAR_CORNER.get();

        return xy && zx && zy && zxy;
    }

    @Nullable
    public Hangar getCorners() {
        return hangar;
    }
}
