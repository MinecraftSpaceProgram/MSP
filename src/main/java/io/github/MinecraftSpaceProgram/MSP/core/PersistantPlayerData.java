package io.github.MinecraftSpaceProgram.MSP.core;

import io.github.MinecraftSpaceProgram.MSP.util.MSPNBTUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;

public class PersistantPlayerData implements IMSPPlayerData {
  private static final String TAG_NAME = "MSPData";
  private static final String CURRENT_ROTATION = "MSPCurrentRotation";
  private static final String CURRENT_POSITION = "MSPCurrentPosition";
  private static final String CURRENT_SPEED = "MSPCurrentSpeed";
  private static final String CURRENT_ROTATION_SPEED = "MSPCurrentRotationSpeed";

  @Override
  public Quaternion getCurrentRotation(PlayerEntity player) {
    CompoundNBT compoundNBT = (CompoundNBT) getPlayerPositionData(player).get(CURRENT_ROTATION);
    if (compoundNBT != null) {
      return MSPNBTUtils.readQuaternion(compoundNBT);
    } else {
      return Quaternion.ONE;
    }
  }

  @Override
  public void setCurrentRotation(PlayerEntity player, Quaternion quaternion) {
    getPlayerPositionData(player).put(CURRENT_ROTATION, MSPNBTUtils.writeQuaternion(quaternion));
  }

  @Override
  public Quaternion getRotationSpeed(PlayerEntity player) {
    CompoundNBT compoundNBT = (CompoundNBT) getPlayerPositionData(player).get(CURRENT_ROTATION_SPEED);
    if (compoundNBT != null) {
      return MSPNBTUtils.readQuaternion(compoundNBT);
    } else {
      return Quaternion.ONE;
    }
  }

  @Override
  public void setRotationSpeed(PlayerEntity player, Quaternion quaternion) {
    getPlayerPositionData(player).put(CURRENT_ROTATION_SPEED, MSPNBTUtils.writeQuaternion(quaternion));
  }

  @Override
  public Vector3d getCurrentPosition(PlayerEntity player) {
    CompoundNBT compoundNBT = (CompoundNBT) getPlayerPositionData(player).get(CURRENT_POSITION);
    if (compoundNBT != null) {
      return MSPNBTUtils.readVector3d(compoundNBT);
    } else {
      return Vector3d.ZERO;
    }
  }

  @Override
  public void setCurrentPosition(PlayerEntity player, Vector3d pos) {
    getPlayerPositionData(player).put(CURRENT_POSITION, MSPNBTUtils.writeVector3d(pos));
  }

  @Override
  public Vector3d getCurrentSpeed(PlayerEntity player) {
    CompoundNBT compoundNBT = (CompoundNBT) getPlayerPositionData(player).get(CURRENT_SPEED);
    if (compoundNBT != null) {
      return MSPNBTUtils.readVector3d(compoundNBT);
    } else {
      return Vector3d.ZERO;
    }
  }

  @Override
  public void setSpeed(PlayerEntity player, Vector3d pos) {
    getPlayerPositionData(player).put(CURRENT_SPEED, MSPNBTUtils.writeVector3d(pos));
  }

  private static CompoundNBT getPlayerPositionData(PlayerEntity player) {
    // Fetch the required data from the player's NBT
    CompoundNBT persistantData = player.getPersistentData();
    CompoundNBT persistedData = persistantData.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
    CompoundNBT compoundNBT = persistedData.getCompound(TAG_NAME);

    // Restores the player's NBT to its default state
    persistedData.put(TAG_NAME, compoundNBT);
    persistantData.put(PlayerEntity.PERSISTED_NBT_TAG, persistedData);

    return compoundNBT;
  }
}
