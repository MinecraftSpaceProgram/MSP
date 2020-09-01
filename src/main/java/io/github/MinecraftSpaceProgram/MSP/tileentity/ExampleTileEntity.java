package io.github.MinecraftSpaceProgram.MSP.tileentity;

import io.github.MinecraftSpaceProgram.MSP.init.ModTileEntityTypes;
import io.github.MinecraftSpaceProgram.MSP.init.MSPTileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nonnull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExampleTileEntity extends TileEntity {
    private boolean activated;

    public ExampleTileEntity(TileEntityType<?> typeIn) {
        super(typeIn);
        CompoundNBT tileData = this.getTileData();
        if (tileData.contains("activated"))
            this.activated = tileData.getBoolean("activated");
        else
            this.activated = false;
    }

    public ExampleTileEntity() {
        this(MSPTileEntityTypes.EXAMPLE_TILEENTITY.get());
    }

    @Override
    @Nonnull
    public CompoundNBT write(CompoundNBT compound) {
        compound.putBoolean("activated", this.activated);
        return super.write(compound);
    }

    @Override
    public void func_230337_a_(@Nonnull BlockState p_230337_1_,@Nonnull CompoundNBT compoundNBT) {
        super.func_230337_a_(p_230337_1_, compoundNBT);
        if (compoundNBT.contains("activated"))
            this.activated = compoundNBT.getBoolean("activated");
    }

    public void setActivated() {
        this.activated = !this.activated;
        this.markDirty();
    }

    public boolean getActivated() {
        return this.activated;
    }
}
