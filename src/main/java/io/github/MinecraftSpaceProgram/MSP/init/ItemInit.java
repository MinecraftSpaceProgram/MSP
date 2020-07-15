package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.Main;
import net.minecraft.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS,
            Main.MOD_ID);
}
