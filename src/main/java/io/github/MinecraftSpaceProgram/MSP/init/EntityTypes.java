package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.entity.RocketEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, MSP.MOD_ID);

    public static final RegistryObject<EntityType<RocketEntity>> ROCKET_ENTITY_TYPE = ENTITY_TYPES.register(
            "rocket",
            () -> EntityType.Builder.<RocketEntity>create(RocketEntity::new, EntityClassification.MISC).size(1.0F,1.0F).build("rocket")
    );
}
