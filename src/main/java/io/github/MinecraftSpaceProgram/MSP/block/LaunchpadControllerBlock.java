package io.github.MinecraftSpaceProgram.MSP.block;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.entity.RocketEntity;
import io.github.MinecraftSpaceProgram.MSP.init.MSPBlocks;
import io.github.MinecraftSpaceProgram.MSP.init.MSPTileEntityTypes;
import io.github.MinecraftSpaceProgram.MSP.rocket.Launchpad;
import io.github.MinecraftSpaceProgram.MSP.rocket.Rocket;
import io.github.MinecraftSpaceProgram.MSP.util.BlockStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static io.github.MinecraftSpaceProgram.MSP.util.VoxelShapesUtil.rotateY;

public class LaunchpadControllerBlock extends Block {
  private static final EnumProperty<Side> SIDE = EnumProperty.create("side", Side.class);
  private static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  private static final VoxelShape TOP_VOXEL_SHAPE =
      VoxelShapes.or(
          Block.makeCuboidShape(0.0D, 10.0D, 1.0D, 16.0D, 14.0D, 5.333333D),
          Block.makeCuboidShape(0.0D, 12.0D, 5.333333D, 16.0D, 16.0D, 9.666667D),
          Block.makeCuboidShape(0.0D, 14.0D, 9.666667D, 16.0D, 18.0D, 14.0D));
  private static final VoxelShape CENTER_VOXEL_SHAPE =
      VoxelShapes.or(
          TOP_VOXEL_SHAPE,
          Block.makeCuboidShape(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D),
          Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D));

  private static final Marker MARKER = MarkerManager.getMarker("LaunchpadController");

  public LaunchpadControllerBlock(Properties properties) {
    super(properties);
    this.setDefaultState(this.getStateContainer().getBaseState().with(SIDE, Side.CENTER));
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return state.get(SIDE) == Side.CENTER;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    if (state.get(SIDE) == Side.CENTER)
      return MSPTileEntityTypes.LAUNCHPAD_CONTROLLER.get().create();
    return null;
  }

  @Override
  protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
    builder.add(SIDE, FACING);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(@Nonnull BlockItemUseContext context) {
    World world = context.getWorld();
    BlockPos pos = context.getPos();
    Direction facing = context.getPlacementHorizontalFacing().getOpposite();
    if (!world.getBlockState(pos.offset(facing.rotateY())).isReplaceable(context)) return null;
    if (!world.getBlockState(pos.offset(facing.rotateYCCW())).isReplaceable(context)) return null;
    return getDefaultState().with(FACING, facing);
  }

  @Override
  @ParametersAreNonnullByDefault
  public void onBlockPlacedBy(
      World worldIn,
      BlockPos pos,
      BlockState state,
      @Nullable LivingEntity placer,
      ItemStack stack) {
    super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    Direction facing = state.get(FACING);
    if (!worldIn.isRemote) {
      worldIn.setBlockState(pos.offset(facing.rotateY()), state.with(SIDE, Side.RIGHT), 3);
      worldIn.setBlockState(pos.offset(facing.rotateYCCW()), state.with(SIDE, Side.LEFT), 3);
      worldIn.func_230547_a_(pos, Blocks.AIR);
      state.func_235734_a_(worldIn, pos, 3);
    }
  }

  @Override
  @ParametersAreNonnullByDefault
  public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
    if (!worldIn.isRemote) {
      Side thisSide = state.get(SIDE);
      Direction facing = state.get(FACING);
      BlockPos leftPos;
      BlockPos rightPos;
      BlockPos centerPos;
      if (thisSide == Side.LEFT) {
        leftPos = pos;
        centerPos = pos.offset(facing.rotateY());
        rightPos = centerPos.offset(facing.rotateY());
      } else if (thisSide == Side.RIGHT) {
        rightPos = pos;
        centerPos = pos.offset(facing.rotateYCCW());
        leftPos = centerPos.offset(facing.rotateYCCW());
      } else {
        centerPos = pos;
        leftPos = pos.offset(facing.rotateYCCW());
        rightPos = pos.offset(facing.rotateY());
      }
      BlockState centerState = worldIn.getBlockState(centerPos);
      BlockState leftState = worldIn.getBlockState(leftPos);
      BlockState rightState = worldIn.getBlockState(rightPos);

      if (thisSide != Side.LEFT && leftState.getBlock() == this && leftState.get(SIDE) == Side.LEFT)
        worldIn.setBlockState(leftPos, Blocks.AIR.getDefaultState(), 35);
      if (thisSide != Side.RIGHT
          && rightState.getBlock() == this
          && rightState.get(SIDE) == Side.RIGHT)
        worldIn.setBlockState(rightPos, Blocks.AIR.getDefaultState(), 35);
      if (thisSide != Side.CENTER
          && centerState.getBlock() == this
          && centerState.get(SIDE) == Side.CENTER)
        worldIn.setBlockState(centerPos, Blocks.AIR.getDefaultState(), 35);
    }
    super.onBlockHarvested(worldIn, pos, state, player);
  }

  @SuppressWarnings("deprecation")
  @Override
  @ParametersAreNonnullByDefault
  @Nonnull
  public ActionResultType onBlockActivated(
      BlockState state,
      World worldIn,
      BlockPos pos,
      PlayerEntity player,
      Hand handIn,
      BlockRayTraceResult hit) {
    // if (worldIn.isRemote)
    //    return ActionResultType.SUCCESS; //super.onBlockActivated(state, worldIn, pos, player,
    // handIn, hit);

    Direction facing = state.get(FACING);
    Block launchpad_base = MSPBlocks.LAUNCHPAD_BASE.get();
    BlockPos inFront = pos.offset(facing.getOpposite());
    BlockPos inFrontDown = inFront.down();
    BlockPos launchpadBeginPos = null;
    if (worldIn.getBlockState(inFront).getBlock() == launchpad_base) {
      launchpadBeginPos = inFront;
      MSP.LOGGER.info(MARKER, "Found launchpad block in front of the controller");
    }
    if (worldIn.getBlockState(inFrontDown).getBlock() == launchpad_base) {
      launchpadBeginPos = inFrontDown;
      MSP.LOGGER.info(MARKER, "Found launchpad block in front down of the controller");
    }

    if (launchpadBeginPos == null) {
      player.sendMessage(
          new TranslationTextComponent("msp.event.launchpad_not_found"), player.getUniqueID());
      return ActionResultType.FAIL;
    }

    Launchpad launchpad = Launchpad.find(worldIn, launchpadBeginPos);
    if (launchpad == null) {
      player.sendMessage(
          new TranslationTextComponent("event.msp.launchpad_not_found"), player.getUniqueID());
      MSP.LOGGER.info(MARKER, "Could not find complete launchpad structure");
      return ActionResultType.FAIL;
    }

    BlockPos[] extremeCorners = launchpad.getExtremeCorners();
    player.sendMessage(
        new TranslationTextComponent(
            "event.msp.launchpad_found",
            extremeCorners[0].getX(),
            extremeCorners[0].getY(),
            extremeCorners[0].getZ(),
            extremeCorners[1].getX(),
            extremeCorners[1].getY(),
            extremeCorners[1].getZ()),
        player.getUniqueID());

    Rocket rocket = Rocket.createFromLaunchpad(launchpad);

    if (player.isSneaking()) {
      //TODO rules
      if (true ||rocket.getRulesRespected()) {
        if (!rocket.getRocketBlocksPos().isEmpty()) {
          if (worldIn instanceof ServerWorld) {

            // TODO have the rocket not tp
            BlockPos rocketPos =
                launchpad.startingPos.add(launchpad.x / 2f, launchpad.y / 2f, launchpad.z / 2f);

            player.sendMessage(
                new TranslationTextComponent("event.msp.attempting_rocket_assembly"),
                player.getUniqueID());

            RocketEntity rocketEntity =
                new RocketEntity(
                    worldIn,
                    new BlockStorage(rocket),
                    rocketPos.getX(),
                    rocketPos.getY(),
                    rocketPos.getZ());

            rocketEntity.setFuel(rocket.fuel);

            MSP.LOGGER.debug(rocket.fuel);

            worldIn.addEntity(rocketEntity);
          }
          launchpad.clear();
        }
      } else {
        player.sendMessage(
            new TranslationTextComponent("event.msp.rocket_rules"), player.getUniqueID());
        return ActionResultType.FAIL;
      }
    }

    return ActionResultType.SUCCESS;
  }

  @SuppressWarnings("deprecation")
  @Override
  @ParametersAreNonnullByDefault
  @Nonnull
  public VoxelShape getShape(
      BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    VoxelShape voxelShape;
    if (state.get(SIDE) == Side.CENTER) voxelShape = CENTER_VOXEL_SHAPE;
    else voxelShape = TOP_VOXEL_SHAPE;
    switch (state.get(FACING)) {
      case SOUTH:
        return rotateY(voxelShape, 2);
      case WEST:
        return rotateY(voxelShape, -1);
      case EAST:
        return rotateY(voxelShape, 1);
      default:
        return voxelShape;
    }
  }

  private enum Side implements IStringSerializable {
    LEFT("left"),
    CENTER("center"),
    RIGHT("right");

    private final String name;

    @Nonnull
    public String func_176610_l() {
      return name;
    }

    Side(String name) {
      this.name = name;
    }
  }
}
