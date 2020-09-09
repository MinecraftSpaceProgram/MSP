package io.github.MinecraftSpaceProgram.MSP.client;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.client.renderer.entity.RocketRenderer;
import io.github.MinecraftSpaceProgram.MSP.client.renderer.entity.SeatRenderer;
import io.github.MinecraftSpaceProgram.MSP.init.MSPEntityTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MSP.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MSPClient {
    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        MSP.LOGGER.debug("Adding renderer's to the registry");
        RenderingRegistry.registerEntityRenderingHandler(MSPEntityTypes.ROCKET_ENTITY_TYPE.get(), RocketRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MSPEntityTypes.SEAT_TYPE.get(), SeatRenderer::new);
    }
}
