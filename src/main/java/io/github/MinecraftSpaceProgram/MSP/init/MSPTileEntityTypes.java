package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.tileentity.ExampleTileEntity;
import io.github.MinecraftSpaceProgram.MSP.tileentity.GUIBlockTileEntity;
import io.github.MinecraftSpaceProgram.MSP.tileentity.HydrazineTankTileEntity;
import io.github.MinecraftSpaceProgram.MSP.tileentity.LaunchpadControllerTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("ConstantConditions")
public final class MSPTileEntityTypes {

  public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES =
      DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MSP.MOD_ID);

  public static final RegistryObject<TileEntityType<ExampleTileEntity>> EXAMPLE_TILEENTITY =
      TILE_ENTITY_TYPES.register(
          "example_tileentity",
          () ->
              TileEntityType.Builder.create(
                      ExampleTileEntity::new, MSPBlocks.EXAMPLE_TILEENTITYBLOCK.get())
                  .build(null));
  public static final RegistryObject<TileEntityType<LaunchpadControllerTileEntity>>
      LAUNCHPAD_CONTROLLER =
          TILE_ENTITY_TYPES.register(
              "launchpad_controller",
              () ->
                  TileEntityType.Builder.create(
                          LaunchpadControllerTileEntity::new, MSPBlocks.LAUNCHPAD_CONTROLLER.get())
                      .build(null));
  public static final RegistryObject<TileEntityType<GUIBlockTileEntity>> GUI_BLOCK =
      TILE_ENTITY_TYPES.register(
          "gui_block",
          () ->
              TileEntityType.Builder.create(GUIBlockTileEntity::new, MSPBlocks.GUI_BLOCK.get())
                  .build(null));
  public static final RegistryObject<TileEntityType<HydrazineTankTileEntity>>
      HYDRAZINE_TANK_TILE_ENTITY =
          TILE_ENTITY_TYPES.register(
              "hydrazine_tank_tile_entity",
              () ->
                  TileEntityType.Builder.create(
                          HydrazineTankTileEntity::new, MSPBlocks.HYDRAZINE_TANK_BLOCK.get())
                      .build(null));
}
