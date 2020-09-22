package io.github.MinecraftSpaceProgram.MSP.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
  // func_228424_a_
  @SuppressWarnings("resource")
  @Inject(
      at = @At("HEAD"),
      method = "renderSky(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V",
      cancellable = true)
  private void renderSky(MatrixStack matrixStackIn, float partialTicks, CallbackInfo callback) {
  }
}
