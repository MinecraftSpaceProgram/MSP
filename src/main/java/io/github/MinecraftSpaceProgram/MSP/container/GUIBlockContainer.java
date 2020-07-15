package io.github.MinecraftSpaceProgram.MSP.container;

import io.github.MinecraftSpaceProgram.MSP.init.ModContainerTypes;
import io.github.MinecraftSpaceProgram.MSP.tileentity.GUIBlockTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;

import java.util.Objects;

public class GUIBlockContainer extends Container {

    public GUIBlockContainer(final int windowId, final PlayerInventory playerInventory, final GUIBlockTileEntity tileEntity) {
        super(ModContainerTypes.GUI_BLOCK.get(), windowId);
    }

    private static GUIBlockTileEntity getTileEntity(final PlayerInventory inventory, final PacketBuffer data){
        Objects.requireNonNull(inventory, "Player Inventories cannot be null");
        Objects.requireNonNull(data, "Data cannot be null");

        final TileEntity tile = inventory.player.world.getTileEntity(data.readBlockPos());
        if (tile instanceof GUIBlockTileEntity){
            return (GUIBlockTileEntity) tile;
        }
        throw new IllegalStateException("Tile Entity is not correct " + tile);
    }

    public GUIBlockContainer(final int windowId, final PlayerInventory inventory, final PacketBuffer data){
        this(windowId, inventory, getTileEntity(inventory, data));
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}
