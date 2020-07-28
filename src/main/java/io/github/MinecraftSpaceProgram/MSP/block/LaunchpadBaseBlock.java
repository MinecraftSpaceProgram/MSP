package io.github.MinecraftSpaceProgram.MSP.block;

import io.github.MinecraftSpaceProgram.MSP.init.BlockLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class LaunchpadBaseBlock extends Block {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;

    public LaunchpadBaseBlock() {
        super(Properties.create(Material.ROCK));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, WEST, EAST);
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        final World world = context.getWorld();
        final BlockPos pos = context.getPos();
        return getDefaultState().with(
                NORTH,
                world.getBlockState(pos.north()).getBlock() == BlockLoader.LAUNCHPAD_BASE.get()
        ).with(
                SOUTH,
                world.getBlockState(pos.south()).getBlock() == BlockLoader.LAUNCHPAD_BASE.get()
        ).with(
                WEST,
                world.getBlockState(pos.west()).getBlock() == BlockLoader.LAUNCHPAD_BASE.get()
        ).with(
                EAST,
                world.getBlockState(pos.east()).getBlock() == BlockLoader.LAUNCHPAD_BASE.get()
        );
    }

    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    @Nonnull
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing.getAxis().getPlane() == Direction.Plane.HORIZONTAL) {
            BooleanProperty facingProperty = null;
            if (facing == Direction.NORTH)
                facingProperty = NORTH;
            if (facing == Direction.SOUTH)
                facingProperty = SOUTH;
            if (facing == Direction.WEST)
                facingProperty = WEST;
            if (facing == Direction.EAST)
                facingProperty = EAST;

            if (facingProperty != null)
                return stateIn.with(facingProperty, facingState.getBlock() == BlockLoader.LAUNCHPAD_BASE.get());
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }
}
