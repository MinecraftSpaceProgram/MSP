package io.github.MinecraftSpaceProgram.MSP.init;

import com.google.common.collect.Maps;
import com.mojang.serialization.Lifecycle;
import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.world.space.MSPBiomes;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.FuzzedBiomeMagnifier;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.gen.settings.ScalingSettings;
import net.minecraft.world.gen.settings.SlideSettings;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.function.Supplier;

public class MSPDimensions {

  public static final RegistryKey<DimensionType> space =
      RegistryKey.func_240903_a_(Registry.DIMENSION_TYPE_KEY, name("space"));
  public static final RegistryKey<World> space_w =
      RegistryKey.func_240903_a_(Registry.WORLD_KEY, name("space"));

  private static ResourceLocation name(String name) {
    return new ResourceLocation(MSP.MOD_ID, name);
  }

  public static final RegistryKey<World> SPACE_W =
      RegistryKey.func_240903_a_(Registry.WORLD_KEY, name("space2"));

  public static final RegistryKey<DimensionSettings> SPACE_NOISE_SETTINGS =
      RegistryKey.func_240903_a_(Registry.field_243549_ar, name("space2"));

  public static final RegistryKey<DimensionType> SPACE_TYPE =
      RegistryKey.func_240903_a_(Registry.DIMENSION_TYPE_KEY, name("space2"));

  public static final RegistryKey<Dimension> SPACE_DIM =
      RegistryKey.func_240903_a_(Registry.DIMENSION_KEY, name("space2"));

  public static void init(
      SimpleRegistry<Dimension> simpleRegistry,
      MutableRegistry<DimensionType> mutableRegistry,
      MutableRegistry<Biome> biomeRegistry,
      MutableRegistry<DimensionSettings> dimSettingsRegistry,
      long seed) {
    Function<RegistryKey<DimensionSettings>, DimensionSettings> spaceSettings =
        (noiseSettings) ->
            createNoiseSettings(
                new DimensionStructuresSettings(false),
                false,
                Blocks.AIR.getDefaultState(),
                Blocks.WATER.getDefaultState(),
                SPACE_NOISE_SETTINGS.func_240901_a_());

    Function<DimensionSettings, ChunkGenerator> spaceGenerator =
        (s) -> createSpaceChunkGenerator(biomeRegistry, dimSettingsRegistry, seed);
    Supplier<DimensionType> spaceDimensionType =
        () -> createDimSettings(OptionalLong.of(18000), false, false);

    Dimension space2 =
        new Dimension(
            spaceDimensionType, spaceGenerator.apply(spaceSettings.apply(SPACE_NOISE_SETTINGS)));

    simpleRegistry.register(SPACE_DIM, space2, Lifecycle.stable());
  }

  public static void initNoiseSettings() {
    WorldGenRegistries.func_243664_a(
        WorldGenRegistries.field_243658_j,
        SPACE_NOISE_SETTINGS.func_240901_a_(),
        Objects.requireNonNull(
            createNoiseSettings(
                new DimensionStructuresSettings(false),
                false,
                Blocks.STONE.getDefaultState(),
                Blocks.WATER.getDefaultState(),
                SPACE_NOISE_SETTINGS.func_240901_a_())));
  }

  public static DimensionSettings createNoiseSettings(
      DimensionStructuresSettings structureSettingsIn,
      boolean flag1,
      BlockState fillerBlockIn,
      BlockState fluidBlockIn,
      ResourceLocation resourceLocation) {
    try {
      Constructor<DimensionSettings> constructor =
          DimensionSettings.class.getDeclaredConstructor(
              DimensionStructuresSettings.class,
              NoiseSettings.class,
              BlockState.class,
              BlockState.class,
              int.class,
              int.class,
              int.class,
              boolean.class);
      constructor.setAccessible(true);
      return constructor.newInstance(
          structureSettingsIn,
          new NoiseSettings(
              256,
              new ScalingSettings(0.9999999814507745D, 0.9999999814507745D, 80.0D, 160.0D),
              new SlideSettings(-10, 3, 0),
              new SlideSettings(-30, 0, 0),
              1,
              2,
              1.0D,
              -0.46875D,
              true,
              true,
              false,
              flag1),
          fillerBlockIn,
          fluidBlockIn,
          -10,
          0,
          63,
          false);
    } catch (Exception e) {
      MSP.LOGGER.error("Failed to create dimension settings. This issue should be reported!");
      e.printStackTrace();
    }

    return null;
  }

  private static ChunkGenerator createSpaceChunkGenerator(
      Registry<Biome> biomeRegistry, Registry<DimensionSettings> dimSettingsRegistry, long seed) {
    Optional<Supplier<Biome>> optionalSupplierBiome =
        Optional.of(() -> biomeRegistry.func_243576_d(MSPBiomes.SPACE_BIOME));
    DimensionStructuresSettings dimensionstructuressettings =
        new DimensionStructuresSettings(Optional.empty(), Maps.newHashMap());
    FlatGenerationSettings flatGenerationSettings =
        new FlatGenerationSettings(
            biomeRegistry,
            dimensionstructuressettings,
            new ArrayList<>(),
            false,
            false,
            optionalSupplierBiome);
    return new FlatChunkGenerator(flatGenerationSettings);
  }

  private static DimensionType createDimSettings(
      OptionalLong time, boolean ultrawarm, boolean piglinSafe) {
    return new DimensionType(
        time,
        true,
        false,
        ultrawarm,
        true,
        1,
        false,
        piglinSafe,
        true,
        false,
        false,
        256,
        FuzzedBiomeMagnifier.INSTANCE,
        BlockTags.INFINIBURN_OVERWORLD.getName(),
        new ResourceLocation("space2"),
        0.0F) {};
  }
}
