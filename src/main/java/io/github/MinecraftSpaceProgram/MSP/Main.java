package io.github.MinecraftSpaceProgram.MSP;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Main.MODID)
public final class Main {

    public static final String MODID="msp";

    private static final Logger LOGGER = LogManager.getLogger();

    public Main() {
        LOGGER.debug("Hello world");
    }


}
