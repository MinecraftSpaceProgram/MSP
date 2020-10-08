package io.github.MinecraftSpaceProgram.MSP;

import io.github.MinecraftSpaceProgram.MSP.client.ClientProxy;
import io.github.MinecraftSpaceProgram.MSP.client.gui.RocketGui;
import io.github.MinecraftSpaceProgram.MSP.init.*;
import io.github.MinecraftSpaceProgram.MSP.network.NetworkHandler;
import io.github.MinecraftSpaceProgram.MSP.world.UniversalClock;
import io.github.MinecraftSpaceProgram.MSP.world.space.MSPBiomes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
    public static CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    public static final String MOD_ID ="msp";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static final Marker MARKER = MarkerManager.getMarker("MSP-ModInitialization");

    public static final ItemGroup ITEM_GROUP = new ItemGroup(MOD_ID) {
        public ItemStack createIcon() {
            return new ItemStack(MSPItems.EXAMPLE_ITEM.get());
        }
    };


    public MSP() {
        // initializes the network manager
        DeferredWorkQueue.runLater(NetworkHandler::init);

        // registers other stuff
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MSPItems.ITEMS.register(modEventBus);
        MSPBlocks.BLOCKS.register(modEventBus);
        MSPTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        MSPEntityTypes.ENTITY_TYPES.register(modEventBus);
        ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
        MSPDataSerializers.DATA_SERIALIZERS.register(modEventBus);
        modEventBus.addListener(this::clientRegistries);
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

    private void clientRegistries(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new RocketGui(Minecraft.getInstance()));
        LOGGER.debug(MARKER, "Registered GUI");
    }

    @SubscribeEvent
    public static void registerBiomes(RegistryEvent.Register<Biome> evt)
    {
        MSP.LOGGER.debug("Registered Biomes");
        MSPBiomes.registerBiomes(evt);
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent e){
        MinecraftForge.EVENT_BUS.register(new UniversalClock());
        LOGGER.debug("Registered a Universal Clock");
    }
}
