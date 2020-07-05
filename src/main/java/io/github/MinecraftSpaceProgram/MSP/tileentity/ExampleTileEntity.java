package io.github.MinecraftSpaceProgram.MSP.tileentity;

import io.github.MinecraftSpaceProgram.MSP.init.ModTileEntityTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class ExampleTileEntity extends LockableLootTileEntity {
    private boolean activated;

    public ExampleTileEntity(TileEntityType<?> typeIn) {
        super(typeIn);
        this.activated = false;
    }

    public ExampleTileEntity() {
        this(ModTileEntityTypes.EXAMPLE_TILEENTITY.get());
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return null;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {

    }

    @Override
    protected ITextComponent getDefaultName() {
        return null;
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return null;
    }

    @Override
    public int getSizeInventory() {
        return 0;
    }

    public void setActivated() {
        this.activated = !this.activated;
    }

    public boolean getActivated() {
        return this.activated;
    }
}
