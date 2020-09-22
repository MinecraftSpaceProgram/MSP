package io.github.MinecraftSpaceProgram.MSP.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;

public class PlanetEntity extends Entity {
  public PlanetEntity(EntityType<?> entityTypeIn, World worldIn) {
    super(entityTypeIn, worldIn);
  }

  @Override
  protected void registerData() {

  }

  @Override
  protected void readAdditional(CompoundNBT compound) {

  }

  @Override
  protected void writeAdditional(CompoundNBT compound) {

  }

  @Override
  public IPacket<?> createSpawnPacket() {
    return null;
  }
}
