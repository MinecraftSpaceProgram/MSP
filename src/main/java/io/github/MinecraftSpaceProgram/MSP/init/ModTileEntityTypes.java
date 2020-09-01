package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.tileentity.GUIBlockTileEntity;
import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.tileentity.ExampleTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MSP.MOD_ID);

    public static final RegistryObject<TileEntityType<ExampleTileEntity>> EXAMPLE_TILEENTITY = TILE_ENTITY_TYPES.register(
            "example_tileentity", () -> TileEntityType.Builder
                    .create(ExampleTileEntity::new, BlockLoader.EXAMPLE_TILEENTITYBLOCK.get()).build(null)
    );

    public static final RegistryObject<TileEntityType<GUIBlockTileEntity>> GUI_BLOCK = TILE_ENTITY_TYPES.register(
            "gui_block", () -> TileEntityType.Builder
                    .create(GUIBlockTileEntity::new, BlockLoader.GUI_BLOCK.get()).build(null)
    );
}
