package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.Main;
import io.github.MinecraftSpaceProgram.MSP.block.ExampleBlock;
import io.github.MinecraftSpaceProgram.MSP.block.RocketGeneric;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;


@ObjectHolder(Main.MODID)
@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockLoader {

    /**
     * Creates instances of custom block classes
     */
    public static final Block example_block = new ExampleBlock();
    public static final Block rocket_generic = new RocketGeneric();

    private static final Block[] blocks_to_register = {
            example_block,
            rocket_generic
    };

    /**
     * Registers the Block
     *
     * To register a Block replace example_block by the name of your block in
     * event.getRegistry().register(example_block);
     */
    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
        for (Block block : blocks_to_register)
            event.getRegistry().register(block);
    }

    /**
     * Registers the BlockItem
     *
     * To register a BlockItem replace example_block by the name of your block in
     * event.getRegistry().register(new BlockItem(example_block, new Item.Properties()).setRegistryName(example_block.getRegistryName()));
     */
    @SubscribeEvent
    public static void registerItemBlocks(final RegistryEvent.Register<Item> event) {
        for (Block block: blocks_to_register)
            //noinspection ConstantConditions
            event.getRegistry().register(new BlockItem(block, new Item.Properties().group(Main.mspItemGroup)).setRegistryName(block.getRegistryName()));
    }

}
