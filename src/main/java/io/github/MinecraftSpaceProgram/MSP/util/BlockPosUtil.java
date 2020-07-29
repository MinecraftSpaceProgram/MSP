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
    
    public static BlockPos[] getCubeInsideBorders(BlockPos cornerPos, int x, int y, int z) {
        final BlockPos[] borderBlocks = new BlockPos[2 * (x * y + x * z + y * z - 4 * (x + y + z) - 3)];
        int i = 0;

        for (int dx = 1; dx < x; ++dx) {
            for (int dy = 1; dy < y; ++dy) {
                borderBlocks[i] = cornerPos.add(dx,dy,1);
                ++i;
            }
        }
        for (int dx = 2; dx < x - 1; ++dx) {
            for (int dz = 2; dz < z - 1; ++dz) {
                borderBlocks[i] = cornerPos.add(dx,1,dz);
                ++i;
            }
        }
        for (int dz = 2; dz < z - 1; ++dz) {
            for (int dy = 1; dy < y; ++dy) {
                borderBlocks[i] = cornerPos.add(1,dy,dz);
                ++i;
            }
        }
        for (int dx = 1; dx < x; ++dx) {
            for (int dy = 1; dy < y; ++dy) {
                borderBlocks[i] = cornerPos.add(dx,dy, z - 1);
                ++i;
            }
        }
        for (int dx = 2; dx < x - 1; ++dx) {
            for (int dz = 2; dz < z - 1; ++dz) {
                borderBlocks[i] = cornerPos.add(dx, y - 1,dz);
                ++i;
            }
        }
        for (int dz = 2; dz < z - 1; ++dz) {
            for (int dy = 1; dy < y; ++dy) {
                borderBlocks[i] = cornerPos.add(x - 1,dy,dz);
                ++i;
            }
        }

        return borderBlocks;
    }
    
    public static BlockPos[] getCubeInside(BlockPos cornerPos, int x, int y, int z) {
        BlockPos[] insideBlocks = new BlockPos[(x - 1) * (y - 1) * (z - 1)];
        int i = 0;
        for (int dx = 1; dx < x; ++dx) {
            for (int dy = 1; dy < y; ++dy) {
                for (int dz = 1; dz < z; ++dz) {
                    insideBlocks[i] = cornerPos.add(x,y,z);
                    ++i;
                }
            }
        }
        return insideBlocks;
    }
}
