package io.github.MinecraftSpaceProgram.MSP.entity;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;

public class RocketEntityType extends EntityType<RocketEntity> {
    public final ResourceLocation texture;

    public RocketEntityType(ResourceLocation texture, boolean immuneToFire) {
        super(
                RocketEntity::new,
                EntityClassification.MISC,
                true,
                true,
                immuneToFire,
                true,
                ImmutableSet.of(),
                EntitySize.flexible(2.0f, 0.5f),
                5,
                3);
        this.texture = texture;
    }
}
