package io.github.MinecraftSpaceProgram.MSP;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import io.github.MinecraftSpaceProgram.MSP.init.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Main.MOD_ID)
@Mod.EventBusSubscriber(modid=Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Main {

    public static final String MOD_ID ="msp";
    public static Main instance;

    public static final Logger LOGGER = LogManager.getLogger();

    public Main() {
        LOGGER.debug("GOT HERE");
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemInit.ITEMS.register(modEventBus);
        BlockInit.BLOCKS.register(modEventBus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);

        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event){
        final IForgeRegistry<Item> registry = event.getRegistry();

        BlockInit.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
            final Item.Properties properties = new Item.Properties().group(MSPItemGroup.instance);
            final BlockItem blockItem = new BlockItem(block, properties);
            //noinspection ConstantConditions
            blockItem.setRegistryName(block.getRegistryName());
            registry.register(blockItem);
        });
        LOGGER.debug("Registered Block Items");
    }

    public static class MSPItemGroup extends ItemGroup {
        public static final ItemGroup instance = new MSPItemGroup("MSP");

        private MSPItemGroup(String label) {
            super(label);
        }

        @Override
        public ItemStack createIcon() {
            return new ItemStack(ItemInit.EXAMPLE_ITEM.get());
        }
    }
}
