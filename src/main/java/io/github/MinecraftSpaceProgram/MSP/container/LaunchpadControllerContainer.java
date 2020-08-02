package io.github.MinecraftSpaceProgram.MSP.container;

import io.github.MinecraftSpaceProgram.MSP.init.MSPContainerTypes;
import io.github.MinecraftSpaceProgram.MSP.rocket.Launchpad;
import io.github.MinecraftSpaceProgram.MSP.tileentity.LaunchpadControllerTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import java.util.Objects;

public class LaunchpadControllerContainer extends Container {
    final LaunchpadControllerTileEntity tileEntity;
    final PlayerInventory playerInventory;
    final Launchpad launchpad;

    public LaunchpadControllerContainer(int id, PlayerInventory playerInventory, LaunchpadControllerTileEntity tileEntity) {
        super(MSPContainerTypes.LAUNCHPAD_CONTROLLER.get(), id);
        this.tileEntity = tileEntity;
        this.playerInventory = playerInventory;
        this.launchpad = Launchpad.find(playerInventory.player.world, tileEntity.getPos());
    }

    private static LaunchpadControllerTileEntity getTileEntity(final PlayerInventory inventory, final PacketBuffer data){
        Objects.requireNonNull(inventory, "Player Inventories cannot be null");
        Objects.requireNonNull(data, "Data cannot be null");

        final TileEntity tileEntity = inventory.player.world.getTileEntity(data.readBlockPos());
        if (tileEntity instanceof LaunchpadControllerTileEntity){
            return (LaunchpadControllerTileEntity) tileEntity;
        }
        throw new IllegalStateException("Tile Entity missing " + tileEntity);
    }

    public LaunchpadControllerContainer(final int windowId, final PlayerInventory inventory, final PacketBuffer data){
        this(windowId, inventory, getTileEntity(inventory, data));
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return true;
    }
}
