package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.Main;
import io.github.MinecraftSpaceProgram.MSP.block.ExampleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Main.MODID)
@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockInit {

    /**
     * Creates an instance of the custom block class
     */
    public static final Block example_block = new ExampleBlock();

    /**
     * Registers the Block
     *
     * To register a Block replace example_block by the name of your block in
     * event.getRegistry().register(example_block);
     */
    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
        event.getRegistry().register(example_block);
    }

    /**
     * Registers the BlockItem
     *
     * To register a BlockItem replace example_block by the name of your block in
     * event.getRegistry().register(new BlockItem(example_block, new Item.Properties()).setRegistryName(example_block.getRegistryName()));
     */
    @SubscribeEvent
    public static void registerItemBlocks(final RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new BlockItem(example_block, new Item.Properties()).setRegistryName(example_block.getRegistryName()));
    }

}
