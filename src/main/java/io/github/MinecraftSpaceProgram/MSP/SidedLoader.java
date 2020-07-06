package io.github.MinecraftSpaceProgram.MSP;

import io.github.MinecraftSpaceProgram.MSP.init.BlockLoader;
import io.github.MinecraftSpaceProgram.MSP.init.ItemLoader;
import io.github.MinecraftSpaceProgram.MSP.init.ModTileEntityTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

public class SidedLoader {
    SidedLoader(IEventBus modEventBus) {
        ItemLoader.ITEMS.register(modEventBus);
        BlockLoader.BLOCKS.register(modEventBus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);

        modEventBus.addListener(SidedLoader::enqueueIMC);
        modEventBus.addListener(SidedLoader::processIMC);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private static void enqueueIMC(final InterModEnqueueEvent event) {
    }

    private static void processIMC(final InterModEnqueueEvent event) {
    }

    public static class Client extends SidedLoader {
        Client(IEventBus modEventBus) {
            super(modEventBus);
        }
    }

    public static class Server extends SidedLoader {
        Server(IEventBus modEventBus) {
            super(modEventBus);
        }
    }
}
