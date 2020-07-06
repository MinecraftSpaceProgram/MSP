package io.github.MinecraftSpaceProgram.MSP.tileentity;

import io.github.MinecraftSpaceProgram.MSP.block.HangarCorner;
import io.github.MinecraftSpaceProgram.MSP.init.ModTileEntityTypes;
import io.github.MinecraftSpaceProgram.MSP.util.HangarCorners;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class HangarCornerTileEntity extends TileEntity {
    protected HangarCorners associatedCorners;

    public HangarCornerTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public HangarCornerTileEntity() {
        this(ModTileEntityTypes.HANGAR_CORNER_TILEENTITY.get());
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if (this.associatedCorners != null) {
            CompoundNBT c = new CompoundNBT();
            c.putInt("x", associatedCorners.startingPos.getX());
            c.putInt("y", associatedCorners.startingPos.getY());
            c.putInt("z", associatedCorners.startingPos.getZ());
            c.putInt("dx", associatedCorners.x);
            c.putInt("dy", associatedCorners.y);
            c.putInt("dz", associatedCorners.z);
            CompoundNBT MSP = new CompoundNBT();
            MSP.put("hangar", c);
            compound.put("MSP", MSP);
        }

        return super.write(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);

        if (!compound.contains("MSP"))
            return;
        CompoundNBT MSP = compound.getCompound("MSP");
        if (!MSP.contains("hangar"))
            return;
        CompoundNBT hangar = MSP.getCompound("hangar");
        this.associatedCorners = new HangarCorners(
                new BlockPos(hangar.getInt("x"), hangar.getInt("Y"), hangar.getInt("z")),
                hangar.getInt("dx"),
                hangar.getInt("dy"),
                hangar.getInt("dz")
        );
    }

    /**
     * Links hangar blocks and sets BlockState to HANGAR_BUILT = true
     * @param hangarCorners hangar to link
     */
    public void setAssociatedCorners(HangarCorners hangarCorners) {
        if (hangarCorners == null)
            return;

        this.associatedCorners = hangarCorners;
        markDirty();

        if (this.world != null) {
            this.world.setBlockState(
                    pos,
                    this.getBlockState().getBlock().getDefaultState().with(
                            HangarCorner.HANGAR_BUILD,
                            true
                    )
            );
        }
    }

    @Nullable
    public HangarCorners getAssociatedCorners() {
        return this.associatedCorners;
    }

    /**
     * Unlinks associated hangar blocks and sets BlockState to HANGAR_BUILT = false
     */
    public void removeAssociatedCorners() {
        this.associatedCorners = null;
        markDirty();

        if (this.world != null) {
            this.world.setBlockState(
                    pos,
                    this.getBlockState().getBlock().getDefaultState().with(
                            HangarCorner.HANGAR_BUILD,
                            false
                    )
            );
        }
    }
}
