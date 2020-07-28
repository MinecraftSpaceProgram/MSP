package io.github.MinecraftSpaceProgram.MSP.rocket;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.block.EngineBlock;
import io.github.MinecraftSpaceProgram.MSP.init.BlockLoader;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayList;

public final class Rocket {
    private static final Marker MARKER = MarkerManager.getMarker("MSP-RocketBuilding");

    private final ArrayList<BlockPos> rocketBlocksPos;
    private final ArrayList<BlockPos> borderBlocksPos;
    private final World world;

    private BlockPos baseBlockPos;

    public final boolean NO_FLYING_BLOCKS;
    public final boolean ROCKET_BORDER_COATED;
    public final boolean HAS_ENGINES;
    public final boolean HAS_TANKS;
    public final boolean ENGINES_FACING_OUTWARDS;

    public Rocket(ArrayList<BlockPos> rocketBlocksPos, ArrayList<BlockPos> borderBlocksPos, World world) {
        this.borderBlocksPos = borderBlocksPos;
        this.rocketBlocksPos = rocketBlocksPos;
        this.world = world;

        MSP.LOGGER.info(MARKER, "Starting rocket building rules enforcement");
        findBaseBlock();
        this.NO_FLYING_BLOCKS = enforceNoFlyingBlocks();
        this.ROCKET_BORDER_COATED = enforceRocketBorderCoated();
        this.HAS_ENGINES = enforceHasEngines();
        this.HAS_TANKS = enforceHasTanks();
        this.ENGINES_FACING_OUTWARDS = enforceEnginesFacingOutwards();

        MSP.LOGGER.info(MARKER, this.toString());
    }

    public static Rocket createFromHangar(Hangar hangar, World world) {
        return new RocketBuilder(hangar, world).findRocket();
    }

    private void findBaseBlock() {
        for (BlockPos blockPos : this.rocketBlocksPos) {
            if (this.world.getBlockState(blockPos).getBlock() == BlockLoader.FLIGHT_CONTROLLER.get()) {
                this.baseBlockPos = blockPos;
                return;
            }
        }
    }

    private boolean enforceNoFlyingBlocks() {
        ArrayList<BlockPos> toVisit = new ArrayList<>();
        //noinspection unchecked
        ArrayList<BlockPos> notVisited = (ArrayList<BlockPos>) this.rocketBlocksPos.clone();

        if (this.baseBlockPos == null)
            toVisit.add(this.rocketBlocksPos.get(0));
        else
            toVisit.add(this.baseBlockPos);

        while (!toVisit.isEmpty()) {
            BlockPos currentPos = toVisit.get(0);
            for (int delta : new int[]{-1,1}) {
                for (BlockPos nextPos : new BlockPos[]{
                        currentPos.add(delta,0,0),
                        currentPos.add(0,delta,0),
                        currentPos.add(0,0,delta)
                }) {
                    if (this.rocketBlocksPos.contains(nextPos) && !this.world.isAirBlock(nextPos) && notVisited.contains(nextPos))
                        toVisit.add(nextPos);
                }
            }
            toVisit.remove(0);
        }

        return notVisited.isEmpty();
    }

    private boolean enforceRocketBorderCoated() {
        ResourceLocation rocketBlocksTag = new ResourceLocation("msp", "rocket_blocks");
        for (BlockPos blockPos : this.borderBlocksPos) {
            // let's hope ITag.func_230235_a_ is equivalent to contains
            if (!BlockTags.getCollection().getOrCreate(rocketBlocksTag).func_230235_a_(this.world.getBlockState(blockPos).getBlock())) {
                return false;
            }
        }
        return true;
    }

    private boolean enforceHasEngines() {
        ResourceLocation enginesTag = new ResourceLocation("msp", "engine_blocks");
        for (BlockPos blockPos : this.rocketBlocksPos) {
            if (BlockTags.getCollection().getOrCreate(enginesTag).func_230235_a_(this.world.getBlockState(blockPos).getBlock())) {
                return true;
            }
        }
        return false;
    }

    private boolean enforceHasTanks() {
        ResourceLocation enginesTag = new ResourceLocation("msp", "tank_blocks");
        for (BlockPos blockPos : this.rocketBlocksPos) {
            if (BlockTags.getCollection().getOrCreate(enginesTag).func_230235_a_(this.world.getBlockState(blockPos).getBlock())) {
                return true;
            }
        }
        return false;
    }

    private boolean enforceEnginesFacingOutwards() {
        if (!this.HAS_ENGINES)
            return true;

        ResourceLocation enginesTag = new ResourceLocation("msp", "engine_blocks");
        for (BlockPos blockPos : this.rocketBlocksPos) {
            BlockState blockState = this.world.getBlockState(blockPos);
            if (BlockTags.getCollection().getOrCreate(enginesTag).func_230235_a_(blockState.getBlock())) {
                if (this.borderBlocksPos.contains(blockPos)) {
                    switch (blockState.get(EngineBlock.FACING)) {
                        case UP:
                            if (rocketBlocksPos.contains(blockPos.add(0,-1,0))) return false;
                        case DOWN:
                            if (rocketBlocksPos.contains(blockPos.add(0,1,0))) return false;
                        case NORTH:
                            if (rocketBlocksPos.contains(blockPos.add(0,0,1))) return false;
                        case SOUTH:
                            if (rocketBlocksPos.contains(blockPos.add(0,0,-1))) return false;
                        case EAST:
                            if (rocketBlocksPos.contains(blockPos.add(-1,0,0))) return false;
                        case WEST:
                            if (rocketBlocksPos.contains(blockPos.add(1,0,0))) return false;
                    }
                }
                else {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Rocket{" +
                "baseBlockPos=" + baseBlockPos +
                ", NO_FLYING_BLOCKS=" + NO_FLYING_BLOCKS +
                ", ROCKET_BORDER_COATED=" + ROCKET_BORDER_COATED +
                ", HAS_ENGINES=" + HAS_ENGINES +
                ", HAS_TANKS=" + HAS_TANKS +
                ", ENGINES_FACING_OUTWARDS=" + ENGINES_FACING_OUTWARDS +
                '}';
    }

    public ArrayList<BlockPos> getRocketBlocksPos() {
        return this.rocketBlocksPos;
    }
}
