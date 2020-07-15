package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.Main;
import io.github.MinecraftSpaceProgram.MSP.tileentity.FlightComputerTileEntity;
import io.github.MinecraftSpaceProgram.MSP.tileentity.GUIBlockTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = new DeferredRegister<>(
            ForgeRegistries.TILE_ENTITIES, Main.MOD_ID
    );

    public static final RegistryObject<TileEntityType<FlightComputerTileEntity>> FLIGHT_COMPUTER = TILE_ENTITY_TYPES.register(
            "flight_computer", () -> TileEntityType.Builder
                    .create(FlightComputerTileEntity::new, BlockInitNew.FLIGHT_COMPUTER.get()).build(null));

    public static final RegistryObject<TileEntityType<GUIBlockTileEntity>> GUI_BLOCK = TILE_ENTITY_TYPES.register(
            "gui_block", () -> TileEntityType.Builder
                    .create(GUIBlockTileEntity::new, BlockInitNew.GUI_BLOCK.get()).build(null));
}
