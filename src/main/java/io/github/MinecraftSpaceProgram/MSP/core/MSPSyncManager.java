package io.github.MinecraftSpaceProgram.MSP.core;

import io.github.MinecraftSpaceProgram.MSP.network.NetworkHandler;
import io.github.MinecraftSpaceProgram.MSP.network.PlayerPositionMessage;
import io.github.MinecraftSpaceProgram.MSP.network.PlayerRotationSpeedMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;

public class MSPSyncManager {
  public static void sendPlayerPosition(PlayerEntity player){
    Vector3d position = PlayerPositionManager.getPlayerPosition(player);
    Quaternion rotation = PlayerPositionManager.getPlayerRotation(player);
    NetworkHandler.sendTo(new PlayerPositionMessage(position, rotation), player);
  }

  public static void sendPlayerRotationSpeed(PlayerEntity player) {
    Quaternion rotationSpeed = PlayerPositionManager.getPlayerAngularVelocity(player);
    NetworkHandler.channel.sendToServer(new PlayerRotationSpeedMessage(rotationSpeed));
  }
}
