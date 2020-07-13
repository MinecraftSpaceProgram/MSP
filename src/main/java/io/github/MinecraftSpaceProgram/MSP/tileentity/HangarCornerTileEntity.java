package io.github.MinecraftSpaceProgram.MSP.tileentity;

import io.github.MinecraftSpaceProgram.MSP.block.HangarCorner;
import io.github.MinecraftSpaceProgram.MSP.init.ModTileEntityTypes;
import io.github.MinecraftSpaceProgram.MSP.util.Hangar;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class HangarCornerTileEntity extends TileEntity {
    protected Hangar associatedCorners;

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
    public void func_230337_a_(BlockState p_230337_1_, CompoundNBT compound) {
        super.func_230337_a_(p_230337_1_, compound);

        if (!compound.contains("MSP"))
            return;
        CompoundNBT MSP = compound.getCompound("MSP");
        if (!MSP.contains("hangar"))
            return;
        CompoundNBT hangar = MSP.getCompound("hangar");
        this.associatedCorners = new Hangar(
                new BlockPos(hangar.getInt("x"), hangar.getInt("y"), hangar.getInt("z")),
                hangar.getInt("dx"),
                hangar.getInt("dy"),
                hangar.getInt("dz")
        );
    }

    /**
     * Links hangar blocks and sets BlockState to HANGAR_BUILT = true
     * @param hangar hangar to link
     */
    public void setAssociatedCorners(Hangar hangar) {
        if (hangar == null)
            return;

        this.associatedCorners = hangar;
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
    public Hangar getAssociatedCorners() {
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
