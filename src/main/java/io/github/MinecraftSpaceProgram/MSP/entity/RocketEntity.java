package io.github.MinecraftSpaceProgram.MSP.entity;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.init.MSPBlocks;
import io.github.MinecraftSpaceProgram.MSP.init.MSPEntityTypes;
import io.github.MinecraftSpaceProgram.MSP.util.BlockStorage;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RocketEntity extends Entity implements IEntityAdditionalSpawnData {
    protected static final DataParameter<Integer> FUEL = EntityDataManager.createKey(RocketEntity.class, DataSerializers.VARINT);
    //private static final DataParameter<BlockStorage> STORAGE = EntityDataManager.createKey(RocketEntity.class, DataSerializers.BLOCK_POS);
    public BlockStorage storage;
    private float momentum;
    private float deltaRotation;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYaw;
    private double lerpPitch;
    private boolean leftInputDown;
    private boolean rightInputDown;
    private boolean forwardInputDown;
    private boolean backInputDown;
    private float boatGlide;

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
        this.tickLerp();
        if (this.canPassengerSteer()) {
            this.updateMotion();
            if (this.world.isRemote) {
                this.controlBoat();
            }

            this.move(MoverType.SELF, this.getMotion());
        } else {
            this.setMotion(Vector3d.ZERO);
        }

        this.doBlockCollisions();
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow( 0.2F, -0.01F,0.2F), EntityPredicates.pushableBy(this));
        if (!list.isEmpty()) {
            boolean flag = !this.world.isRemote && !(this.getControllingPassenger() instanceof PlayerEntity);

            for (Entity entity : list) {
                if (!entity.isPassenger(this)) {
                    if (flag && this.getPassengers().size() < 2 && !entity.isPassenger() && entity.getWidth() < this.getWidth() && entity instanceof LivingEntity && !(entity instanceof WaterMobEntity) && !(entity instanceof PlayerEntity)) {
                        entity.startRiding(this);
                    } else {
                        this.applyEntityCollision(entity);
                    }
                }
            }
        }

    }

    private void tickLerp() {
        if (this.canPassengerSteer()) {
            this.lerpSteps = 0;
            this.setPacketCoordinates(this.getPosX(), this.getPosY(), this.getPosZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.getPosX() + (this.lerpX - this.getPosX()) / (double) this.lerpSteps;
            double d1 = this.getPosY() + (this.lerpY - this.getPosY()) / (double) this.lerpSteps;
            double d2 = this.getPosZ() + (this.lerpZ - this.getPosZ()) / (double) this.lerpSteps;
            double d3 = MathHelper.wrapDegrees(this.lerpYaw - (double) this.rotationYaw);
            this.rotationYaw = (float) ((double) this.rotationYaw + d3 / (double) this.lerpSteps);
            this.rotationPitch = (float) ((double) this.rotationPitch + (this.lerpPitch - (double) this.rotationPitch) / (double) this.lerpSteps);
            --this.lerpSteps;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
    }

    @Override
    protected void registerData() {
        dataManager.register(FUEL, 0);
    }

    public int getFuel() {
        return dataManager.get(FUEL);
    }

    public boolean isPowered() {
        return dataManager.get(FUEL) > 0;
    }

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

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats.
     */
    @Nullable
    @Override
    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return entityIn.canBePushed() ? entityIn.getBoundingBox() : null;
    }

    /**
     * Returns the <b>solid</b> collision bounding box for this entity. Used to make (e.g.) boats solid. Return null if
     * this entity is not solid.
     */
    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return this.getBoundingBox();
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    @Override
    public boolean canBePushed() {
        return true;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    //TODO SEATS
    @Override
    public double getMountedYOffset() {
        return -0.1D;
    }

    /**
     * Applies a velocity to the entities, to push them away from eachother.
     */
    @Override
    public void applyEntityCollision(Entity entityIn) {
        if (entityIn instanceof BoatEntity) {
            if (entityIn.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.applyEntityCollision(entityIn);
            }
        } else if (entityIn.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.applyEntityCollision(entityIn);
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    @Override
    public boolean canBeCollidedWith() {
        return !this.removed;
    }

    /**
     * Sets a target for the client to interpolate towards over the next few ticks
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = yaw;
        this.lerpPitch = pitch;
        this.lerpSteps = 10;
    }

    /**
     * Gets the horizontal facing direction of this Entity, adjusted to take specially-treated entity types into account.
     */
    @Override
    public Direction getAdjustedHorizontalFacing() {
        return this.getHorizontalFacing().rotateY();
    }

    /**
     * Update the boat's speed, based on momentum.
     */
    private void updateMotion() {
        double d1 = this.hasNoGravity() ? 0.0D : (double) -0.04F;
        this.momentum = 0.05F;
        this.momentum = this.boatGlide;

        if (this.getControllingPassenger() instanceof PlayerEntity) {
            this.boatGlide /= 2.0F;
        }

        Vector3d vector3d = this.getMotion();
        this.setMotion(vector3d.x * (double) this.momentum, vector3d.y + d1, vector3d.z * (double) this.momentum);
        this.deltaRotation *= this.momentum;
    }

    private void controlBoat() {
        if (this.isBeingRidden()) {
            float f = 0.0F;
            if (this.leftInputDown) {
                --this.deltaRotation;
            }

            if (this.rightInputDown) {
                ++this.deltaRotation;
            }

            if (this.rightInputDown != this.leftInputDown && !this.forwardInputDown && !this.backInputDown) {
                f += 0.005F;
            }

            this.rotationYaw += this.deltaRotation;
            if (this.forwardInputDown) {
                f += 0.04F;
            }

            if (this.backInputDown) {
                f -= 0.005F;
            }

            this.setMotion(this.getMotion().add(
                    MathHelper.sin(-this.rotationYaw * ((float) Math.PI / 180F)) * f,
                    0.0D, MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F)) * f
            ));
        }
    }

    @Override
    public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger)) {
            float f = 0.0F;
            float f1 = (float) ((this.removed ? (double) 0.01F : this.getMountedYOffset()) + passenger.getYOffset());
            if (this.getPassengers().size() > 1) {
                int i = this.getPassengers().indexOf(passenger);
                if (i == 0) {
                    f = 0.2F;
                } else {
                    f = -0.6F;
                }

                if (passenger instanceof AnimalEntity) {
                    f = (float) ((double) f + 0.2D);
                }
            }

            Vector3d vector3d = (new Vector3d(f, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * ((float) Math.PI / 180F) - ((float) Math.PI / 2F));
            passenger.setPosition(this.getPosX() + vector3d.x, this.getPosY() + (double) f1, this.getPosZ() + vector3d.z);
            passenger.rotationYaw += this.deltaRotation;
            passenger.setRotationYawHead(passenger.getRotationYawHead() + this.deltaRotation);
            this.applyYawToEntity(passenger);
            if (passenger instanceof AnimalEntity && this.getPassengers().size() > 1) {
                int j = passenger.getEntityId() % 2 == 0 ? 90 : 270;
                passenger.setRenderYawOffset(((AnimalEntity) passenger).renderYawOffset + (float) j);
                passenger.setRotationYawHead(passenger.getRotationYawHead() + (float) j);
            }

        }
    }

    /**
     * Applies this boat's yaw to the given entity. Used to update the orientation of its passenger.
     */
    protected void applyYawToEntity(Entity entityToUpdate) {
        entityToUpdate.setRenderYawOffset(this.rotationYaw);
        float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
        float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
        entityToUpdate.prevRotationYaw += f1 - f;
        entityToUpdate.rotationYaw += f1 - f;
        entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
    }

    /**
     * Applies this entity's orientation (pitch/yaw) to another entity. Used to update passenger orientation.
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void applyOrientationToEntity(Entity entityToUpdate) {
        this.applyYawToEntity(entityToUpdate);
    }

    @Override
    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        if (player.isSecondaryUseActive()) {
            return ActionResultType.PASS;
        }
        if (!this.world.isRemote) {
            return player.startRiding(this) ? ActionResultType.CONSUME : ActionResultType.PASS;
        } else {
            return ActionResultType.SUCCESS;
        }
    }

    /**
     * For vehicles, the first passenger is generally considered the controller and "drives" the vehicle. For example,
     * Pigs, Horses, and Boats are generally "steered" by the controlling passenger.
     */
    @Nullable
    @Override
    public Entity getControllingPassenger() {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : list.get(0);
    }

    @OnlyIn(Dist.CLIENT)
    public void updateInputs(boolean p_184442_1_, boolean p_184442_2_, boolean p_184442_3_, boolean p_184442_4_) {
        this.leftInputDown = p_184442_1_;
        this.rightInputDown = p_184442_2_;
        this.forwardInputDown = p_184442_3_;
        this.backInputDown = p_184442_4_;
    }

    // Forge: Fix MC-119811 by instantly completing lerp on board
    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.canPassengerSteer() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.setPositionAndRotation(this.lerpX, this.lerpY, this.lerpZ, (float) this.lerpYaw, (float) this.lerpPitch);
        }
    }

    @Override
    public Vector3d func_230268_c_(LivingEntity p_230268_1_) {
        Vector3d vector3d = func_233559_a_(this.getWidth() * MathHelper.SQRT_2, p_230268_1_.getWidth(), this.rotationYaw);
        double d0 = this.getPosX() + vector3d.x;
        double d1 = this.getPosZ() + vector3d.z;
        BlockPos blockpos = new BlockPos(d0, this.getBoundingBox().maxY, d1);
        BlockPos blockpos1 = blockpos.down();
        if (!this.world.hasWater(blockpos1)) {
            for (Pose pose : p_230268_1_.func_230297_ef_()) {
                AxisAlignedBB axisalignedbb = p_230268_1_.func_233648_f_(pose);
                double d2 = this.world.func_234936_m_(blockpos);
                if (TransportationHelper.func_234630_a_(d2)) {
                    Vector3d vector3d1 = new Vector3d(d0, (double) blockpos.getY() + d2, d1);
                    if (TransportationHelper.func_234631_a_(this.world, p_230268_1_, axisalignedbb.offset(vector3d1))) {
                        p_230268_1_.setPose(pose);
                        return vector3d1;
                    }
                }

                double d3 = this.world.func_234936_m_(blockpos1);
                if (TransportationHelper.func_234630_a_(d3)) {
                    Vector3d vector3d2 = new Vector3d(d0, (double) blockpos1.getY() + d3, d1);
                    if (TransportationHelper.func_234631_a_(this.world, p_230268_1_, axisalignedbb.offset(vector3d2))) {
                        p_230268_1_.setPose(pose);
                        return vector3d2;
                    }
                }
            }
        }

        return super.func_230268_c_(p_230268_1_);
    }

    @Override
    protected boolean canBeRidden(Entity entityIn) {
        return true;
    }

    @Override
    public boolean canBeRiddenInWater(Entity rider) {
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

    /**
     * @return The player controlling the rocket
     */
    public PlayerEntity getControllingPlayer() {
        Entity entity = getControllingPassenger();
        if (entity instanceof PlayerEntity) {
            return (PlayerEntity) entity;
        }
        return null;
    }
}
