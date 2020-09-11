package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class MSPBiomes {
  private static ResourceLocation name(String name) {
    return new ResourceLocation(MSP.MOD_ID, name);
  }

  public static final RegistryKey<Biome> spaceDimension = RegistryKey.func_240903_a_(Registry.BIOME_KEY, name("spaceDimension"));
}
