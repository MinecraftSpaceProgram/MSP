package io.github.MinecraftSpaceProgram.MSP.rocket;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.init.MSPBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Rocket {
  private static final Marker MARKER = MarkerManager.getMarker("RocketBuilding");

  private final List<BlockPos> rocketBlocksPos;
  private final List<BlockPos> borderBlocksPos;

  private final World world;
  public final float fuel;

  private BlockPos baseBlockPos;

  public final boolean NO_FLYING_BLOCKS;
  public final boolean ROCKET_BORDER_COATED;
  public final boolean HAS_ENGINES;
  public final boolean HAS_TANKS;
  public final boolean ENGINES_FACING_OUTWARDS;

  public Rocket(BlockPos[] rocketBlocksPos, BlockPos[] borderBlocksPos, World world) {
    this(Arrays.asList(borderBlocksPos), Arrays.asList(rocketBlocksPos), world);
  }

  public Rocket(List<BlockPos> rocketBlocksPos, List<BlockPos> borderBlocksPos, World world) {
    this.borderBlocksPos = borderBlocksPos;
    this.rocketBlocksPos = rocketBlocksPos;

    this.world = world;

    MSP.LOGGER.info(MARKER, "Starting rocket building rules enforcement");
    findBaseBlock();
    this.NO_FLYING_BLOCKS = enforceNoFlyingBlocks();
    this.ROCKET_BORDER_COATED = true;
    this.HAS_ENGINES = true;
    this.HAS_TANKS = true;
    this.ENGINES_FACING_OUTWARDS = true;

    this.fuel = this.findFuel();

    MSP.LOGGER.info(MARKER, toString());
  }

  public static Rocket createFromLaunchpad(Launchpad launchpad) {
    return launchpad.findRocket();
  }

  public boolean getRulesRespected() {
    return (!rocketBlocksPos.isEmpty())
        && NO_FLYING_BLOCKS
        && ROCKET_BORDER_COATED
        && HAS_ENGINES
        && HAS_TANKS
        && ENGINES_FACING_OUTWARDS;
  }

  private void findBaseBlock() {
    for (BlockPos blockPos : rocketBlocksPos) {
      if (world.getBlockState(blockPos).getBlock() == MSPBlocks.FLIGHT_CONTROLLER.get()) {
        baseBlockPos = blockPos;
        return;
      }
    }
  }

  private boolean enforceNoFlyingBlocks() {
    ArrayList<BlockPos> toVisit = new ArrayList<>();
    ArrayList<BlockPos> notVisited = new ArrayList<>(rocketBlocksPos);

    if (rocketBlocksPos.isEmpty()) return true;

    if (baseBlockPos == null) toVisit.add(rocketBlocksPos.get(0));
    else toVisit.add(baseBlockPos);

    while (!toVisit.isEmpty()) {
      BlockPos currentPos = toVisit.get(0);
      for (int delta : new int[] {-1, 1}) {
        for (BlockPos nextPos :
            new BlockPos[] {
              currentPos.add(delta, 0, 0), currentPos.add(0, delta, 0), currentPos.add(0, 0, delta)
            }) {
          if (rocketBlocksPos.contains(nextPos)
              && !world.isAirBlock(nextPos)
              && notVisited.contains(nextPos)) {
            toVisit.add(nextPos);
          }
        }
      }
      notVisited.remove(currentPos);
      toVisit.remove(0);
    }

    return notVisited.isEmpty();
  }
  /*
    private boolean enforceRocketBorderCoated() {
      ResourceLocation rocketBlocksTag = new ResourceLocation("msp", "rocket_blocks");
      for (BlockPos blockPos : borderBlocksPos) {
        // let's hope ITag.func_230235_a_ is equivalent to contains
        if (!BlockTags.getCollection()
            .getOrCreate(rocketBlocksTag)
            .func_230235_a_(world.getBlockState(blockPos).getBlock())) {
          return false;
        }
      }
      return true;
    }

    private boolean enforceHasEngines() {
      ResourceLocation enginesTag = new ResourceLocation("msp", "engine_blocks");
      for (BlockPos blockPos : rocketBlocksPos) {
        if (BlockTags.getCollection()
            .getOrCreate(enginesTag)
            .func_230235_a_(world.getBlockState(blockPos).getBlock())) {
          return true;
        }
      }
      return false;
    }
  */
  private float findFuel() {
    float fuelCounter = 0.0F;
    for (BlockPos blockPos : this.rocketBlocksPos) {
      TileEntity tileEntity = world.getTileEntity(blockPos);
      if (tileEntity != null) {
        if (IRocketTank.class.isAssignableFrom(tileEntity.getClass())) {
          fuelCounter += ((IRocketTank) tileEntity).getFuelLevel();
          MSP.LOGGER.debug("found fuel");
        }
      }
    }
    return fuelCounter;
  }

  /*private boolean enforceHasTanks() {
      ResourceLocation enginesTag = new ResourceLocation("msp", "tank_blocks");
      for (BlockPos blockPos : rocketBlocksPos) {
        if (BlockTags.getCollection()
            .getOrCreate(enginesTag)
            .func_230235_a_(world.getBlockState(blockPos).getBlock())) {
          return true;
        }
      }
      return false;
    }

    private boolean enforceEnginesFacingOutwards() {
      if (!HAS_ENGINES) return true;

      ResourceLocation enginesTag = new ResourceLocation("msp", "engine_blocks");
      for (BlockPos blockPos : rocketBlocksPos) {
        BlockState blockState = world.getBlockState(blockPos);
        if (BlockTags.getCollection().getOrCreate(enginesTag).func_230235_a_(blockState.getBlock())) {
          if (borderBlocksPos.contains(blockPos)) {
            switch (blockState.get(EngineBlock.FACING)) {
              case UP:
                if (rocketBlocksPos.contains(blockPos.add(0, -1, 0))) return false;
              case DOWN:
                if (rocketBlocksPos.contains(blockPos.add(0, 1, 0))) return false;
              case NORTH:
                if (rocketBlocksPos.contains(blockPos.add(0, 0, 1))) return false;
              case SOUTH:
                if (rocketBlocksPos.contains(blockPos.add(0, 0, -1))) return false;
              case EAST:
                if (rocketBlocksPos.contains(blockPos.add(-1, 0, 0))) return false;
              case WEST:
                if (rocketBlocksPos.contains(blockPos.add(1, 0, 0))) return false;
              default:
            }
          } else {
            return false;
          }
        }
      }
      return true;
    }
  */
  public List<BlockPos> getRocketBlocksPos() {
    return rocketBlocksPos;
  }

  public World getWorld() {
    return world;
  }

  public String toString() {
    return "Rocket{"
        + "baseBlockPos="
        + baseBlockPos
        + ", NO_FLYING_BLOCKS="
        + NO_FLYING_BLOCKS
        + ", ROCKET_BORDER_COATED="
        + ROCKET_BORDER_COATED
        + ", HAS_ENGINES="
        + HAS_ENGINES
        + ", HAS_TANKS="
        + HAS_TANKS
        + ", ENGINES_FACING_OUTWARDS="
        + ENGINES_FACING_OUTWARDS
        + '}';
  }
}
