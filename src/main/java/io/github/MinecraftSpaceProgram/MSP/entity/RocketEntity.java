package io.github.MinecraftSpaceProgram.MSP.entity;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.init.MSPBlocks;
import io.github.MinecraftSpaceProgram.MSP.init.MSPDimensions;
import io.github.MinecraftSpaceProgram.MSP.init.MSPEntityTypes;
import io.github.MinecraftSpaceProgram.MSP.physics.orbital.SolarSystem;
import io.github.MinecraftSpaceProgram.MSP.util.BlockStorage;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static io.github.MinecraftSpaceProgram.MSP.init.MSPDataSerializers.BLOCK_POS_LIST_SERIALIZER;
import static io.github.MinecraftSpaceProgram.MSP.init.MSPDataSerializers.QUATERNION_SERIALIZER;
import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.G;
import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.TICK_LENGTH;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RocketEntity extends Entity implements IEntityAdditionalSpawnData {
  protected static final DataParameter<Boolean> STARTED =
      EntityDataManager.createKey(RocketEntity.class, DataSerializers.BOOLEAN);
  protected static final DataParameter<Float> FUEL =
      EntityDataManager.createKey(RocketEntity.class, DataSerializers.FLOAT);
  protected static final DataParameter<Float> FUEL_CAPACITY =
      EntityDataManager.createKey(RocketEntity.class, DataSerializers.FLOAT);
  protected static final DataParameter<Float> DRY_MASS =
      EntityDataManager.createKey(RocketEntity.class, DataSerializers.FLOAT);
  protected static final DataParameter<Float> THRUST =
      EntityDataManager.createKey(RocketEntity.class, DataSerializers.FLOAT);
  protected static final DataParameter<Float> PLAYER_ROTATION =
      EntityDataManager.createKey(RocketEntity.class, DataSerializers.FLOAT);
  protected static final DataParameter<Float> CONSUMPTION =
      EntityDataManager.createKey(RocketEntity.class, DataSerializers.FLOAT);
  protected static final DataParameter<Quaternion> QUATERNION =
      EntityDataManager.createKey(RocketEntity.class, QUATERNION_SERIALIZER);
  protected static final DataParameter<List<BlockPos>> ENGINES =
      EntityDataManager.createKey(RocketEntity.class, BLOCK_POS_LIST_SERIALIZER);
  protected static final DataParameter<BlockPos> CHAIR =
      EntityDataManager.createKey(RocketEntity.class, DataSerializers.BLOCK_POS);
  // private static final DataParameter<BlockStorage> STORAGE =
  // EntityDataManager.createKey(RocketEntity.class, DataSerializers.BLOCK_POS);

  protected final AxisAlignedBB AABB;
  public BlockStorage storage;
  private int lerpSteps;
  private double lerpX;
  private double lerpY;
  private double lerpZ;
  private double lerpYaw;
  private double lerpPitch;

  public double trueAltitude, trueSpeed, trueAcceleration;

  public RocketEntity(EntityType<? extends RocketEntity> entityEntityType, World world) {
    super(entityEntityType, world);
    MSP.LOGGER.debug("SUMMONED A ROCKET");
    this.storage =
        new BlockStorage(
            new BlockState[][][] {{{MSPBlocks.ROCKET_GENERIC.get().getDefaultState()}}},
            1,
            1,
            1,
            1,
            1,
            1,
            1);

    this.AABB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
  }

  public RocketEntity(World world, BlockStorage storage, double x, double y, double z) {
    super(MSPEntityTypes.ROCKET_ENTITY_TYPE.get(), world);
    this.storage = storage;
    this.setPosition(x, y, z);
    this.AABB = new AxisAlignedBB(0, 0, 0, storage.sizeX, storage.sizeY, storage.sizeZ);
    this.recalculateSize();
  }

  @Override
  public void tick() {
    super.tick();

    this.tickLerp();

    // starts the rocket if it isn't started yet
    LivingEntity controllingPassenger = (LivingEntity) getControllingPassenger();
    float moveForward =
        controllingPassenger instanceof PlayerEntity ? controllingPassenger.moveForward : 0;
    if (moveForward > 0 && !dataManager.get(STARTED)) {
      dataManager.set(STARTED, true);
    }

    // TODO make this planet dependant
    // "Oh Gravity Thou Art a Heartless Bitch" -Sheldon
    double r = this.getPosY() + SolarSystem.EARTH.size / 2.0D;
    double force = -G * SolarSystem.EARTH.mass * this.getMass() / (r * r);

    // Drag very naive model
    force -= Math.signum(trueSpeed) * 0.5D * 1.2 * trueSpeed * trueSpeed * 0.5;

    // TODO currently using a monopropellant engine MR-80B 3,100N (700 lbf) Throttling Rocket from
    //  Aerojet Rocketdyne
    if (dataManager.get(STARTED)) {
      float fuel = dataManager.get(FUEL);
      if (fuel > 0) {

        // removes fuel due to consumption
        fuel -= this.getConsumption() * (float) TICK_LENGTH;
        fuel = fuel < 0 ? 0 : fuel;
        dataManager.set(FUEL, fuel);

        // acceleration due to thrust
        force += this.getThrust();

        // adds particles under every engines
        if (world.isRemote) {
          for (BlockPos blockPos : this.getEngines()) {
            if (world.getGameTime() % 10 == 0) {
              spawnParticle(
                  "rocketSmoke", world, this.getPosX(), this.getPosY(), this.getPosZ(), 0, 0, 0);
            }

            // TODO make this actually do flames
            for (int i = 0; i < 4; i++) {
              spawnParticle(
                  "rocketFlame",
                  world,
                  blockPos.getX() + this.getPosX(),
                  blockPos.getY() + this.getPosY() - 0.75 - 0.2 * i,
                  blockPos.getZ() + this.getPosZ(),
                  (this.rand.nextFloat() - 0.5f) / 8f,
                  -.75,
                  (this.rand.nextFloat() - 0.5f) / 8f);
              world.addParticle(
                  ParticleTypes.LARGE_SMOKE,
                  blockPos.getX() + this.getPosX(),
                  blockPos.getY() + this.getPosY() - 2 - 0.2 * i,
                  blockPos.getZ() + this.getPosZ(),
                  (this.rand.nextFloat() - 0.5f) / 6f,
                  -.75,
                  (this.rand.nextFloat() - 0.5f) / 6f);
            }
          }
        }
      }
    }

    // Newton's first law: F=ma
    this.trueAcceleration = force / this.getMass();

    this.trueSpeed = this.onGround ? 0 : this.trueSpeed + trueAcceleration * TICK_LENGTH;

    double MAX_SPEED = 100.0D;
    double MAX_ALTITUDE = 500.0D;

    this.setMotion(
        0,
        this.trueAltitude > MAX_ALTITUDE
            ? 0
            : Math.signum(trueSpeed) * Math.min(MAX_SPEED, Math.abs(trueSpeed)) * TICK_LENGTH,
        0);
    this.move(MoverType.SELF, this.getMotion());

    if (this.getPosY() > MAX_ALTITUDE) {
      this.trueAltitude += this.trueSpeed * TICK_LENGTH;
    } else {
      this.trueAltitude = this.getPosY() - 62;
    }

    if (this.trueAltitude >= 20000) {
      teleportToSpace();
    }

    this.doBlockCollisions();
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
      this.rotationPitch =
          (float)
              ((double) this.rotationPitch
                  + (this.lerpPitch - (double) this.rotationPitch) / (double) this.lerpSteps);
      --this.lerpSteps;
      this.setPosition(d0, d1, d2);
      this.setRotation(this.rotationYaw, this.rotationPitch);
    }
  }

  @Override
  protected void registerData() {
    dataManager.register(STARTED, false);
    dataManager.register(FUEL, 0.0F);
    dataManager.register(QUATERNION, Quaternion.ONE);
    dataManager.register(ENGINES, new ArrayList<>());
    dataManager.register(DRY_MASS, 0.0F);
    dataManager.register(THRUST, 0.0F);
    dataManager.register(CONSUMPTION, 0.0F);
    dataManager.register(CHAIR, new BlockPos(0, 0, 0));
    dataManager.register(PLAYER_ROTATION, 0.0F);
    dataManager.register(FUEL_CAPACITY, 0.0F);
  }

  @Override
  protected void readAdditional(@Nonnull CompoundNBT compound) {
    this.storage = new BlockStorage(compound.getCompound("storage"));
  }

  @Override
  protected void writeAdditional(@Nonnull CompoundNBT compound) {
    compound.put("storage", this.storage.toNBT());
  }

  public BlockStorage getStorage() {
    return storage;
  }

  public void setFuel(float fuel) {
    this.dataManager.set(FUEL, fuel);
  }

  public float getFuel() {
    return this.dataManager.get(FUEL);
  }

  public void setFuelCapacity(float fuel) {
    this.dataManager.set(FUEL_CAPACITY, fuel);
  }

  public float getFuelCapacity() {
    return this.dataManager.get(FUEL_CAPACITY);
  }

  public List<BlockPos> getEngines() {
    return this.dataManager.get(ENGINES);
  }

  public void setEngines(List<BlockPos> pos) {
    this.dataManager.set(ENGINES, pos);
  }

  public float getDryMass() {
    return this.dataManager.get(DRY_MASS);
  }

  public void setDryMass(float mass) {
    this.dataManager.set(DRY_MASS, mass);
  }

  public void setConsumption(float consumption) {
    this.dataManager.set(CONSUMPTION, consumption);
  }

  public void setThrust(float thrust) {
    this.dataManager.set(THRUST, thrust);
  }

  public float getConsumption() {
    return this.dataManager.get(CONSUMPTION);
  }

  public float getThrust() {
    return this.dataManager.get(THRUST);
  }

  public void setChair(BlockPos pos) {
    this.dataManager.set(CHAIR, pos);
  }

  public BlockPos getChair() {
    return this.dataManager.get(CHAIR);
  }

  public void setPlayerRotation(float rotation) {
    this.dataManager.set(PLAYER_ROTATION, rotation);
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean attackEntityFrom(DamageSource source, float amount) {
    if (this.isInvulnerableTo(source)) {
      return false;
    }
    if (!(source.getTrueSource() instanceof PlayerEntity
            && ((PlayerEntity) source.getTrueSource()).abilities.isCreativeMode)
        && world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)
        && !this.removed) {
      entityDropItem(Items.STICK);
    }
    if (!this.world.isRemote && !this.removed) {
      remove();
      return true;
    }
    return false;
  }

  /**
   * Returns a boundingBox used to collide the entity with other entities and blocks. This enables
   * the entity to be pushable on contact, like boats.
   */
  /*@Nullable
  @Override
  public AxisAlignedBB getCollisionBox(Entity entityIn) {
    return entityIn.canBePushed() ? entityIn.getBoundingBox() : null;
  }

  /**
   * Returns the <b>solid</b> collision bounding box for this entity. Used to make (e.g.) boats
   * solid. Return null if this entity is not solid.
   */
  /*@Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
      return this.getBoundingBox();
    }
  /*

   */
  /** Returns true if this entity should push and be pushed by other entities when colliding. */
  @Override
  public boolean canBePushed() {
    return false;
  }

  @Override
  public void updatePassenger(Entity passenger) {
    passenger.setPosition(
        this.getPosX() + this.getChair().getX(),
        this.getPosY() + this.getChair().getY() - 0.5D,
        this.getPosZ() + getChair().getZ());
    this.applyYawToEntity(passenger);
  }

  /** Applies a velocity to the entities, to push them away from eachother. */
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

  /** Returns true if other Entities should be prevented from moving through this Entity. */
  @Override
  public boolean canBeCollidedWith() {
    return !this.removed;
  }

  /** Sets a target for the client to interpolate towards over the next few ticks */
  @OnlyIn(Dist.CLIENT)
  @Override
  public void setPositionAndRotationDirect(
      double x,
      double y,
      double z,
      float yaw,
      float pitch,
      int posRotationIncrements,
      boolean teleport) {
    this.lerpX = x;
    this.lerpY = y;
    this.lerpZ = z;
    this.lerpYaw = yaw;
    this.lerpPitch = pitch;
    this.lerpSteps = 10;
  }

  /**
   * Gets the horizontal facing direction of this Entity, adjusted to take specially-treated entity
   * types into account.
   */
  @Override
  public Direction getAdjustedHorizontalFacing() {
    return this.getHorizontalFacing().rotateY();
  }

  /**
   * Applies this boat's yaw to the given entity. Used to update the orientation of its passenger.
   */
  protected void applyYawToEntity(Entity entityToUpdate) {
    entityToUpdate.setRenderYawOffset(this.rotationYaw + this.dataManager.get(PLAYER_ROTATION));
    float f =
        MathHelper.wrapDegrees(
            entityToUpdate.rotationYaw - this.rotationYaw - this.dataManager.get(PLAYER_ROTATION));
    float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
    entityToUpdate.prevRotationYaw += f1 - f;
    entityToUpdate.rotationYaw += f1 - f;
    entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
  }

  /**
   * Applies this entity's orientation (pitch/yaw) to another entity. Used to update passenger
   * orientation.
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
   * For vehicles, the first passenger is generally considered the controller and "drives" the
   * vehicle. For example, Pigs, Horses, and Boats are generally "steered" by the controlling
   * passenger.
   */
  @Nullable
  @Override
  public Entity getControllingPassenger() {
    List<Entity> list = this.getPassengers();
    return list.isEmpty() ? null : list.get(0);
  }

  @Override
  protected void addPassenger(Entity passenger) {
    super.addPassenger(passenger);
    if (this.canPassengerSteer() && this.lerpSteps > 0) {
      this.lerpSteps = 0;
      this.setPositionAndRotation(
          this.lerpX, this.lerpY, this.lerpZ, (float) this.lerpYaw, (float) this.lerpPitch);
    }
  }

  /** Copied from boat */
  @Override
  public Vector3d func_230268_c_(LivingEntity p_230268_1_) {
    Vector3d vector3d =
        func_233559_a_(
            this.getWidth() * MathHelper.SQRT_2, p_230268_1_.getWidth(), this.rotationYaw);
    double d0 = this.getPosX() + vector3d.x;
    double d1 = this.getPosZ() + vector3d.z;
    BlockPos blockpos = new BlockPos(d0, this.getBoundingBox().maxY, d1);
    BlockPos blockpos1 = blockpos.down();
    if (!this.world.hasWater(blockpos1)) {
      double d2 = (double) blockpos.getY() + this.world.func_242403_h(blockpos);
      double d3 = (double) blockpos.getY() + this.world.func_242403_h(blockpos1);

      for (Pose pose : p_230268_1_.func_230297_ef_()) {
        Vector3d vector3d1 =
            TransportationHelper.func_242381_a(this.world, d0, d2, d1, p_230268_1_, pose);
        if (vector3d1 != null) {
          p_230268_1_.setPose(pose);
          return vector3d1;
        }

        Vector3d vector3d2 =
            TransportationHelper.func_242381_a(this.world, d0, d3, d1, p_230268_1_, pose);
        if (vector3d2 != null) {
          p_230268_1_.setPose(pose);
          return vector3d2;
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
    return false;
  }

  @Override
  protected AxisAlignedBB getBoundingBox(Pose pose) {
    // if(storage != null) {
    return AABB;
    // return new AxisAlignedBB(0,0,0,1,1,1);

    // AxisAlignedBB COLLISION_AABB = new AxisAlignedBB(-1, 0, -1, 1, 0.5, 1);
    // return COLLISION_AABB.offset(getPositionVec());
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
    CompoundNBT compound = dataStream.readCompoundTag();
    if (compound != null) {
      this.storage = new BlockStorage(compound);
    } else {
      throw new IllegalArgumentException("Data Stream does not contain the CompoundNBT");
    }
  }

  /** @return the thrust delivered by the engines in a given tick */
  /*public float engineThrust() {
    float thrust = 0.0F;
    for (IRocketEngine engine : engines) {
      thrust += engine.getThrust();
    }
    return thrust * (float) TICK_LENGTH;
  }/*

  /** @return the total mass of the rocket */
  public float getMass() {
    return this.getDryMass() + this.getFuel();
  }

  public void spawnParticle(
      String particle,
      World world,
      double x,
      double y,
      double z,
      double motionX,
      double motionY,
      double motionZ) {
    // WTF how is == working?  Should be .equals
    if (particle.equals("rocketFlame")) {
      // RocketFx fx = new RocketFx(world, x, y, z, motionX, motionY, motionZ);
      world.addParticle(ParticleTypes.FLAME, x, y, z, motionX, motionY, motionZ);
    } else if (particle.equals(("rocketSmoke"))) {
      // TrailFx fx = new TrailFx(world, x, y, z, motionX, motionY, motionZ);
      world.addParticle(ParticleTypes.SMOKE, x, y, z, motionX, motionY, motionZ);
    }
  }

  private void teleportToSpace() {
    for (Entity entity : this.getPassengers()) {
      if (entity.func_242280_ah()) {
        entity.func_242279_ag();
      } else {
        World serverWorld = this.world;
        if (serverWorld != null) {
          MinecraftServer minecraftserver = serverWorld.getServer(); // overworld
          RegistryKey<World> where2go =
              this.world.func_234923_W_() == MSPDimensions.space_w
                  ? World.field_234918_g_
                  : MSPDimensions.space_w;
          if (minecraftserver != null) {
            ServerWorld destination = minecraftserver.getWorld(where2go);
            // if (minecraftserver.getAllowNether() && !entity.isPassenger()) {
            entity.world.getProfiler().startSection("space_portal"); // WTF is this
            entity.func_242279_ag();
            entity.stopRiding();
            entity.func_241206_a_(destination);
            this.trueAltitude = 0;
            entity.world.getProfiler().endSection();
          }
        }
      }
    }
  }
}
