package io.github.MinecraftSpaceProgram.MSP.rocket;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayList;

import static io.github.MinecraftSpaceProgram.MSP.util.BlockPosUtil.getCubeInside;
import static io.github.MinecraftSpaceProgram.MSP.util.BlockPosUtil.getCubeInsideBorders;

public final class RocketBuilder {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker("MSP-RocketBuilding");
    protected final Launchpad launchpad;
    protected final World world;
    
    public RocketBuilder(Launchpad launchpad, World world) {
        this.launchpad = launchpad;
        this.world = world;
    }
    
    private boolean isInLaunchpad(BlockPos blockPos) {
        int dx = blockPos.getX() - launchpad.startingPos.getX();
        int dy = blockPos.getY() - launchpad.startingPos.getY();
        int dz = blockPos.getZ() - launchpad.startingPos.getZ();
        return dx > 0 && dx < launchpad.x && dy > 0 && dy < launchpad.y && dz > 0 && dz < launchpad.z;
    }
    
    private boolean isAir(BlockPos blockPos) {
        return world.isAirBlock(blockPos);
    }

    public Rocket findRocket() {
        ArrayList<BlockPos> outside = new ArrayList<>();
        ArrayList<BlockPos> airNotOutside = new ArrayList<>();
        ArrayList<BlockPos> toVisit = new ArrayList<>();
        ArrayList<BlockPos> rocket = new ArrayList<>();
        ArrayList<BlockPos> rocketBorder = new ArrayList<>();

        final BlockPos[] hangarInsideBlocks = getCubeInside(launchpad.startingPos, launchpad.x, launchpad.y, launchpad.z);
        final BlockPos[] hangarBorderBlocks = getCubeInsideBorders(launchpad.startingPos, launchpad.x, launchpad.y, launchpad.z);
        
        for (BlockPos insideBlockPos : hangarInsideBlocks) {
            if (!isAir(insideBlockPos))
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
            LOGGER.debug(MARKER, "No air was found on the borders of the hangar, the rocket must be taking all the space...");
            LOGGER.debug(MARKER, "For info, the hangar was " + launchpad.toString());
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
}
