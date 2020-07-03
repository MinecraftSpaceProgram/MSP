package io.github.MinecraftSpaceProgram.MSP;

import io.github.MinecraftSpaceProgram.MSP.init.ItemLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Main.MODID)
public final class Main {

    public static final String MODID="msp";
    public static final Logger LOGGER = LogManager.getLogger();

    public Main() {
        LOGGER.debug("Hello world");
    }

    public static final ItemGroup mspItemGroup = new ItemGroup("itemgroup.MSP") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ItemLoader.soldering_iron);
        }
    };
}
