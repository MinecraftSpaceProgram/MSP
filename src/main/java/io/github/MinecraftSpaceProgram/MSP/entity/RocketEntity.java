package io.github.MinecraftSpaceProgram.MSP.entity;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.init.MSPBlocks;
import io.github.MinecraftSpaceProgram.MSP.init.MSPEntityTypes;
import io.github.MinecraftSpaceProgram.MSP.util.BlockStorage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class RocketEntity extends Entity implements IEntityAdditionalSpawnData {
    //private static final DataParameter<BlockStorage> STORAGE = EntityDataManager.createKey(RocketEntity.class, DataSerializers.BLOCK_POS);
    public BlockStorage storage;

    private int tickCounter = 0;

    public RocketEntity(EntityType<? extends RocketEntity> entityEntityType, World world) {
        super(entityEntityType, world);
        MSP.LOGGER.debug("SUMMONED A ROCKET");
        this.storage = new BlockStorage(
                new BlockState[][][]{{{MSPBlocks.ROCKET_GENERIC.get().getDefaultState()}}},
                1,
                1,
                1,
                1,
                1,
                1,
                1
        );
    }

    public RocketEntity(World world, BlockStorage storage, double x, double y, double z) {
        super(MSPEntityTypes.ROCKET_ENTITY_TYPE.get(), world);
        this.storage = storage;
        this.setPosition(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();
        tickCounter ++;
        if(tickCounter > 360){
            tickCounter = 0;
        }
        this.rotationYaw = tickCounter;
        //this.setPosition(this.getPosX(), this.getPosY() + (tickCounter / 360.0f) - 0.5f, this.getPosZ());
    }


    @Override
    protected void registerData() {}

    @Override
    protected void readAdditional(@Nonnull CompoundNBT compound) {
        //this.storage = new BlockStorage(compound.getCompound("storage"));
    }

    @Override
    protected void writeAdditional(@Nonnull CompoundNBT compound) {
        //compound.put("storage", this.storage.toNBT());
    }

    public BlockStorage getStorage() {
        return storage;
    }

    @OnlyIn(Dist.CLIENT)
    public World getWorldObj() {
        return this.world;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!(source.getTrueSource() instanceof PlayerEntity && ((PlayerEntity) source.getTrueSource()).abilities.isCreativeMode)
                && world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS) && !this.removed) {
            entityDropItem(Items.STICK);
        }
        if (!this.world.isRemote && !this.removed) {
            remove();
            return true;
        }
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected AxisAlignedBB getBoundingBox(Pose pose) {
        AxisAlignedBB COLLISION_AABB = new AxisAlignedBB(-1, 0, -1, 1, 0.5, 1);
        return COLLISION_AABB.offset(getPositionVec());
    }

    @Nonnull
    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer dataStream) {
        dataStream.writeCompoundTag(this.storage.toNBT());
    }

    @Override
    public void readSpawnData(PacketBuffer dataStream) {
        MSP.LOGGER.debug("spawn data");
        this.storage = new BlockStorage(dataStream.readCompoundTag());
        MSP.LOGGER.debug("number :" + this.storage.numberOfBlocks);
    }


}
