package io.github.MinecraftSpaceProgram.MSP.block;

import io.github.MinecraftSpaceProgram.MSP.init.BlockLoader;
import io.github.MinecraftSpaceProgram.MSP.init.ItemLoader;
import io.github.MinecraftSpaceProgram.MSP.init.ModTileEntityTypes;
import io.github.MinecraftSpaceProgram.MSP.item.IHangarController;
import io.github.MinecraftSpaceProgram.MSP.tileentity.HangarCornerTileEntity;
import io.github.MinecraftSpaceProgram.MSP.util.Hangar;
import io.github.MinecraftSpaceProgram.MSP.util.HangarBuilder;
import io.github.MinecraftSpaceProgram.MSP.util.Rocket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class HangarCorner extends Block {
    public static final Property<Boolean> HANGAR_BUILD = BooleanProperty.create("hangar_built");

    public HangarCorner() {
        super(Properties.create(Material.IRON));
    }

    @Override
    public boolean hasTileEntity(BlockState blockState) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState blockState, IBlockReader world) {
        return ModTileEntityTypes.HANGAR_CORNER_TILEENTITY.get().create();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HANGAR_BUILD);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getDefaultState().with(HANGAR_BUILD, false);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack itemStack = player.getHeldItem(handIn);
        if (ItemLoader.HANGAR_CONTROLLER.get() == itemStack.getItem()) {
            if (player.isSneaking()) {
                if (!worldIn.isRemote) {
                    findHangar(worldIn, pos, player, itemStack);
                }
                return ActionResultType.SUCCESS;
            }
        }
        else if (ItemLoader.ROCKET_ASSEMBLER.get() == itemStack.getItem()) {
            if (player.isSneaking()) {
                if (!worldIn.isRemote)
                    assembleRocket(worldIn, pos, player);
                return ActionResultType.SUCCESS;
            }
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() != BlockLoader.HANGAR_CORNER.get()) {
            HangarCornerTileEntity hangarCornerTileEntity = (HangarCornerTileEntity) worldIn.getTileEntity(pos);
            Hangar hangar = hangarCornerTileEntity.getAssociatedCorners();
            if (hangar != null) {
                for (BlockPos cornerPos : hangar.getCorners()) {
                    if (cornerPos.compareTo(pos) != 0)
                        ((HangarCornerTileEntity) worldIn.getTileEntity(cornerPos)).removeAssociatedCorners();
                }
            }
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    private void findHangar(World world, BlockPos pos, PlayerEntity player, ItemStack itemStack) {
        Hangar hangar = new HangarBuilder(world, pos).getCorners();
        if (hangar == null)
            player.sendMessage(new TranslationTextComponent("event.msp.hangar_controller.not_found"), player.getUniqueID());
        else {
            for (BlockPos cornerPos : hangar.getCorners())
                ((HangarCornerTileEntity) world.getTileEntity(cornerPos)).setAssociatedCorners(hangar);

            final BlockPos[] extremeBlocks = hangar.getExtremeCorners();
            final String pos1 = String.format("(%d,%d,%d)", extremeBlocks[0].getX(), extremeBlocks[0].getY(), extremeBlocks[0].getZ());
            final String pos2 = String.format("(%d,%d,%d)", extremeBlocks[1].getX(), extremeBlocks[1].getY(), extremeBlocks[1].getZ());
            player.sendMessage(new TranslationTextComponent("event.msp.hangar_controller.found", pos1, pos2), player.getUniqueID());
            ((IHangarController) itemStack.getItem()).linkHangar(itemStack, pos);
        }
    }

    private void assembleRocket(World world, BlockPos pos, PlayerEntity player) {
        Hangar hangar = ((HangarCornerTileEntity) world.getTileEntity(pos)).getAssociatedCorners();
        if (hangar == null)
            player.sendMessage(new TranslationTextComponent("event.msp.hangar_controller.not_found"), player.getUniqueID());
        else {
            Rocket rocket = Rocket.createFromHangar(hangar, world);
            player.sendMessage(new TranslationTextComponent("event.msp.hangar_corner.rocket_found", rocket.getRocketBlocks().size()), player.getUniqueID());
        }
    }
}
