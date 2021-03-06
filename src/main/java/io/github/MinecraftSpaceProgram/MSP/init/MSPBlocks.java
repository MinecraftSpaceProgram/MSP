package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.block.*;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("unused")
public final class MSPBlocks {
  public static final DeferredRegister<Block> BLOCKS =
      DeferredRegister.create(ForgeRegistries.BLOCKS, MSP.MOD_ID);

  public static final RegistryObject<Block> EXAMPLE_BLOCK =
      BLOCKS.register("example_block", () -> new Block(Properties.create(Material.ROCK)));
  public static final RegistryObject<Block> EXAMPLE_TILEENTITYBLOCK =
      BLOCKS.register("example_tileentity", ExampleTileEntityBlock::new);
  public static final RegistryObject<Block> ROCKET_GENERIC =
      BLOCKS.register("rocket_generic", RocketGenericBlock::new);
  public static final RegistryObject<Block> FLIGHT_CONTROLLER =
      BLOCKS.register("flight_controller", FlightControllerBlock::new);
  public static final RegistryObject<Block> ROCKET_ENGINE =
      BLOCKS.register("engine", EngineBlock::new);
  public static final RegistryObject<Block> EJECTOR_SEAT =
      BLOCKS.register("seat", EjectorSeatBlock::new);
  public static final RegistryObject<Block> TANK =
      BLOCKS.register("tank", () -> new Block(Properties.create(Material.GLASS).notSolid()));
  public static final RegistryObject<Block> LAUNCHPAD_BASE =
      BLOCKS.register("launchpad_base", LaunchpadBaseBlock::new);
  public static final RegistryObject<Block> LAUNCHPAD_CRANE =
      BLOCKS.register(
          "launchpad_crane", () -> new Block(Properties.create(Material.GLASS).notSolid()));
  public static final RegistryObject<Block> LAUNCHPAD_CONTROLLER =
      BLOCKS.register(
          "launchpad_controller",
          () -> new LaunchpadControllerBlock(Properties.create(Material.IRON)));
  public static final RegistryObject<Block> SPACE_EMPTY =
      BLOCKS.register("space_empty", () -> new AirBlock(Properties.create(Material.AIR)));
  public static final RegistryObject<Block> GUI_BLOCK = BLOCKS.register("gui_block", GUIBlock::new);
  public static final RegistryObject<Block> HYDRAZINE_TANK_BLOCK =
      BLOCKS.register("hydrazine_tank_block", () -> new HydrazineTankBlock(Properties.create(Material.IRON).notSolid()));
}
