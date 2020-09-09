package io.github.MinecraftSpaceProgram.MSP.client.renderer.entity;

import io.github.MinecraftSpaceProgram.MSP.entity.SeatEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class SeatRenderer extends EntityRenderer<SeatEntity> {
  public SeatRenderer(EntityRendererManager renderManager) {
    super(renderManager);
  }

  @Override
  public ResourceLocation getEntityTexture(SeatEntity entity) {
    return null;
  }
}
