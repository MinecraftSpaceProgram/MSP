package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.entity.RocketEntityType;
import io.github.MinecraftSpaceProgram.MSP.entity.SeatEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("unused")
public final class MSPEntityTypes {
  public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
      DeferredRegister.create(ForgeRegistries.ENTITIES, MSP.MOD_ID);

  public static final RegistryObject<RocketEntityType> ROCKET_ENTITY_TYPE =
      ENTITY_TYPES.register(
          "rocket",
          () ->
              new RocketEntityType(
                  new ResourceLocation(MSP.MOD_ID, "textures/block/tank.png"), true));

  public static final RegistryObject<EntityType<SeatEntity>> SEAT_TYPE =
      ENTITY_TYPES.register(
          "seat",
          () ->
              EntityType.Builder.create(SeatEntity::new, EntityClassification.MISC)
                  .size(0.01F, 0.01F)
                  .build(new ResourceLocation(MSP.MOD_ID, "seat").toString()));
}
