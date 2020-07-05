package io.github.MinecraftSpaceProgram.MSP.util;

import io.github.MinecraftSpaceProgram.MSP.init.BlockInit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class HangarBuilder {
    protected final World world;
    protected final BlockPos startingBlockPos;
    private HangarBuilderCorners hangarBuilderCorners;

    private static final int horizontal_limit = 256;
    private static final int vertical_limit = 256;

    public HangarBuilder(World world, BlockPos startingBlockPos) {
        this.world = world;
        this.startingBlockPos = startingBlockPos;

        int x = FindAlongX();
        if (x != 0) {
            int y = FindAlongY();
            if (y != 0) {
                int z = FindAlongZ();
                if (z != 0) {
                    if (CheckXYZ(x, y, z)) {
                        hangarBuilderCorners = new HangarBuilderCorners(startingBlockPos, x, y, z);
                    }
                }
            }
        }
    }

    private int FindAlongX() {
        for (int dx = 1; dx < horizontal_limit; dx++) {
            if (world.getBlockState(new BlockPos(startingBlockPos.getX() + dx, startingBlockPos.getY(), startingBlockPos.getZ())).getBlock() == BlockInit.HANGAR_CORNER.get()) {
                return dx;
            }
        }
        for (int dx = -1; dx > -horizontal_limit; dx--) {
            if (world.getBlockState(new BlockPos(startingBlockPos.getX() + dx, startingBlockPos.getY(), startingBlockPos.getZ())).getBlock() == BlockInit.HANGAR_CORNER.get()) {
                return dx;
            }
        }
        return 0;
    }

    private int FindAlongY() {
        int y = startingBlockPos.getY();
        for (int dy = 1; dy < vertical_limit && y + dy < 256; dy++) {
            if (world.getBlockState(new BlockPos(startingBlockPos.getX(), startingBlockPos.getY() + dy, startingBlockPos.getZ())).getBlock() == BlockInit.HANGAR_CORNER.get()) {
                return dy;
            }
        }
        for (int dy = 1; dy > -vertical_limit && y + dy > 0; dy--) {
            if (world.getBlockState(new BlockPos(startingBlockPos.getX(), startingBlockPos.getY() + dy, startingBlockPos.getZ())).getBlock() == BlockInit.HANGAR_CORNER.get()) {
                return dy;
            }
        }
        return 0;
    }

    private int FindAlongZ() {
        int z = startingBlockPos.getZ();
        for (int dz = 1; dz < vertical_limit; dz++) {
            if (world.getBlockState(new BlockPos(startingBlockPos.getX(), startingBlockPos.getY(), startingBlockPos.getZ() + dz)).getBlock() == BlockInit.HANGAR_CORNER.get()) {
                return dz;
            }
        }
        for (int dz = -1; dz > -vertical_limit; dz--) {
            if (world.getBlockState(new BlockPos(startingBlockPos.getX(), startingBlockPos.getY(), startingBlockPos.getZ() + dz)).getBlock() == BlockInit.HANGAR_CORNER.get()) {
                return dz;
            }
        }
        return 0;
    }

    private boolean CheckXYZ(int x, int y, int z) {
        boolean xy = world.getBlockState(new BlockPos(startingBlockPos.getX() + x, startingBlockPos.getY() + y, startingBlockPos.getZ())).getBlock() == BlockInit.HANGAR_CORNER.get();
        boolean zx = world.getBlockState(new BlockPos(startingBlockPos.getX() + x, startingBlockPos.getY(), startingBlockPos.getZ() + z)).getBlock() == BlockInit.HANGAR_CORNER.get();
        boolean zy = world.getBlockState(new BlockPos(startingBlockPos.getX(), startingBlockPos.getY() + y, startingBlockPos.getZ() + z)).getBlock() == BlockInit.HANGAR_CORNER.get();
        boolean zxy = world.getBlockState(new BlockPos(startingBlockPos.getX() + x, startingBlockPos.getY() + y, startingBlockPos.getZ() + z)).getBlock() == BlockInit.HANGAR_CORNER.get();

        return xy && zx && zy && zxy;
    }

    @Nullable
    public HangarBuilderCorners getCorners() {
        return hangarBuilderCorners;
    }
}
