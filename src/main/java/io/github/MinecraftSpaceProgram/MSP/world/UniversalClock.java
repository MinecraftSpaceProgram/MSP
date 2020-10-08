package io.github.MinecraftSpaceProgram.MSP.world;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.core.MSPSyncManager;
import io.github.MinecraftSpaceProgram.MSP.core.PlayerPositionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.TICK_LENGTH;

@Mod.EventBusSubscriber(modid = MSP.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class UniversalClock {
  @SubscribeEvent
  public void onTick(TickEvent.PlayerTickEvent event) {
    if (event.phase == TickEvent.Phase.END && event.side.isServer()) {
      tickEnd(event.player);
    }
  }

  private void tickEnd(PlayerEntity player){
    // updates spaceship's position and rotation
    // adds the current position to the speed times the tick length
    Vector3d newPos = PlayerPositionManager.getPlayerPosition(player).add(PlayerPositionManager.getPlayerVelocity(player).scale(TICK_LENGTH));

    // Combines current rotation with angular rotation
    Quaternion newRotation = new Quaternion(PlayerPositionManager.getPlayerAngularVelocity(player));
    newRotation.multiply(PlayerPositionManager.getPlayerRotation(player));

    PlayerPositionManager.setPlayerPosition(player, newPos, newRotation);
    MSPSyncManager.sendPlayerPosition(player);
    //MSP.LOGGER.debug("ticking");
  }
}
