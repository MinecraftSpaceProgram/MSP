package io.github.MinecraftSpaceProgram.MSP;

import io.github.MinecraftSpaceProgram.MSP.init.BlockLoader;
import io.github.MinecraftSpaceProgram.MSP.init.ItemLoader;
import io.github.MinecraftSpaceProgram.MSP.init.ModTileEntityTypes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@Mod(MSP.MOD_ID)
@Mod.EventBusSubscriber(modid= MSP.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class MSP {
    public static final String MOD_ID ="msp";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static final Marker MARKER = MarkerManager.getMarker("MSP-ModInitialization");

    public static final ItemGroup ITEM_GROUP = new ItemGroup(MOD_ID) {
        public ItemStack createIcon() {
            return new ItemStack(ItemLoader.EXAMPLE_ITEM.get());
        }
    };

    public static MSP instance;


    public MSP() {
        instance = this;

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ItemLoader.ITEMS.register(modEventBus);
        BlockLoader.BLOCKS.register(modEventBus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
    }

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event){
        final IForgeRegistry<Item> registry = event.getRegistry();

        BlockLoader.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
            final Item.Properties properties = new Item.Properties().group(ITEM_GROUP);
            final BlockItem blockItem = new BlockItem(block, properties);
            //noinspection ConstantConditions
            blockItem.setRegistryName(block.getRegistryName());
            registry.register(blockItem);
        });
        LOGGER.debug(MARKER, "Registered Block Items");
    }

}
