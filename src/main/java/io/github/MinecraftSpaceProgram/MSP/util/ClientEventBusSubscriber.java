package io.github.MinecraftSpaceProgram.MSP.util;

import io.github.MinecraftSpaceProgram.MSP.Main;
import io.github.MinecraftSpaceProgram.MSP.client.gui.FlightComputerScreen;
import io.github.MinecraftSpaceProgram.MSP.client.gui.GUIBlockScreen;
import io.github.MinecraftSpaceProgram.MSP.init.ModContainerTypes;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event){
        ScreenManager.registerFactory(ModContainerTypes.FLIGHT_COMPUTER.get(), FlightComputerScreen::new);

        ScreenManager.registerFactory(ModContainerTypes.GUI_BLOCK.get(), GUIBlockScreen::new);
    }
}
