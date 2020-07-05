package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.Main;
import io.github.MinecraftSpaceProgram.MSP.item.ExampleItem;
import io.github.MinecraftSpaceProgram.MSP.item.SolderingIron;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS,
            Main.MOD_ID
    );

    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register(
            "example_item", ExampleItem::new
    );
    public static final RegistryObject<Item> SOLDERING_IRON = ITEMS.register(
            "soldering_iron", SolderingIron::new
    );
}
