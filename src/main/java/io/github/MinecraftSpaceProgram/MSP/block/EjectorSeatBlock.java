package io.github.MinecraftSpaceProgram.MSP.block;

import io.github.MinecraftSpaceProgram.MSP.entity.SeatEntity;
import io.github.MinecraftSpaceProgram.MSP.rocket.IMassive;
import io.github.MinecraftSpaceProgram.MSP.util.VoxelShapesUtil;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class EjectorSeatBlock extends Block implements IMassive {

    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    private static final VoxelShape BOTTOM = VoxelShapes.or(
            Block.makeCuboidShape(2.0D,0.0D,2.0D,14.0D,1.0D,16.0D)
    );
    private static final VoxelShape BACK = VoxelShapes.or(
            Block.makeCuboidShape(4.0D,0.0D,15.0D,12.0D,20.0D,16.0D)
    );
    private static final VoxelShape VOXEL_SHAPE = VoxelShapes.or(BACK, BOTTOM);

    public EjectorSeatBlock() {
        super(AbstractBlock.Properties.create(Material.IRON).notSolid());
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
    }

    @OnlyIn(Dist.CLIENT)
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1.0F;
    }

    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    @Override
    @Nonnull
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(FACING)) {
            case SOUTH:
                return VoxelShapesUtil.rotateY(VOXEL_SHAPE, 2);
            case EAST:
                return VoxelShapesUtil.rotateY(VOXEL_SHAPE, 1);
            case WEST:
                return VoxelShapesUtil.rotateY(VOXEL_SHAPE, -1);
            default:
                return VOXEL_SHAPE;
        }
    }

    @Override
    public float getMass() {
        return 250;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        return SeatEntity.create(worldIn, pos, 0, player);
    }
}
