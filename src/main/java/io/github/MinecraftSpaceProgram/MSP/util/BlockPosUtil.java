package io.github.MinecraftSpaceProgram.MSP.util;

import net.minecraft.util.math.BlockPos;

public final class BlockPosUtil {
    public static BlockPos[] getRectangleOutsideBorders(BlockPos cornerPos, int x, int z) {
        BlockPos[] border = new BlockPos[2 * (x + z + 5)];
        int i = 0;
        for (int dx = -1; dx < x + 2; ++dx) {
            border[i] = cornerPos.add(dx,0,-1);
            border[i + 1] = cornerPos.add(dx, 0, z + 1);
            i += 2;
        }
        for (int dz = 0; dz < z + 1; ++dz) {
            border[i] = cornerPos.add(-1, 0, dz);
            border[i + 1] = cornerPos.add(x + 1, 0, dz);
            i += 2;
        }
        return border;
    }
    
    public static BlockPos[] getCubeBorder(BlockPos cornerPos, int x, int y, int z) {
        final BlockPos[] borderBlocks = new BlockPos[2 * (x * y + x * z + y * z + 1)];
        int i = 0;

        for (int dx = 0; dx <= x; ++dx) {
            for (int dy = 0; dy <= y; ++dy) {
                borderBlocks[i] = cornerPos.add(dx,dy,0);
                ++i;
            }
        }
        for (int dx = 1; dx < x; ++dx) {
            for (int dz = 1; dz < z; ++dz) {
                borderBlocks[i] = cornerPos.add(dx,0,dz);
                ++i;
            }
        }
        for (int dz = 1; dz < z; ++dz) {
            for (int dy = 0; dy <= y; ++dy) {
                borderBlocks[i] = cornerPos.add(0,dy,dz);
                ++i;
            }
        }
        for (int dx = 0; dx <= x; ++dx) {
            for (int dy = 0; dy <= y; ++dy) {
                borderBlocks[i] = cornerPos.add(dx,dy,z);
                ++i;
            }
        }
        for (int dx = 1; dx < x; ++dx) {
            for (int dz = 1; dz < z; ++dz) {
                borderBlocks[i] = cornerPos.add(dx,y,dz);
                ++i;
            }
        }
        for (int dz = 1; dz < z; ++dz) {
            for (int dy = 0; dy <= y; ++dy) {
                borderBlocks[i] = cornerPos.add(x,dy,dz);
                ++i;
            }
        }

        return borderBlocks;
    }
    
    public static BlockPos[] getCube(BlockPos cornerPos, int x, int y, int z) {
        BlockPos[] insideBlocks = new BlockPos[(x + 1) * (y + 1) * (z + 1)];
        int i = 0;
        for (int dx = 0; dx <= x; ++dx) {
            for (int dy = 0; dy <= y; ++dy) {
                for (int dz = 0; dz <= z; ++dz) {
                    insideBlocks[i] = cornerPos.add(dx,dy,dz);
                    ++i;
                }
            }
        }
        return insideBlocks;
    }
}
