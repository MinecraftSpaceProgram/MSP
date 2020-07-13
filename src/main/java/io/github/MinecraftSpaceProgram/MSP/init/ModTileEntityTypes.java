package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.tileentity.ExampleTileEntity;
import io.github.MinecraftSpaceProgram.MSP.tileentity.HangarCornerTileEntity;
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
    public static final RegistryObject<TileEntityType<HangarCornerTileEntity>> HANGAR_CORNER_TILEENTITY = TILE_ENTITY_TYPES.register(
            "hangar_corner", () -> TileEntityType.Builder
                    .create(HangarCornerTileEntity::new, BlockLoader.EXAMPLE_TILEENTITYBLOCK.get()).build(null)
    );
}
