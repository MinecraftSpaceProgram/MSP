package io.github.MinecraftSpaceProgram.MSP.block;

import io.github.MinecraftSpaceProgram.MSP.rocket.IMassive;
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
public class EjectorSeatBlock extends EngineAbstract implements IMassive {
    private static final VoxelShape BOTTOM = VoxelShapes.or(
            Block.makeCuboidShape(2.0D,0.0D,2.0D,12.0D,1.0D,12.0D)
    );
    private static final VoxelShape BACK = VoxelShapes.or(
            Block.makeCuboidShape(16.0D,0.0D,16.0D,15.0D,15.0D,16.0D)
    );
    private static final VoxelShape VOXEL_SHAPE = VoxelShapes.or(BACK, BOTTOM);

    public EjectorSeatBlock() {
        super(Properties.create(Material.IRON).notSolid());
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH));
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
    public float getMass() {
        return 250;
    }
}
