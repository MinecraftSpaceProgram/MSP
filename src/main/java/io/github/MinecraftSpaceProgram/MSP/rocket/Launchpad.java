package io.github.MinecraftSpaceProgram.MSP.rocket;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.init.MSPBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.util.ArrayList;

import static io.github.MinecraftSpaceProgram.MSP.util.BlockPosUtil.*;

public final class Launchpad {
    public final BlockPos startingPos;
    public final int x;
    public final int y;
    public final int z;
    final World world;

    private static final Marker MARKER = MarkerManager.getMarker("MSP-Launchpad");

    private static final int HORIZONTAL_LIMIT = 256;
    private static final int VERTICAL_LIMIT = 256;


    /**
     * @param startingPos first corner position
     * @param x x axis offset
     * @param y y axis offset
     * @param z z axis offset
     */
    public Launchpad(World world, BlockPos startingPos, int x, int y , int z) {
        this.startingPos = new BlockPos(
                startingPos.getX() + Math.min(x, 0),
                startingPos.getY() + Math.min(y, 0),
                startingPos.getZ() + Math.min(z, 0)
        );
        this.x = Math.abs(x);
        this.y = Math.abs(y);
        this.z = Math.abs(z);
        this.world = world;
    }

    /**
     * Find launchpad (rectangular base and crane)
     * @param world world
     * @param startingPos starting pos from where to begin search
     * @return found Launchpad
     */
    @Nullable
    public static Launchpad find(World world, BlockPos startingPos) {
        Block launchpad_base = MSPBlocks.LAUNCHPAD_BASE.get();
        Block launchpad_crane = MSPBlocks.LAUNCHPAD_CRANE.get();
        int y = startingPos.getY();
        if (world.getBlockState(startingPos).getBlock() == launchpad_base) {
            int px = 1;
            while (px < HORIZONTAL_LIMIT) {
                if (world.getBlockState(startingPos.add(px,0,0)).getBlock() != launchpad_base) {
                    --px;
                    break;
                }
                ++px;
            }
            int nx = 1;
            while (nx < HORIZONTAL_LIMIT) {
                if (world.getBlockState(startingPos.add(-nx,0,0)).getBlock() != launchpad_base) {
                    --nx;
                    break;
                }
                ++nx;
            }
            int pz = 1;
            while (pz < HORIZONTAL_LIMIT) {
                if (world.getBlockState(startingPos.add(0,0,pz)).getBlock() != launchpad_base) {
                    --pz;
                    break;
                }
                ++pz;
            }
            int nz = 1;
            while (nz < HORIZONTAL_LIMIT) {
                if (world.getBlockState(startingPos.add(0,0,-nz)).getBlock() != launchpad_base) {
                    --nz;
                    break;
                }
                ++nz;
            }

            BlockPos cornerPos = startingPos.add(-nx,0,-nz);
            int x = px + nx + 1;
            int z = pz + nz + 1;

            BlockPos startingCranePos = null;
            for (BlockPos borderPos : getRectangleOutsideBorders(cornerPos, x, z)) {
                if (world.getBlockState(borderPos).getBlock() == launchpad_crane) {
                    startingCranePos = borderPos;
                    break;
                }
            }
            if (startingCranePos == null)
                return null;
            int py = 1;
            while (py < VERTICAL_LIMIT && y + py < 256) {
                if (world.getBlockState(startingCranePos.add(0,py,0)).getBlock() != launchpad_crane)
                    break;
                ++py;
            }

            return py > 1 ? new Launchpad(world, cornerPos, x, py - 1, z) : null;
        }
        else
            MSP.LOGGER.info(MARKER, "Starting block is not launchpad base");
        return null;
    }

    /**
     * @return BlockPos[] The two most extreme launchpad corners
     */
    public BlockPos[] getExtremeCorners() {
        return new BlockPos[]{
                startingPos,
                startingPos.add(x,y,z)
        };
    }

    private boolean isInLaunchpad(BlockPos blockPos) {
        int dx = blockPos.getX() - startingPos.getX();
        int dy = blockPos.getY() - startingPos.getY();
        int dz = blockPos.getZ() - startingPos.getZ();
        return dx > 0 && dx < x && dy > 0 && dy < y && dz > 0 && dz < z;
    }

    public Rocket findRocket() {
        ArrayList<BlockPos> outside = new ArrayList<>();
        ArrayList<BlockPos> airNotOutside = new ArrayList<>();
        ArrayList<BlockPos> toVisit = new ArrayList<>();
        ArrayList<BlockPos> rocket = new ArrayList<>();
        ArrayList<BlockPos> rocketBorder = new ArrayList<>();

        final BlockPos[] hangarInsideBlocks = getCubeInside(startingPos, x, y, z);
        final BlockPos[] hangarBorderBlocks = getCubeInsideBorders(startingPos, x, y, z);

        for (BlockPos insideBlockPos : hangarInsideBlocks) {
            if (!world.isAirBlock(insideBlockPos))
                rocket.add(insideBlockPos);
            else
                airNotOutside.add(insideBlockPos);
        }

        for (BlockPos borderBlockPos : hangarBorderBlocks) {
            if (world.isAirBlock(borderBlockPos)) {
                outside.add(borderBlockPos);
                toVisit.add(borderBlockPos);
                airNotOutside.remove(borderBlockPos);
            }
            else
                rocketBorder.add(borderBlockPos);
        }

        if (outside.isEmpty()) {
            MSP.LOGGER.info(MARKER, "No air was found on the borders of the hangar, the rocket must be taking all the space...");
            MSP.LOGGER.info(MARKER, "For info, the hangar was " + toString());
            return new Rocket(hangarInsideBlocks, hangarBorderBlocks, world);
        }

        while (!toVisit.isEmpty()) {
            BlockPos currentPos = toVisit.get(0);
            for (int x : new int[]{-1,1}) {
                for (BlockPos nextPos : new BlockPos[]{
                        new BlockPos(currentPos.getX() + x, currentPos.getY(), currentPos.getZ()),
                        new BlockPos(currentPos.getX(), currentPos.getY() + x, currentPos.getZ()),
                        new BlockPos(currentPos.getX(), currentPos.getY(), currentPos.getZ() + x)
                }) {
                    if (this.isInLaunchpad(nextPos) && !outside.contains(nextPos)) {
                        if (world.isAirBlock(nextPos)) {
                            outside.add(nextPos);
                            toVisit.add(nextPos);
                            airNotOutside.remove(nextPos);
                        }
                        else if (!rocketBorder.contains(nextPos)) {
                            rocketBorder.add(nextPos);
                        }
                    }
                }
            }
            toVisit.remove(0);
        }

        rocket.addAll(airNotOutside);
        return new Rocket(rocket, rocketBorder, world);
    }

    public void clear() {
        for (BlockPos pos : getCubeInside(startingPos, x, y, z)) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

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
}
