package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.item.ExampleItem;
import io.github.MinecraftSpaceProgram.MSP.item.HangarController;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemLoader {

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS,
            MSP.MOD_ID
    );

    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register(
            "example_item", ExampleItem::new
    );
    public static final RegistryObject<Item> HANGAR_CONTROLLER = ITEMS.register(
            "hangar_controller", HangarController::new
    );
}
