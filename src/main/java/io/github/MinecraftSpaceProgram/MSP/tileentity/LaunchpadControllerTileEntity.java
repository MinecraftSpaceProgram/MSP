package io.github.MinecraftSpaceProgram.MSP.tileentity;

import io.github.MinecraftSpaceProgram.MSP.container.LaunchpadControllerContainer;
import io.github.MinecraftSpaceProgram.MSP.init.MSPTileEntityTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import io.github.MinecraftSpaceProgram.MSP.init.MSPTileEntityTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LaunchpadControllerTileEntity extends TileEntity implements INamedContainerProvider {
    public LaunchpadControllerTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public LaunchpadControllerTileEntity() {
        this(MSPTileEntityTypes.LAUNCHPAD_CONTROLLER.get());
    }

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
        return new LaunchpadControllerContainer(id, playerInventory, this);
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.msp.launchpad_controller");
    }
}
