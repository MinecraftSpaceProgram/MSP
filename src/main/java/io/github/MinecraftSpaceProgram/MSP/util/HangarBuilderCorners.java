package io.github.MinecraftSpaceProgram.MSP.util;

import net.minecraft.util.math.BlockPos;

public class HangarBuilderCorners {
    public final BlockPos startingPos;
    public final int x;
    public final int y;
    public final int z;

    public HangarBuilderCorners(BlockPos startingPos, int x, int y , int z) {
        this.startingPos = startingPos;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
