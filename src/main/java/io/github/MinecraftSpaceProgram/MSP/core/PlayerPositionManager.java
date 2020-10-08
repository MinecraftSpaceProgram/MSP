package io.github.MinecraftSpaceProgram.MSP.core;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class PlayerPositionManager{
  private static final IMSPPlayerData persistentPlayerData = new PersistantPlayerData();
  private static final IMSPPlayerData inMemoryPlayerData = new InMemoryPlayerData();

  public static Quaternion getPlayerRotation(PlayerEntity player){
    return getPlayerData(player.world).getCurrentRotation(player);
  }

  public static void setPlayerPosition(PlayerEntity player, Vector3d pos, Quaternion quaternion){
    IMSPPlayerData data = getPlayerData(player.world);
    data.setCurrentPosition(player, pos);
    data.setCurrentRotation(player, quaternion);
  }

  public static void setPlayerSpeed(PlayerEntity player, Vector3d speed){
    IMSPPlayerData data = getPlayerData(player.world);
    data.setSpeed(player, speed);
  }

  public static void setPlayerAngularVelocity(PlayerEntity player, Quaternion angularRotation){
    IMSPPlayerData data = getPlayerData(player.world);
    data.setRotationSpeed(player, angularRotation);
  }

  public static Vector3d getPlayerPosition(PlayerEntity player){
    return getPlayerData(player.world).getCurrentPosition(player);
  }

  public static IMSPPlayerData getPlayerData(World world) {
    return world.isRemote ? inMemoryPlayerData : persistentPlayerData;
  }

  public static Quaternion getPlayerAngularVelocity(PlayerEntity player) {
    return getPlayerData(player.world).getRotationSpeed(player);
  }

  public static Vector3d getPlayerVelocity(PlayerEntity player) {
    return getPlayerData(player.world).getCurrentSpeed(player);
  }
}
