package io.github.MinecraftSpaceProgram.MSP.world.space;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.init.MSPDimensions;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class MSPBiomes {
  public static Biome space_biome;
  public static RegistryKey<Biome> SPACE_BIOME = RegistryKey.func_240903_a_(Registry.BIOME_KEY, new ResourceLocation(MSP.MOD_ID, "space_biome"));

  private static int getSkyColor(float p_244206_0_) {
    float lvt_1_1_ = p_244206_0_ / 3.0F;
    lvt_1_1_ = MathHelper.clamp(lvt_1_1_, -1.0F, 1.0F);
    return MathHelper.hsvToRGB(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
  }

  public static void registerBiomes(RegistryEvent.Register<Biome> event) {

    IForgeRegistry<Biome> registry = event.getRegistry();

    MSPDimensions.initNoiseSettings();

    BiomeAmbience noAmbience =
        new BiomeAmbience.Builder()
            .func_242539_d(/*sky color*/ getSkyColor(0.6F))
            .func_235246_b_(/*water color*/ 4159204)
            .func_235248_c_(/*water fog color*/ 329011)
            .func_235239_a_(/*fog color*/ 12638463)
            .func_235238_a_();

    MobSpawnInfo noMobs = new MobSpawnInfo.Builder().func_242577_b();

    space_biome =
        new Biome.Builder()
            .category(Biome.Category.NONE)
            .downfall(0)
            .precipitation(Biome.RainType.NONE)
            .depth(-2.0F)
            .scale(0)
            .temperature(1.0F)
            .func_242457_a(BiomeGenerationSettings.field_242480_b)
            .func_235097_a_(noAmbience)
            .func_242458_a(noMobs)
            .func_242455_a();

    space_biome.setRegistryName(new ResourceLocation(MSP.MOD_ID, "space_biome"));
    registry.register(space_biome);
  }
}
