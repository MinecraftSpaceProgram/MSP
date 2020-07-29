package io.github.MinecraftSpaceProgram.MSP.block;

import io.github.MinecraftSpaceProgram.MSP.tileentity.LaunchpadControllerTileEntity;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class LaunchpadControllerBlock extends Block {
    public static final EnumProperty<side> SIDE = EnumProperty.create("side", side.class);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public LaunchpadControllerBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.getStateContainer().getBaseState().with(SIDE, side.CENTER));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        if (state.get(SIDE) == side.CENTER)
            return new LaunchpadControllerTileEntity();
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
        Direction facing = context.getPlacementHorizontalFacing();
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
            worldIn.setBlockState(pos.offset(facing.rotateY()), state.with(SIDE, side.RIGHT), 3);
            worldIn.setBlockState(pos.offset(facing.rotateYCCW()), state.with(SIDE, side.LEFT), 3);
            worldIn.func_230547_a_(pos, Blocks.AIR);
            state.func_235734_a_(worldIn, pos, 3);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!worldIn.isRemote) {
            side thisSide = state.get(SIDE);
            Direction facing = state.get(FACING);
            BlockPos leftPos;
            BlockPos rightPos;
            BlockPos centerPos;
            if (thisSide == side.LEFT) {
                leftPos = pos;
                centerPos = pos.offset(facing.rotateY());
                rightPos = centerPos.offset(facing.rotateY());
            }
            else if (thisSide == side.RIGHT) {
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

            if (thisSide != side.LEFT && leftState.getBlock() == this && leftState.get(SIDE) == side.LEFT)
                worldIn.setBlockState(leftPos, Blocks.AIR.getDefaultState(), 35);
            if (thisSide != side.RIGHT && rightState.getBlock() == this && rightState.get(SIDE) == side.RIGHT)
                worldIn.setBlockState(rightPos, Blocks.AIR.getDefaultState(), 35);
            if (thisSide != side.CENTER && centerState.getBlock() == this && centerState.get(SIDE) == side.CENTER)
                worldIn.setBlockState(centerPos, Blocks.AIR.getDefaultState(), 35);
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @SuppressWarnings("deprecation")
    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    private enum side implements IStringSerializable {
        LEFT("left"),
        CENTER("center"),
        RIGHT("right");

        private final String name;

        @Nonnull
        public String func_176610_l() {
            return name;
        }

        side(String name) {
            this.name = name;
        }
    }
}
