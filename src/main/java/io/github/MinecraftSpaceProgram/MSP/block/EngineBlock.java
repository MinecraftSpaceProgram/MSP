package io.github.MinecraftSpaceProgram.MSP.block;

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

public class EngineBlock extends EngineAbstract {
    private static final VoxelShape TAILPIPE = VoxelShapes.or(
            Block.makeCuboidShape(1.0D,0.0D,1.0D,15.0D,3.0D,15.0D),
            VoxelShapes.or(
                    Block.makeCuboidShape(2.0D,3.0D,2.0D,14.0D,6.0D,14.0D),
                    VoxelShapes.or(
                            Block.makeCuboidShape(3.0D,6.0D,3.0D,13.0D,9.0D,13.0D),
                            Block.makeCuboidShape(4.0D,9.0D,4.0D,12.0D,12.0D,12.0D)
                    )
            )
    );
    private static final VoxelShape EXHAUST = VoxelShapes.or(
            Block.makeCuboidShape(0.0D,0.0D,5.0D,1.0D,4.0D,7.0D),
            VoxelShapes.or(
                    Block.makeCuboidShape(1.0D,3.0D,5.0D,2.0D,7.0D,7.0D),
                    VoxelShapes.or(
                            Block.makeCuboidShape(2.0D,6.0D,5.0D,3.0D,10.0D,7.0D),
                            Block.makeCuboidShape(3.0D,9.0D,5.0D,4.0D,12.0D,7.0D)
                    )
            )
    );
    private static final VoxelShape TOP = VoxelShapes.or(
            Block.makeCuboidShape(3.0D,12.0D,3.0D,13.0D,13.0D,13.0D),
            Block.makeCuboidShape(2.0D,13.0D,2.0D,14.0D,16.0D,14.0D)
    );
    private static final VoxelShape VOXEL_SHAPE = VoxelShapes.or(TOP, VoxelShapes.or(EXHAUST, TAILPIPE));

    public EngineBlock() {
        super(Properties.create(Material.GLASS).notSolid());
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.DOWN));
    }

    public boolean isNormalCube(IBlockReader reader, BlockPos pos) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1.0F;
    }

    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    @Override
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
}
