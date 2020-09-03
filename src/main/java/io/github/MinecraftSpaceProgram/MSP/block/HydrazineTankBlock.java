package io.github.MinecraftSpaceProgram.MSP.block;

import io.github.MinecraftSpaceProgram.MSP.init.MSPBlocks;
import io.github.MinecraftSpaceProgram.MSP.init.MSPTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class HydrazineTankBlock extends Block {

  public static final BooleanProperty UP = BlockStateProperties.UP;
  public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

  private static final VoxelShape CENTER_VOXEL_SHAPE =
      VoxelShapes.or(Block.makeCuboidShape(2.0D, 2.0D, 2.0D, 14.0D, 14.0D, 14.0D));

  public HydrazineTankBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(UP, DOWN);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @OnlyIn(Dist.CLIENT)
  public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return 1.0F;
  }

  public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
    return true;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return MSPTileEntityTypes.HYDRAZINE_TANK_TILE_ENTITY.get().create();
  }

  @SuppressWarnings("deprecation")
  @Override
  @Nonnull
  public VoxelShape getShape(
      BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return CENTER_VOXEL_SHAPE;
  }

  public BlockState getStateForPlacement(BlockItemUseContext context) {
    final World world = context.getWorld();
    final BlockPos pos = context.getPos();
    return getDefaultState()
        .with(UP, world.getBlockState(pos.up()).getBlock() == MSPBlocks.HYDRAZINE_TANK_BLOCK.get())
        .with(
            DOWN,
            world.getBlockState(pos.down()).getBlock() == MSPBlocks.HYDRAZINE_TANK_BLOCK.get());
  }

  @SuppressWarnings("deprecation")
  @ParametersAreNonnullByDefault
  @Nonnull
  public BlockState updatePostPlacement(
      BlockState stateIn,
      Direction facing,
      BlockState facingState,
      IWorld worldIn,
      BlockPos currentPos,
      BlockPos facingPos) {
    if (facing.getAxis().getPlane() == Direction.Plane.VERTICAL) {
      BooleanProperty facingProperty = null;
      if (facing == Direction.UP) {
        facingProperty = UP;
      }
      if (facing == Direction.DOWN) {
        facingProperty = DOWN;
      }

      if (facingProperty != null)
        return stateIn.with(
            facingProperty, facingState.getBlock() == MSPBlocks.HYDRAZINE_TANK_BLOCK.get());
    }
    return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
  }
}
