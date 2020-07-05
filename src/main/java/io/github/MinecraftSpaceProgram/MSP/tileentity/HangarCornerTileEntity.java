package io.github.MinecraftSpaceProgram.MSP.tileentity;

import io.github.MinecraftSpaceProgram.MSP.util.HangarBuilderCorners;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;

public class HangarCornerTileEntity extends TileEntity {
    protected HangarBuilderCorners associatedCorners;

    public HangarCornerTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public void setAssociatedCorners(HangarBuilderCorners hangarBuilderCorners) {
        this.associatedCorners = hangarBuilderCorners;
    }

    @Nullable
    public HangarBuilderCorners getAssociatedCorners() {
        return this.associatedCorners;
    }

    public void removeAssociatedCorners() {
        this.associatedCorners = null;
    }
}
