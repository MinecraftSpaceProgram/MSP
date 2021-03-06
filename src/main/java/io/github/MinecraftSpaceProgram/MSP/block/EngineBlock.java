package io.github.MinecraftSpaceProgram.MSP.block;

import io.github.MinecraftSpaceProgram.MSP.rocket.IMassive;
import io.github.MinecraftSpaceProgram.MSP.rocket.IRocketEngine;
import io.github.MinecraftSpaceProgram.MSP.rocket.IRocketTank;
import io.github.MinecraftSpaceProgram.MSP.util.VoxelShapesUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
public class EngineBlock extends EngineAbstract implements IRocketEngine, IMassive {
    private static final VoxelShape TAILPIPE = VoxelShapes.or(
            Block.makeCuboidShape(1.0D,0.0D,1.0D,15.0D,3.0D,15.0D),
            Block.makeCuboidShape(2.0D,3.0D,2.0D,14.0D,6.0D,14.0D),
            Block.makeCuboidShape(3.0D,6.0D,3.0D,13.0D,9.0D,13.0D),
            Block.makeCuboidShape(4.0D,9.0D,4.0D,12.0D,12.0D,12.0D)
    );
    private static final VoxelShape EXHAUST = VoxelShapes.or(
            Block.makeCuboidShape(0.0D,0.0D,5.0D,1.0D,4.0D,7.0D),
            Block.makeCuboidShape(1.0D,3.0D,5.0D,2.0D,7.0D,7.0D),
            Block.makeCuboidShape(2.0D,6.0D,5.0D,3.0D,10.0D,7.0D),
            Block.makeCuboidShape(3.0D,9.0D,5.0D,4.0D,12.0D,7.0D)
    );
    private static final VoxelShape TOP = VoxelShapes.or(
            Block.makeCuboidShape(3.0D,12.0D,3.0D,13.0D,13.0D,13.0D),
            Block.makeCuboidShape(2.0D,13.0D,2.0D,14.0D,16.0D,14.0D)
    );
    private static final VoxelShape VOXEL_SHAPE = VoxelShapes.or(TOP, EXHAUST, TAILPIPE);

    public EngineBlock() {
        super(Properties.create(Material.GLASS).notSolid());
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.DOWN));
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
        switch (state.get(EngineAbstract.FACING)) {
            case DOWN:
                return VoxelShapesUtil.rotateX(VOXEL_SHAPE, 2);
            case NORTH:
                return VoxelShapesUtil.rotateX(VOXEL_SHAPE, 1);
            case SOUTH:
                return VoxelShapesUtil.rotateX(VOXEL_SHAPE, -1);
            case EAST:
                return VoxelShapesUtil.rotateY(VoxelShapesUtil.rotateX(VOXEL_SHAPE, 1), 1);
            case WEST:
                return VoxelShapesUtil.rotateY(VoxelShapesUtil.rotateX(VOXEL_SHAPE, 1), -1);
            default:
                return VOXEL_SHAPE;
        }
    }

    @Override
    public IRocketTank.fuelTypes getFuelType() {
        return IRocketTank.fuelTypes.HYDRAZINE;
    }

    @Override
    //TODO find values
    // Right now using Merlin 1D
    public float getThrust() {
        return 845000.0F;
    }

    @Override
    //TODO find values
    public float getFlowRate() {
        return 305.0F;
    }

    @Override
    public float getMass() {
        return 470;
    }
}
