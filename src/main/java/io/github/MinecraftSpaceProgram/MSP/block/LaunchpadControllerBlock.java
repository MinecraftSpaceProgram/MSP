package io.github.MinecraftSpaceProgram.MSP.block;

import io.github.MinecraftSpaceProgram.MSP.init.MSPTileEntityTypes;
import io.github.MinecraftSpaceProgram.MSP.tileentity.LaunchpadControllerTileEntity;
import io.github.MinecraftSpaceProgram.MSP.init.MSPTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static io.github.MinecraftSpaceProgram.MSP.util.VoxelShapesUtil.rotateY;

public class LaunchpadControllerBlock extends Block {
    private static final EnumProperty<Side> SIDE = EnumProperty.create("side", Side.class);
    private static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape TOP_VOXEL_SHAPE = VoxelShapes.or(
            Block.makeCuboidShape(0.0D, 10.0D, 1.0D, 16.0D, 14.0D, 5.333333D),
            Block.makeCuboidShape(0.0D, 12.0D, 5.333333D, 16.0D, 16.0D, 9.666667D),
            Block.makeCuboidShape(0.0D, 14.0D, 9.666667D, 16.0D, 18.0D, 14.0D)
    );
    private static final VoxelShape CENTER_VOXEL_SHAPE = VoxelShapes.or(
            TOP_VOXEL_SHAPE,
            Block.makeCuboidShape(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D),
            Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D)
    );

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
        if (!world.getBlockState(pos.offset(facing.rotateY())).isReplaceable(context))
            return null;
        if (!world.getBlockState(pos.offset(facing.rotateYCCW())).isReplaceable(context))
            return null;
        return getDefaultState().with(FACING, facing);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
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
            }
            else if (thisSide == Side.RIGHT) {
                rightPos = pos;
                centerPos = pos.offset(facing.rotateYCCW());
                leftPos = centerPos.offset(facing.rotateYCCW());
            }
            else {
                centerPos = pos;
                leftPos = pos.offset(facing.rotateYCCW());
                rightPos = pos.offset(facing.rotateY());
            }
            BlockState centerState = worldIn.getBlockState(centerPos);
            BlockState leftState = worldIn.getBlockState(leftPos);
            BlockState rightState = worldIn.getBlockState(rightPos);

            if (thisSide != Side.LEFT && leftState.getBlock() == this && leftState.get(SIDE) == Side.LEFT)
                worldIn.setBlockState(leftPos, Blocks.AIR.getDefaultState(), 35);
            if (thisSide != Side.RIGHT && rightState.getBlock() == this && rightState.get(SIDE) == Side.RIGHT)
                worldIn.setBlockState(rightPos, Blocks.AIR.getDefaultState(), 35);
            if (thisSide != Side.CENTER && centerState.getBlock() == this && centerState.get(SIDE) == Side.CENTER)
                worldIn.setBlockState(centerPos, Blocks.AIR.getDefaultState(), 35);
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @SuppressWarnings("deprecation")
    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            BlockPos tileEntityPos;
            Direction facing = state.get(FACING);
            if (state.get(SIDE) == Side.LEFT)
                tileEntityPos = pos.offset(facing.rotateY());
            else if (state.get(SIDE) == Side.RIGHT)
                tileEntityPos = pos.offset(facing.rotateYCCW());
            else
                tileEntityPos = pos;

            TileEntity tileEntity = world.getTileEntity(tileEntityPos);
            if (tileEntity instanceof LaunchpadControllerTileEntity) {
                NetworkHooks.openGui((ServerPlayerEntity) player, (LaunchpadControllerTileEntity) tileEntity);
                return ActionResultType.SUCCESS;
            }
            else
                throw new IllegalStateException("Tile Entity missing " + tileEntity);
        }
        return super.onBlockActivated(state, world, pos, player, handIn, hit);
    }

    @SuppressWarnings("deprecation")
    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        VoxelShape voxelShape;
        if (state.get(SIDE) == Side.CENTER)
            voxelShape = CENTER_VOXEL_SHAPE;
        else
            voxelShape = TOP_VOXEL_SHAPE;
        switch(state.get(FACING)) {
            default:
                return voxelShape;
            case SOUTH:
                return rotateY(voxelShape, 2);
            case WEST:
                return rotateY(voxelShape, -1);
            case EAST:
                return rotateY(voxelShape, 1);
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
