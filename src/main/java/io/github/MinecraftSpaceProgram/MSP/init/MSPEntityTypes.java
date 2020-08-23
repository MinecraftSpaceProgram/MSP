package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.entity.RocketEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("unused")
public final class MSPEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, MSP.MOD_ID);

    public static final RegistryObject<RocketEntityType> ROCKET_ENTITY_TYPE = ENTITY_TYPES.register(
            "rocket", () -> new RocketEntityType(new ResourceLocation(MSP.MOD_ID, "textures/block/tank.png"), true));
}
