package io.github.MinecraftSpaceProgram.MSP.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Random;

@SuppressWarnings("deprecation")
public class SpaceSkyRenderer {
  private VertexBuffer starVBO;

  private final VertexFormat skyVertexFormat = DefaultVertexFormats.POSITION;

  public static final SpaceSkyRenderer INSTANCE = new SpaceSkyRenderer();

  public SpaceSkyRenderer() {
    generateStars();
  }

  public void render(
      float partialTicks, MatrixStack matrixStackIn, ClientWorld world, Minecraft mc) {
    RenderSystem.disableTexture();
    FogRenderer.applyFog();
    RenderSystem.depthMask(false);
    RenderSystem.enableFog();

    skyVertexFormat.setupBufferState(0L);
    VertexBuffer.unbindBuffer();
    skyVertexFormat.clearBufferState();
    RenderSystem.disableFog();
    RenderSystem.disableAlphaTest();
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();

    RenderSystem.blendFuncSeparate(
        GlStateManager.SourceFactor.SRC_ALPHA,
        GlStateManager.DestFactor.ONE,
        GlStateManager.SourceFactor.ONE,
        GlStateManager.DestFactor.ZERO);
    matrixStackIn.push();
    float f11 = 1.0F - world.getRainStrength(partialTicks);
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, f11);
    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-90.0F));
    matrixStackIn.rotate(Vector3f.XP.rotationDegrees(1.0F * 360.0F));
    RenderSystem.disableTexture();

    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    starVBO.bindBuffer();
    skyVertexFormat.setupBufferState(0L);
    starVBO.draw(matrixStackIn.getLast().getMatrix(), 7);
    VertexBuffer.unbindBuffer();
    skyVertexFormat.clearBufferState();

    RenderSystem.disableBlend();
    RenderSystem.enableAlphaTest();
    RenderSystem.enableFog();
    matrixStackIn.pop();
    RenderSystem.disableTexture();

    RenderSystem.enableTexture();
    RenderSystem.depthMask(true);
    RenderSystem.disableFog();
  }

  private void generateStars() {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferbuilder = tessellator.getBuffer();
    if (starVBO != null) {
      starVBO.close();
    }

    starVBO = new VertexBuffer(skyVertexFormat);
    renderStars(bufferbuilder);
    bufferbuilder.finishDrawing();
    starVBO.upload(bufferbuilder);
  }

  // Taken from the good night sleep mod
  private void renderStars(BufferBuilder bufferBuilderIn) {
    Random random = new Random(10842L);
    bufferBuilderIn.begin(7, DefaultVertexFormats.POSITION);

    for (int i = 0; i < 1500; ++i) {
      double d0 = random.nextFloat() * 2.0F - 1.0F;
      double d1 = random.nextFloat() * 2.0F - 1.0F;
      double d2 = random.nextFloat() * 2.0F - 1.0F;
      double d3 = 0.15F + random.nextFloat() * 0.1F;
      double d4 = d0 * d0 + d1 * d1 + d2 * d2;
      if (d4 < 1.0D && d4 > 0.01D) {
        d4 = 1.0D / Math.sqrt(d4);
        d0 = d0 * d4;
        d1 = d1 * d4;
        d2 = d2 * d4;
        double d5 = d0 * 100.0D;
        double d6 = d1 * 100.0D;
        double d7 = d2 * 100.0D;
        double d8 = Math.atan2(d0, d2);
        double d9 = Math.sin(d8);
        double d10 = Math.cos(d8);
        double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
        double d12 = Math.sin(d11);
        double d13 = Math.cos(d11);
        double d14 = random.nextDouble() * Math.PI * 2.0D;
        double d15 = Math.sin(d14);
        double d16 = Math.cos(d14);

        for (int j = 0; j < 4; ++j) {
          double d18 = (double) ((j & 2) - 1) * d3;
          double d19 = (double) ((j + 1 & 2) - 1) * d3;
          double d21 = d18 * d16 - d19 * d15;
          double d22 = d19 * d16 + d18 * d15;
          double d23 = d21 * d12 + 0.0D * d13;
          double d24 = 0.0D * d12 - d21 * d13;
          double d25 = d24 * d9 - d22 * d10;
          double d26 = d22 * d9 + d24 * d10;
          bufferBuilderIn.pos(d5 + d25, d6 + d23, d7 + d26).endVertex();
        }
      }
    }
  }

}
