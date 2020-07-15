package io.github.MinecraftSpaceProgram.MSP.container;

import io.github.MinecraftSpaceProgram.MSP.init.BlockInitNew;
import io.github.MinecraftSpaceProgram.MSP.init.ModContainerTypes;
import io.github.MinecraftSpaceProgram.MSP.tileentity.FlightComputerTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

import java.util.Objects;

public class FlightComputerContainer extends Container {
    public final FlightComputerTileEntity tileEntity;
    public final IWorldPosCallable canInteractWithCallable;

    public FlightComputerContainer(final int windowId, final PlayerInventory playerInventory, final FlightComputerTileEntity tileEntity) {
        super(ModContainerTypes.FLIGHT_COMPUTER.get(), windowId);

        this.tileEntity = tileEntity;
        this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());

        // Main Inventory
        int startX = 3;
        int startY = 3;

        this.addSlot(new Slot(tileEntity,0, startX, startY));
    }

    private static FlightComputerTileEntity getTileEntity(final PlayerInventory inventory, final PacketBuffer data){
        Objects.requireNonNull(inventory, "Player Inventories cannot be null");
        Objects.requireNonNull(data, "Data cannot be null");

        final TileEntity tile = inventory.player.world.getTileEntity(data.readBlockPos());
        if (tile instanceof FlightComputerTileEntity){
            return (FlightComputerTileEntity) tile;
        }
        throw new IllegalStateException("Tile Entity is not correct " + tile);
    }

    public FlightComputerContainer(final int windowId, final PlayerInventory inventory, final PacketBuffer data){
        this(windowId, inventory, getTileEntity(inventory, data));
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(canInteractWithCallable, playerIn, BlockInitNew.FLIGHT_COMPUTER.get());
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if(slot != null && slot.getHasStack()){
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();
            if(index < 1) {
                if (!this.mergeItemStack(itemStack1, 1, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemStack1, 0, 1, true)) {
                return ItemStack.EMPTY;
            }
            if(itemStack1.isEmpty()){
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemStack;
    }
}
