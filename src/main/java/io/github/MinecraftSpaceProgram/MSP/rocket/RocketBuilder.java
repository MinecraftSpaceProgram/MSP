package io.github.MinecraftSpaceProgram.MSP.rocket;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayList;

public final class RocketBuilder {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker("MSP-RocketBuilding");
    protected final Hangar hangar;
    protected final World world;
    
    public RocketBuilder(Hangar hangar, World world) {
        this.hangar = hangar;
        this.world = world;
    }
    
    private boolean isInHangar(BlockPos blockPos) {
        int dx = blockPos.getX() - hangar.startingPos.getX();
        int dy = blockPos.getY() - hangar.startingPos.getY();
        int dz = blockPos.getZ() - hangar.startingPos.getZ();
        return dx > 0 && dx < hangar.x && dy > 0 && dy < hangar.y && dz > 0 && dz < hangar.z;
    }
    
    private boolean isAir(BlockPos blockPos) {
        return world.isAirBlock(blockPos);
    }
    
    private ArrayList<BlockPos> getHangarBorderBlocks() {
        final ArrayList<BlockPos> borderBlocks = new ArrayList<>();

        for (int x = 1; x < hangar.x; ++x) {
            for (int y = 1; y < hangar.y; ++y) {
                borderBlocks.add(hangar.startingPos.add(x,y,1));
            }
        }
        for (int x = 2; x < hangar.x - 1; ++x) {
            for (int z = 2; z < hangar.z - 1; ++z) {
                borderBlocks.add(hangar.startingPos.add(x,1,z));
            }
        }
        for (int z = 2; z < hangar.z - 1; ++z) {
            for (int y = 1; y < hangar.y; ++y) {
                borderBlocks.add(hangar.startingPos.add(1,y,z));
            }
        }
        for (int x = 1; x < hangar.x; ++x) {
            for (int y = 1; y < hangar.y; ++y) {
                borderBlocks.add(hangar.startingPos.add(x,y,hangar.z - 1));
            }
        }
        for (int x = 2; x < hangar.x - 1; ++x) {
            for (int z = 2; z < hangar.z - 1; ++z) {
                borderBlocks.add(hangar.startingPos.add(x,hangar.y - 1,z));
            }
        }
        for (int z = 2; z < hangar.z - 1; ++z) {
            for (int y = 1; y < hangar.y; ++y) {
                borderBlocks.add(hangar.startingPos.add(hangar.x - 1,y,z));
            }
        }

        return borderBlocks;
    }

    private ArrayList<BlockPos> getHangarInsideBlocks() {
        ArrayList<BlockPos> insideBlocks = new ArrayList<>();
        for (int x = 1; x < hangar.x; ++x) {
            for (int y = 1; y < hangar.y; ++y) {
                for (int z = 1; z < hangar.z; ++z) {
                    insideBlocks.add(
                            hangar.startingPos.add(x,y,z)
                    );
                }
            }
        }
        return insideBlocks;
    }
    
    public Rocket findRocket() {
        ArrayList<BlockPos> outside = new ArrayList<>();
        ArrayList<BlockPos> airNotOutside = new ArrayList<>();
        ArrayList<BlockPos> toVisit = new ArrayList<>();
        ArrayList<BlockPos> rocket = new ArrayList<>();
        ArrayList<BlockPos> rocketBorder = new ArrayList<>();

        final ArrayList<BlockPos> hangarInsideBlocks = this.getHangarInsideBlocks();
        final ArrayList<BlockPos> hangarBorderBlocks = this.getHangarBorderBlocks();
        
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
            LOGGER.debug(MARKER, "For info, the hangar was " + hangar.toString());
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
                    if (this.isInHangar(nextPos) && !outside.contains(nextPos)) {
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
