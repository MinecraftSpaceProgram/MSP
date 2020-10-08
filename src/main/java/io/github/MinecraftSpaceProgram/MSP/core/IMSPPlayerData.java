package io.github.MinecraftSpaceProgram.MSP.core;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;

public interface IMSPPlayerData {
  Quaternion getCurrentRotation(PlayerEntity player);
  void setCurrentRotation(PlayerEntity player, Quaternion quaternion);
  Quaternion getRotationSpeed(PlayerEntity player);
  void setRotationSpeed(PlayerEntity player, Quaternion quaternion);


  Vector3d getCurrentPosition(PlayerEntity player);
  void setCurrentPosition(PlayerEntity player, Vector3d pos);
  Vector3d getCurrentSpeed(PlayerEntity player);
  void setSpeed(PlayerEntity player, Vector3d pos);
}
