package io.github.MinecraftSpaceProgram.MSP.client;

import io.github.MinecraftSpaceProgram.MSP.CommonProxy;
import io.github.MinecraftSpaceProgram.MSP.core.PlayerPositionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;

public class ClientProxy extends CommonProxy {
  @Override
  public void setPlayerPosition(Vector3d position, Quaternion quaternion) {
    // Running client side
    assert Minecraft.getInstance().player != null;
    PlayerPositionManager.setPlayerPosition(Minecraft.getInstance().player, position, quaternion);
  }
}
