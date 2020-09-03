package io.github.MinecraftSpaceProgram.MSP.tileentity;

import io.github.MinecraftSpaceProgram.MSP.init.MSPTileEntityTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class HydrazineTankTileEntity extends TileEntity implements IRocketTank {
  public HydrazineTankTileEntity(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
  }

  public HydrazineTankTileEntity() {
    this(MSPTileEntityTypes.HYDRAZINE_TANK_TILE_ENTITY.get());
  }

  @Override
  public float getCapacity() {
    return 1004.5F;
  }

  @Override
  public float getFuelLevel() {
    return getCapacity();
  }

  @Override
  public String getFuelType() {
    return "HYDRAZINE";
  }
}
