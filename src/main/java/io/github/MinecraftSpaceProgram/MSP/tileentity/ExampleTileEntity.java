package io.github.MinecraftSpaceProgram.MSP.tileentity;

import io.github.MinecraftSpaceProgram.MSP.init.ModTileEntityTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class ExampleTileEntity extends TileEntity {
    private boolean activated;

    public ExampleTileEntity(TileEntityType<?> typeIn) {
        super(typeIn);
        this.activated = false;
    }

    public ExampleTileEntity() {
        this(ModTileEntityTypes.EXAMPLE_TILEENTITY.get());
    }

    public void setActivated() {
        this.activated = !this.activated;
    }

    public boolean getActivated() {
        return this.activated;
    }
}
