package io.github.MinecraftSpaceProgram.MSP.world.space;

import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.ColumnFuzzedBiomeMagnifier;

import java.util.OptionalLong;

public class SpaceDimensionType extends DimensionType {
  public SpaceDimensionType(ResourceLocation dimId) {
    super(
        // TFF natural, beds,
        // TFT raids, skylight,
        // FTF ultrawarm, piglin safe, respawn anchors, ceiling
        // FFT ?

        OptionalLong.empty(), // fixed time
        true, // TFF
        false, // FTF
        false, // FTF
        true, //TFF
        1.0D, // coordinate scale
        false,  // FFT
        false, // FTF
        true, //TFF
        false, // FTF
        true, // TFT
        256, // logical height
        ColumnFuzzedBiomeMagnifier.INSTANCE,
        BlockTags.INFINIBURN_OVERWORLD.getName(), // infiniburn
        dimId, // id
        0.0F // ambiant light
        );
  }
}
