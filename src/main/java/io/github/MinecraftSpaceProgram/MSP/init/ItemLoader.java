package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.item.ExampleItem;
import io.github.MinecraftSpaceProgram.MSP.item.HangarControllerItem;
import io.github.MinecraftSpaceProgram.MSP.item.RocketAssemblerItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemLoader {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MSP.MOD_ID);

    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register(
            "example_item", ExampleItem::new
    );
    public static final RegistryObject<Item> HANGAR_CONTROLLER = ITEMS.register(
            "hangar_controller", HangarControllerItem::new
    );
    public static final RegistryObject<Item> ROCKET_ASSEMBLER = ITEMS.register(
            "rocket_assembler", RocketAssemblerItem::new
    );
}
