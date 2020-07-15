package io.github.MinecraftSpaceProgram.MSP.util;

import net.minecraft.util.math.BlockPos;

public final class Hangar {
    public final BlockPos startingPos;
    public final int x;
    public final int y;
    public final int z;

    @Override
    public String toString() {
        BlockPos[] extremeCorners = this.getExtremeCorners();
        return String.format(
                "HangarCorners between (%d,%d,%d) and (%d,%d,%d)",
                extremeCorners[0].getX(),
                extremeCorners[0].getY(),
                extremeCorners[0].getZ(),
                extremeCorners[1].getX(),
                extremeCorners[1].getY(),
                extremeCorners[1].getZ()
        );
    }

    /**
     *
     * @param startingPos first corner position
     * @param x x axis offset
     * @param y y axis offset
     * @param z z axis offset
     */
    public Hangar(final BlockPos startingPos, final int x, final int y , final int z) {
        this.startingPos = new BlockPos(
                startingPos.getX() + Math.min(x, 0),
                startingPos.getY() + Math.min(y, 0),
                startingPos.getZ() + Math.min(z, 0)
        );
        this.x = Math.abs(x);
        this.y = Math.abs(y);
        this.z = Math.abs(z);
    }

    /**
     * @return BlockPos[] All 8 corners of the hangar
     */
    public BlockPos[] getCorners() {
        return new BlockPos[]{
                this.startingPos,
                new BlockPos(this.startingPos.getX() + this.x, this.startingPos.getY(), this.startingPos.getZ()),
                new BlockPos(this.startingPos.getX(), this.startingPos.getY() + this.y, this.startingPos.getZ()),
                new BlockPos(this.startingPos.getX(), this.startingPos.getY(), this.startingPos.getZ() + this.z),
                new BlockPos(this.startingPos.getX() + this.x, this.startingPos.getY() + this.y, this.startingPos.getZ()),
                new BlockPos(this.startingPos.getX() + this.x, this.startingPos.getY(), this.startingPos.getZ() + this.z),
                new BlockPos(this.startingPos.getX(), this.startingPos.getY() + this.y, this.startingPos.getZ() + this.z),
                new BlockPos(this.startingPos.getX() + this.x, this.startingPos.getY() + this.y, this.startingPos.getZ() + this.z)
        };
    }

    /**
     * @return BlockPos[] The two most extreme hangar corners
     */
    public BlockPos[] getExtremeCorners() {
        return new BlockPos[]{
                startingPos,
                new BlockPos(startingPos.getX() + this.x, startingPos.getY() + this.y, startingPos.getZ() + this.z)
        };
    }
}
