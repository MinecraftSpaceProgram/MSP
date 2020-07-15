package io.github.MinecraftSpaceProgram.MSP.util;

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
        int dx = blockPos.getX() - this.hangar.startingPos.getX();
        int dy = blockPos.getY() - this.hangar.startingPos.getY();
        int dz = blockPos.getZ() - this.hangar.startingPos.getZ();
        return dx > 0 && dx < this.hangar.x && dy > 0 && dy < this.hangar.y && dz > 0 && dz < this.hangar.z;
    }
    
    private boolean isAir(BlockPos blockPos) {
        return this.world.isAirBlock(blockPos);
    }
    
    private ArrayList<BlockPos> getHangarBorderBlocks() {
        final ArrayList<BlockPos> borderBlocks = new ArrayList<>();

        for (int x = 1; x < this.hangar.x; x++) {
            for (int y = 1; y < this.hangar.y; y++) {
                borderBlocks.add(new BlockPos(this.hangar.startingPos.getX() + x, this.hangar.startingPos.getY() + y, this.hangar.startingPos.getZ() + 1));
            }
        }
        for (int x = 2; x < this.hangar.x - 1; x++) {
            for (int z = 2; z < this.hangar.z - 1; z++) {
                borderBlocks.add(new BlockPos(this.hangar.startingPos.getX() + x, this.hangar.startingPos.getY() + 1, this.hangar.startingPos.getZ() + z));
            }
        }
        for (int z = 2; z < this.hangar.z - 1; z++) {
            for (int y = 1; y < this.hangar.y; y++) {
                borderBlocks.add(new BlockPos(this.hangar.startingPos.getX() + 1, this.hangar.startingPos.getY() + y, this.hangar.startingPos.getZ() + z));
            }
        }
        for (int x = 1; x < this.hangar.x; x++) {
            for (int y = 1; y < this.hangar.y; y++) {
                borderBlocks.add(new BlockPos(this.hangar.startingPos.getX() + x, this.hangar.startingPos.getY() + y, this.hangar.startingPos.getZ() + this.hangar.z - 1));
            }
        }
        for (int x = 2; x < this.hangar.x - 1; x++) {
            for (int z = 2; z < this.hangar.z - 1; z++) {
                borderBlocks.add(new BlockPos(this.hangar.startingPos.getX() + x, this.hangar.startingPos.getY() + this.hangar.y - 1, this.hangar.startingPos.getZ() + z));
            }
        }
        for (int z = 2; z < this.hangar.z - 1; z++) {
            for (int y = 1; y < this.hangar.y; y++) {
                borderBlocks.add(new BlockPos(this.hangar.startingPos.getX() + this.hangar.x - 1, this.hangar.startingPos.getY() + y, this.hangar.startingPos.getZ() + z));
            }
        }

        return borderBlocks;
    }

    private ArrayList<BlockPos> getHangarInsideBlocks() {
        ArrayList<BlockPos> insideBlocks = new ArrayList<>();
        for (int x = 1; x < this.hangar.x; x++) {
            for (int y = 1; y < this.hangar.y; y++) {
                for (int z = 1; z < this.hangar.z; z++) {
                    insideBlocks.add(
                            new BlockPos(this.hangar.startingPos.getX() + x, this.hangar.startingPos.getY() + y, this.hangar.startingPos.getZ() + z)
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
            if (this.world.isAirBlock(borderBlockPos)) {
                outside.add(borderBlockPos);
                toVisit.add(borderBlockPos);
                airNotOutside.remove(borderBlockPos);
            }
            else
                rocketBorder.add(borderBlockPos);
        }

        if (outside.isEmpty()) {
            LOGGER.debug(MARKER, "No air was found on the borders of the hangar, the rocket must be taking all the space...");
            LOGGER.debug(MARKER, "For info, the hangar was " + this.hangar.toString());
            return new Rocket(hangarInsideBlocks, hangarBorderBlocks, this.world);
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
        return new Rocket(rocket, rocketBorder, this.world);
    }
}
