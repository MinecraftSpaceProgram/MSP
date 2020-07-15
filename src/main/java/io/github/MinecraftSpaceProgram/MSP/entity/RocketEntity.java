package io.github.MinecraftSpaceProgram.MSP.entity;

import io.github.MinecraftSpaceProgram.MSP.init.EntityTypes;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.world.World;

public class RocketEntity extends Entity {
    private static final DataParameter<Integer> DIMENSION_ID = EntityDataManager.createKey(RocketEntity.class, DataSerializers.VARINT);
    private int FUEL_CAPACITY;
    private int MAX_THRUST;

    public RocketEntity(net.minecraft.entity.EntityType entityEntityType, World world) {
        super(entityEntityType, world);
        getRocketInformation();
    }

    /**
     * Create rocket entity linked to a dimension via its id
     * @param dimensionId Dimension linked to the rocket
     */
    public RocketEntity(World worldIn, double x, double y, double z, int dimensionId) {
        super(EntityTypes.ROCKET_ENTITY_TYPE.get(), worldIn);
        this.setPosition(x, y, z);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.dataManager.set(DIMENSION_ID, dimensionId);

        getRocketInformation();
    }

    private void getRocketInformation() {
        this.FUEL_CAPACITY = 0;
        this.MAX_THRUST = 0;
    }


    protected void registerData() {
        this.dataManager.register(DIMENSION_ID, 0);
    }

    protected void readAdditional(CompoundNBT compound) {
        if (compound.contains("dimension_id", 3))
            this.dataManager.set(DIMENSION_ID, compound.getInt("dimension_id"));
    }

    protected void writeAdditional(CompoundNBT compound) {
        compound.putInt("dimension_id", this.dataManager.get(DIMENSION_ID));
    }

    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }
}
