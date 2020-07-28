package io.github.MinecraftSpaceProgram.MSP.rocket;

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
                startingPos,
                startingPos.add(x,0,0),
                startingPos.add(0,y,0),
                startingPos.add(0,0,z),
                startingPos.add(x,y,0),
                startingPos.add(x,0,z),
                startingPos.add(0,y,z),
                startingPos.add(x,y,z)
        };
    }

    /**
     * @return BlockPos[] The two most extreme hangar corners
     */
    public BlockPos[] getExtremeCorners() {
        return new BlockPos[]{
                startingPos,
                startingPos.add(x,y,z)
        };
    }
}
