package io.github.MinecraftSpaceProgram.MSP.handler;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.core.MSPSyncManager;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MSP.MOD_ID)
public class LoginHandler {

  @SubscribeEvent
  public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
    MSPSyncManager.sendPlayerPosition(event.getPlayer());
    //WaystoneConfig.syncServerConfigs(event.getPlayer());
  }



}

