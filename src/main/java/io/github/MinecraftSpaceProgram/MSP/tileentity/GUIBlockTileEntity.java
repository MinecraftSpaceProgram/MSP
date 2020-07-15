package io.github.MinecraftSpaceProgram.MSP.tileentity;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.container.GUIBlockContainer;
import io.github.MinecraftSpaceProgram.MSP.init.ModTileEntityTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GUIBlockTileEntity extends TileEntity implements INamedContainerProvider {

    public GUIBlockTileEntity(TileEntityType<?> tileEntityTypeIn) { super(tileEntityTypeIn); }

    public GUIBlockTileEntity(){
        this(ModTileEntityTypes.GUI_BLOCK.get());
    }


    @Nullable
    @Override
    public Container createMenu(int windowId,@Nonnull PlayerInventory playerInventory,@Nonnull PlayerEntity playerEntity) {
        MSP.LOGGER.debug("creating a menu");
        return new GUIBlockContainer(windowId, playerInventory, this);
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        return new StringTextComponent("gui");
    }
}
