package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.block.*;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockLoader {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MSP.MOD_ID);

    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register(
            "example_block", ExampleBlock::new
    );
    public static final RegistryObject<Block> EXAMPLE_TILEENTITYBLOCK = BLOCKS.register(
            "example_tileentity", ExampleTileEntityBlock::new
    );
    public static final RegistryObject<Block> HANGAR_CORNER = BLOCKS.register(
            "hangar_corner", HangarCornerBlock::new
    );
    public static final RegistryObject<Block> ROCKET_GENERIC = BLOCKS.register(
            "rocket_generic", RocketGenericBlock::new
    );
    public static final RegistryObject<Block> FLIGHT_CONTROLLER = BLOCKS.register(
            "flight_controller", FlightControllerBlock::new
    );
    public static final RegistryObject<Block> ROCKET_ENGINE = BLOCKS.register(
            "engine", EngineBlock::new
    );
    public static final RegistryObject<Block> TANK = BLOCKS.register(
            "tank", ExampleBlock::new
    );
}
