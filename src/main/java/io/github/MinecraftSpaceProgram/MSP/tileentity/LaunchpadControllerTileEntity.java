package io.github.MinecraftSpaceProgram.MSP.tileentity;

import io.github.MinecraftSpaceProgram.MSP.init.MSPTileEntityTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class LaunchpadControllerTileEntity extends TileEntity {
    public LaunchpadControllerTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public LaunchpadControllerTileEntity() {
        this(MSPTileEntityTypes.LAUNCHPAD_CONTROLLER.get());
    }
}
