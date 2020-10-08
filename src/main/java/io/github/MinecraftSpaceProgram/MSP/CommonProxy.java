package io.github.MinecraftSpaceProgram.MSP;

import io.github.MinecraftSpaceProgram.MSP.core.PlayerPositionManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;

public class CommonProxy {
  public void setPlayerPosition(Vector3d position, Quaternion PLAYER_ROTATION){}

  public void setPlayerRotationSpeed(Quaternion quaternion, ServerPlayerEntity player){
    // Running server side
    PlayerPositionManager.setPlayerAngularVelocity(player, quaternion);
  }
}