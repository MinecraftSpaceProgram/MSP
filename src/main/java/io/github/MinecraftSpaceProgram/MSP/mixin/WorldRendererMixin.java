package io.github.MinecraftSpaceProgram.MSP.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.MinecraftSpaceProgram.MSP.client.SpaceSkyRenderer;
import io.github.MinecraftSpaceProgram.MSP.init.MSPDimensions;
import net.minecraft.client.Minecraft;
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
    assert Minecraft.getInstance().world != null;
    if (Minecraft.getInstance().world.func_234923_W_() == MSPDimensions.SPACE_W)
    {
      SpaceSkyRenderer.INSTANCE.render(partialTicks, matrixStackIn, Minecraft.getInstance().world, Minecraft.getInstance());
      callback.cancel();
    }
  }

  // Removes Clouds
  @SuppressWarnings("resource")
  @Inject(
      at = @At("HEAD"),
      method = "renderClouds(Lcom/mojang/blaze3d/matrix/MatrixStack;FDDD)V",
      cancellable = true)
  private void renderClouds(MatrixStack matrixStackIn, float partialTicks, double viewEntityX, double viewEntityY, double viewEntityZ, CallbackInfo callback) {
    assert Minecraft.getInstance().world != null;
    if (Minecraft.getInstance().world.func_234923_W_() == MSPDimensions.SPACE_W)
    {
      callback.cancel();
    }
  }
}
