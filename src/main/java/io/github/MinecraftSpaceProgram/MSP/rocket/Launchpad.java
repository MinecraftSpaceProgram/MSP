package io.github.MinecraftSpaceProgram.MSP.rocket;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.block.EjectorSeatBlock;
import io.github.MinecraftSpaceProgram.MSP.init.MSPBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static io.github.MinecraftSpaceProgram.MSP.MSP.LOGGER;
import static io.github.MinecraftSpaceProgram.MSP.util.BlockPosUtil.*;

public final class Launchpad {
  public final BlockPos startingPos;
  public final int x;
  public final int y;
  public final int z;
  final World world;
  public float engineThrust;
  public float engineConsumption;
  public float mass;

  private static final Marker MARKER = MarkerManager.getMarker("MSP-Launchpad");

  private static final int HORIZONTAL_LIMIT = 256;
  private static final int VERTICAL_LIMIT = 256;

  public List<BlockPos> engines = new ArrayList<>();

  public BlockPos chair;
  public Direction chairDirection;

  /**
   * @param startingPos first corner position
   * @param x x axis offset
   * @param y y axis offset
   * @param z z axis offset
   */
  public Launchpad(World world, BlockPos startingPos, int x, int y, int z) {
    this.startingPos = startingPos.add(Math.min(x, 0), Math.min(y, 0), Math.min(z, 0));
    this.x = Math.abs(x);
    this.y = Math.abs(y);
    this.z = Math.abs(z);
    this.world = world;
  }

  /**
   * Find launchpad (rectangular base and crane)
   *
   * @param world world
   * @param startingPos starting pos from where to begin searching
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
        if (world.getBlockState(startingPos.add(px, 0, 0)).getBlock() != launchpad_base) {
          --px;
          break;
        }
        ++px;
      }
      int nx = 1;
      while (nx < HORIZONTAL_LIMIT) {
        if (world.getBlockState(startingPos.add(-nx, 0, 0)).getBlock() != launchpad_base) {
          --nx;
          break;
        }
        ++nx;
      }
      int pz = 1;
      while (pz < HORIZONTAL_LIMIT) {
        if (world.getBlockState(startingPos.add(0, 0, pz)).getBlock() != launchpad_base) {
          --pz;
          break;
        }
        ++pz;
      }
      int nz = 1;
      while (nz < HORIZONTAL_LIMIT) {
        if (world.getBlockState(startingPos.add(0, 0, -nz)).getBlock() != launchpad_base) {
          --nz;
          break;
        }
        ++nz;
      }

      BlockPos cornerPos = startingPos.add(-nx, 1, -nz);
      int x = px + nx;
      int z = pz + nz;

      BlockPos startingCranePos = null;
      for (BlockPos borderPos : getRectangleOutsideBorders(cornerPos, x, z)) {
        if (world.getBlockState(borderPos).getBlock() == launchpad_crane) {
          startingCranePos = borderPos;
          break;
        }
      }
      if (startingCranePos == null) return null;
      int py = 1;
      while (py < VERTICAL_LIMIT && y + py < 256) {
        if (world.getBlockState(startingCranePos.add(0, py, 0)).getBlock() != launchpad_crane)
          break;
        ++py;
      }

      return py > 1 ? new Launchpad(world, cornerPos, x, py - 1, z) : null;
    } else MSP.LOGGER.info(MARKER, "Starting block is not launchpad base");
    return null;
  }

  /** @return BlockPos[] The two most extreme launchpad corners */
  public BlockPos[] getExtremeCorners() {
    return new BlockPos[] {startingPos, startingPos.add(x, y, z)};
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

    final BlockPos[] launchpadInsideBlocks = getCube(startingPos, x, y, z);
    final BlockPos[] launchpadBorderBlocks = getCubeBorder(startingPos, x, y, z);

    for (BlockPos insideBlockPos : launchpadInsideBlocks) {
      if (!world.isAirBlock(insideBlockPos)) {
        rocket.add(insideBlockPos);
        Block block;
        if (IRocketEngine.class.isAssignableFrom(
            (block = world.getBlockState(insideBlockPos).getBlock()).getClass())) {
          this.engines.add(insideBlockPos);
          this.engineThrust += ((IRocketEngine)block).getThrust();
          this.engineConsumption += ((IRocketEngine)block).getFlowRate();
        }
        BlockState blockState;
        if ((blockState = world.getBlockState(insideBlockPos)).getBlock() instanceof EjectorSeatBlock){
          this.chair = insideBlockPos;
          this.chairDirection = blockState.get(HorizontalBlock.HORIZONTAL_FACING);
        }
        if (IMassive.class.isAssignableFrom(
            (block = world.getBlockState(insideBlockPos).getBlock()).getClass())) {
          this.mass += ((IMassive) block).getMass();
        }else{
          mass += 250;
        }
      } else {
        airNotOutside.add(insideBlockPos);
      }
    }

    for (BlockPos borderBlockPos : launchpadBorderBlocks) {
      if (world.isAirBlock(borderBlockPos)) {
        outside.add(borderBlockPos);
        toVisit.add(borderBlockPos);
        airNotOutside.remove(borderBlockPos);
      } else rocketBorder.add(borderBlockPos);
    }

    if (outside.isEmpty()) {
      LOGGER.info(
          MARKER,
          "No air was found on the borders of the hangar, the rocket must be taking all the space...");
      return new Rocket(launchpadInsideBlocks, launchpadBorderBlocks, world);
    }

    while (!toVisit.isEmpty()) {
      BlockPos currentPos = toVisit.get(0);

      for (int x : new int[] {-1, 1}) {
        for (BlockPos nextPos :
            new BlockPos[] {
              new BlockPos(currentPos.getX() + x, currentPos.getY(), currentPos.getZ()),
              new BlockPos(currentPos.getX(), currentPos.getY() + x, currentPos.getZ()),
              new BlockPos(currentPos.getX(), currentPos.getY(), currentPos.getZ() + x)
            }) {
          if (this.isInLaunchpad(nextPos) && !outside.contains(nextPos)) {
            if (world.isAirBlock(nextPos)) {
              outside.add(nextPos);
              toVisit.add(nextPos);
              airNotOutside.remove(nextPos);
            } else if (!rocketBorder.contains(nextPos)) {
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
    MSP.LOGGER.info(MARKER, "Clearing launchpad");
    for (BlockPos pos : getCube(startingPos, x, y, z)) {
      // world.func_230547_a_(pos, Blocks.AIR);
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
        extremeCorners[1].getZ());
  }
}
