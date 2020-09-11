package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public class MSPDimensions {

  public static final RegistryKey<DimensionType> space = RegistryKey.func_240903_a_(Registry.DIMENSION_TYPE_KEY, name("space"));
  public static final RegistryKey<World> space_w = RegistryKey.func_240903_a_(Registry.WORLD_KEY, name("space"));

  private static ResourceLocation name(String name) {
    return new ResourceLocation(MSP.MOD_ID, name);
  }
}
