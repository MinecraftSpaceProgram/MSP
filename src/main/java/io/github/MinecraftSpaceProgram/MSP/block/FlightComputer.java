package io.github.MinecraftSpaceProgram.MSP.block;

import io.github.MinecraftSpaceProgram.MSP.init.ModTileEntityTypes;
import io.github.MinecraftSpaceProgram.MSP.tileentity.FlightComputerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class FlightComputer extends Block {

    public FlightComputer(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.FLIGHT_COMPUTER.get().create();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if(tile instanceof FlightComputerTileEntity) {
                NetworkHooks.openGui((ServerPlayerEntity) player, (FlightComputerTileEntity)tile, pos);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if(state.getBlock() != newState.getBlock()) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if(tile instanceof FlightComputerTileEntity){
                InventoryHelper.dropItems(worldIn, pos, ((FlightComputerTileEntity) tile).getItems());
            }
        }
    }
}
