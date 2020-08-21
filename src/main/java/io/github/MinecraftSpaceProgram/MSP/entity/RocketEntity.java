package io.github.MinecraftSpaceProgram.MSP.entity;

import io.github.MinecraftSpaceProgram.MSP.init.MSPEntityTypes;
import io.github.MinecraftSpaceProgram.MSP.util.BlockStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class RocketEntity extends Entity {
    final BlockStorage storage;

    public RocketEntity(EntityType entityEntityType, World world) {
        super(entityEntityType, world);
        this.storage = null;
    }

    public RocketEntity(World world, BlockStorage storage, double x, double y, double z) {
        super(MSPEntityTypes.ROCKET_ENTITY_TYPE.get(), world);
        this.setPosition(x, y, z);
        this.storage = storage;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }

    @Override
    protected void registerData() {}

    @Override
    protected void readAdditional(@Nonnull CompoundNBT compound) {}

    @Override
    protected void writeAdditional(@Nonnull CompoundNBT compound) {}

    @Nonnull
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }

    public BlockStorage getStorage() {
        return storage;
    }
}
