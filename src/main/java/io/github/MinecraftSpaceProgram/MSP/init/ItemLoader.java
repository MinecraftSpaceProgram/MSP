package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.Main;
import io.github.MinecraftSpaceProgram.MSP.item.SolderingIron;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Main.MODID)
@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemLoader {

    public static final Item soldering_iron = new SolderingIron();

    public static final Item[] items_to_register = {
            soldering_iron
    };

    /**
     * Registers items
     *
     * To register an item, create an instance of its class
     * and append to items_to_register
     */
    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        for (Item item : items_to_register)
            event.getRegistry().register(item);
    }
}
