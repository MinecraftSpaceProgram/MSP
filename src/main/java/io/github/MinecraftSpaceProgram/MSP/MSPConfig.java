package io.github.MinecraftSpaceProgram.MSP;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MSPConfig {

    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;
    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    // These can be made private and replaced with getters
    public static boolean aBoolean;
    public static int anInt;

    public static void bakeConfig() {
        aBoolean = CLIENT.aBoolean.get();
        anInt = CLIENT.anInt.get();
    }

    // Doesn't need to be an inner class
    public static class ClientConfig {

        public final ForgeConfigSpec.BooleanValue aBoolean;
        public final ForgeConfigSpec.IntValue anInt;

        public ClientConfig(ForgeConfigSpec.Builder builder) {
            aBoolean = builder
                    .comment("aBoolean usage description")
                    .translation(Main.MOD_ID + ".config." + "aBoolean")
                    .define("aBoolean", false);

            builder.push("category");
            anInt = builder
                    .comment("anInt usage description")
                    .translation(Main.MOD_ID + ".config." + "anInt")
                    .defineInRange("anInt", 10, 0, 100);
            builder.pop();
        }
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == MSPConfig.CLIENT_SPEC) {
            bakeConfig();
        }
    }

}
