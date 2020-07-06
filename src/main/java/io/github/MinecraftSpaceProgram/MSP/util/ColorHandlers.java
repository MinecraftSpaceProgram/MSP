package io.github.MinecraftSpaceProgram.MSP.util;

import io.github.MinecraftSpaceProgram.MSP.init.ItemLoader;
import io.github.MinecraftSpaceProgram.MSP.item.HangarController;
import net.minecraftforge.client.event.ColorHandlerEvent;

public class ColorHandlers {
    public static void hangarControllerColorHandler(ColorHandlerEvent.Item event) {
        event.getItemColors().register(HangarController::getItemColor, ItemLoader.HANGAR_CONTROLLER.get());
    }
}
