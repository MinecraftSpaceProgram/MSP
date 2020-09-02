package io.github.MinecraftSpaceProgram.MSP;

import io.github.MinecraftSpaceProgram.MSP.init.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
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

@SuppressWarnings("deprecation")
@Mod(MSP.MOD_ID)
@Mod.EventBusSubscriber(modid = MSP.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class MSP {
    public static final String MOD_ID ="msp";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static final Marker MARKER = MarkerManager.getMarker("MSP-ModInitialization");

    public static final ItemGroup ITEM_GROUP = new ItemGroup(MOD_ID) {
        public ItemStack createIcon() {
            return new ItemStack(MSPItems.EXAMPLE_ITEM.get());
        }
    };



    public MSP() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MSPItems.ITEMS.register(modEventBus);
        MSPBlocks.BLOCKS.register(modEventBus);
        MSPTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        MSPEntityTypes.ENTITY_TYPES.register(modEventBus);
        ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
        MSPDataSerializers.DATA_SERIALIZERS.register(modEventBus);
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event){
        if(event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)){
            event.addSprite(new ResourceLocation(MOD_ID, "planets/default"));
            event.addSprite(new ResourceLocation(MOD_ID, "skybox/skybox2"));
            event.addSprite(new ResourceLocation(MOD_ID, "planets/clouds"));
            event.addSprite(new ResourceLocation(MOD_ID, "planets/earth"));
            event.addSprite(new ResourceLocation(MOD_ID, "planets/jupyter"));
            event.addSprite(new ResourceLocation(MOD_ID, "planets/mars"));
            event.addSprite(new ResourceLocation(MOD_ID, "planets/mercury"));
            event.addSprite(new ResourceLocation(MOD_ID, "planets/moon"));
            event.addSprite(new ResourceLocation(MOD_ID, "planets/venus"));
        }
    }

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event){
        final IForgeRegistry<Item> registry = event.getRegistry();

        MSPBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
            final Item.Properties properties = new Item.Properties().group(ITEM_GROUP);
            final BlockItem blockItem = new BlockItem(block, properties);
            //noinspection ConstantConditions
            blockItem.setRegistryName(block.getRegistryName());
            registry.register(blockItem);
        });
        LOGGER.debug(MARKER, "Registered Block Items");
    }
}
