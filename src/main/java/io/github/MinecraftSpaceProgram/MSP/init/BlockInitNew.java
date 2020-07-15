package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.Main;
import io.github.MinecraftSpaceProgram.MSP.block.FlightComputer;
import io.github.MinecraftSpaceProgram.MSP.block.GUIBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockInitNew {

    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, Main.MOD_ID);

    public static final RegistryObject<Block> FLIGHT_COMPUTER = BLOCKS.register(
            "flight_computer", () -> new FlightComputer(Block.Properties.create(Material.IRON)));

    public static final RegistryObject<Block> GUI_BLOCK = BLOCKS.register(
            "gui_block", GUIBlock::new);

}
