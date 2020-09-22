package io.github.MinecraftSpaceProgram.MSP.world.space;

import com.mojang.serialization.Codec;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ChunkProviderSpace extends ChunkGenerator {

  World worldObj;

  public ChunkProviderSpace(BiomeProvider p_i231888_1_, DimensionStructuresSettings p_i231888_2_) {
    super(p_i231888_1_, p_i231888_2_);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected Codec<? extends ChunkGenerator> func_230347_a_() {
    return null;
  }

  @Override
  public ChunkGenerator func_230349_a_(long p_230349_1_) {
    return null;
  }

  @Override
  public void generateSurface(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {
  }

  @Override
  public void func_230352_b_(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_) {

  }

  @Override
  public int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type heightmapType) {
    return 0;
  }

  @Override
  public IBlockReader func_230348_a_(int p_230348_1_, int p_230348_2_) {
    return null;
  }
}