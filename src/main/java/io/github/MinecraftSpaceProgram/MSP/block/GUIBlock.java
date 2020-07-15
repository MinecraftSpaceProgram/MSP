package io.github.MinecraftSpaceProgram.MSP.block;

import io.github.MinecraftSpaceProgram.MSP.Main;
import io.github.MinecraftSpaceProgram.MSP.init.ModTileEntityTypes;
import io.github.MinecraftSpaceProgram.MSP.tileentity.GUIBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class GUIBlock extends Block{
    public GUIBlock() {
        super(Block.Properties.create(Material.IRON));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.GUI_BLOCK.get().create();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isRemote) {
            Main.LOGGER.debug("clicked on gui block");
            TileEntity tile = worldIn.getTileEntity(pos);
            if(tile instanceof GUIBlockTileEntity) {
                //player.openContainer(new GUIBlockContainer());
                NetworkHooks.openGui((ServerPlayerEntity) player, (GUIBlockTileEntity)tile, pos);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }
}
