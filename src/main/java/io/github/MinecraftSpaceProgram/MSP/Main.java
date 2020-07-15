package io.github.MinecraftSpaceProgram.MSP;

import io.github.MinecraftSpaceProgram.MSP.init.BlockInitNew;
import io.github.MinecraftSpaceProgram.MSP.init.ItemInit;
import io.github.MinecraftSpaceProgram.MSP.init.ModContainerTypes;
import io.github.MinecraftSpaceProgram.MSP.init.ModTileEntityTypes;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
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

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, MSPConfig.CLIENT_SPEC);

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemInit.ITEMS.register(modEventBus);
        BlockInitNew.BLOCKS.register(modEventBus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        ModContainerTypes.CONTAINER_TYPES.register(modEventBus);

        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
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

        BlockInitNew.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
            final Item.Properties properties = new Item.Properties().group(TutorialItemGroup.instance);
            final BlockItem blockItem = new BlockItem(block, properties);
            blockItem.setRegistryName(block.getRegistryName());
            registry.register(blockItem);
        });
        LOGGER.debug("Registered Block Items");
    }

    public static class TutorialItemGroup extends ItemGroup {
        public static final ItemGroup instance = new TutorialItemGroup(ItemGroup.GROUPS.length, "MSP");

        private TutorialItemGroup(int index, String label) {
            super(index, label);
        }

        @Override
        public ItemStack createIcon() {
            return new ItemStack(BlockInitNew.FLIGHT_COMPUTER.get());
        }
    }


}
