package io.github.MinecraftSpaceProgram.MSP.core;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;

public class InMemoryPlayerData implements IMSPPlayerData {
  private Quaternion currentRotation = Quaternion.ONE;
  private Vector3d currentPosition = Vector3d.ZERO;
  private Quaternion currentRotationSpeed = Quaternion.ONE;
  private Vector3d currentSpeed = Vector3d.ZERO;

  @Override
  public Quaternion getCurrentRotation(PlayerEntity player) {
    return currentRotation;
  }

  @Override
  public void setCurrentRotation(PlayerEntity player, Quaternion quaternion) {
    this.currentRotation = quaternion;
  }

  @Override
  public Quaternion getRotationSpeed(PlayerEntity player) {
    return currentRotationSpeed;
  }

  @Override
  public void setRotationSpeed(PlayerEntity player, Quaternion quaternion) {
    this.currentRotationSpeed = quaternion;
  }

  @Override
  public Vector3d getCurrentPosition(PlayerEntity player) {
    return currentPosition;
  }

  @Override
  public void setCurrentPosition(PlayerEntity player, Vector3d pos) {
    this.currentPosition = pos;
  }

  @Override
  public Vector3d getCurrentSpeed(PlayerEntity player) {
    return currentSpeed;
  }

  @Override
  public void setSpeed(PlayerEntity player, Vector3d pos) {
    this.currentSpeed = pos;
  }
}
