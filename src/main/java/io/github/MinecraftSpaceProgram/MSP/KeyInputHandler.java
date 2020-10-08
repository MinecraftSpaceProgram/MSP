package io.github.MinecraftSpaceProgram.MSP;

import io.github.MinecraftSpaceProgram.MSP.core.MSPSyncManager;
import io.github.MinecraftSpaceProgram.MSP.core.PlayerPositionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MSP.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyInputHandler {
  @SubscribeEvent
  public void onKeyInput(InputEvent.KeyInputEvent event)
  {
    // retrieves the player
    PlayerEntity player = Minecraft.getInstance().player;

    if (Keybinds.RotateXMinus.isPressed())
    {
      Quaternion deltaSpeed = Vector3f.XP.rotation(0.01F);
      deltaSpeed.multiply(PlayerPositionManager.getPlayerAngularVelocity(player));
      PlayerPositionManager.setPlayerAngularVelocity(player, deltaSpeed);
      MSPSyncManager.sendPlayerRotationSpeed(player);
    }
    if (Keybinds.RotateXPlus.isPressed()){
      Quaternion deltaSpeed = Vector3f.XN.rotation(0.01F);
      deltaSpeed.multiply(PlayerPositionManager.getPlayerAngularVelocity(player));
      PlayerPositionManager.setPlayerAngularVelocity(player, deltaSpeed);
      MSPSyncManager.sendPlayerRotationSpeed(player);
    }
  }
}
