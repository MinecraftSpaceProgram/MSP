package io.github.MinecraftSpaceProgram.MSP.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.SkyRenderHandler;

@OnlyIn(Dist.CLIENT)
public class ClientForgeEventHandler implements SkyRenderHandler {

  @Override
  @OnlyIn(Dist.CLIENT)
  public void render(int ticks, float partialTicks, MatrixStack ms, ClientWorld world, Minecraft mc) {
    // [VanillaCopy] Excerpt from RenderGlobal.loadRenderers as we don't get a callback
//		boolean flag = this.vboEnabled;
//		this.vboEnabled = OpenGlHelper.useVbo();
//		if (flag != this.vboEnabled) {
//			generateStars();
//		}

    WorldRenderer rg = mc.worldRenderer;
    //int pass = GameRenderer.anaglyphEnable ? GameRenderer.anaglyphField : 2;

    RenderSystem.disableTexture();
    Vector3d vec3d = world.getSkyColor(mc.gameRenderer.getActiveRenderInfo().getBlockPos(), partialTicks);
    float f = (float) vec3d.x;
    float f1 = (float) vec3d.y;
    float f2 = (float) vec3d.z;

//		if (pass != 2) {
//			float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
//			float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
//			float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
//			f = f3;
//			f1 = f4;
//			f2 = f5;
//		}

    RenderSystem.color3f(f, f1, f2);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferbuilder = tessellator.getBuffer();
    RenderSystem.depthMask(false);
    RenderSystem.enableFog();
    RenderSystem.color3f(f, f1, f2);

    //TODO
//		if (this.vboEnabled) {
//			rg.skyVBO.bindBuffer();
//			RenderSystem.glEnableClientState(32884);
//			RenderSystem.glVertexPointer(3, 5126, 12, 0);
//			rg.skyVBO.drawArrays(7);
//			rg.skyVBO.unbindBuffer();
//			RenderSystem.glDisableClientState(32884);
//		} else {
//			RenderSystem.callList(rg.glSkyList);
//		}

    RenderSystem.disableFog();
    RenderSystem.disableAlphaTest();
    RenderSystem.enableBlend();
    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    RenderHelper.disableStandardItemLighting();
    /* TF - snip out sunrise/sunset since that doesn't happen here
     * float[] afloat = ...
     * if (afloat != null) ...
     */

    RenderSystem.enableTexture();
    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    ms.push();
    float f16 = 1.0F - world.getRainStrength(partialTicks);
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, f16);
    RenderSystem.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
    RenderSystem.rotatef(world.getCelestialAngleRadians(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
    /* TF - snip out sun/moon
     * float f17 = 30.0F;
     * ...
     * tessellator.draw();
     */
    RenderSystem.disableTexture();
    float f15 = 1.0F; // TF - stars are always bright

    if (f15 > 0.0F) {
      RenderSystem.color4f(f15, f15, f15, f15);

      //TODO
//			if (this.vboEnabled) {
//				this.starVBO.bindBuffer();
//				RenderSystem.glEnableClientState(32884);
//				RenderSystem.glVertexPointer(3, 5126, 12, 0);
//				this.starVBO.drawArrays(7);
//				this.starVBO.unbindBuffer();
//				RenderSystem.glDisableClientState(32884);
//			} else {
//				RenderSystem.callList(this.starGLCallList);
//			}
    }

    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.disableBlend();
    RenderSystem.enableAlphaTest();
    RenderSystem.enableFog();
    ms.pop();
    RenderSystem.disableTexture();
    RenderSystem.color3f(0.0F, 0.0F, 0.0F);
    double d0 = mc.player.getEyePosition(partialTicks).y - world.getSeaLevel();

    if (d0 < 0.0D) {
      ms.push();
      RenderSystem.translatef(0.0F, 12.0F, 0.0F);

      //TODO
//			if (this.vboEnabled) {
//				rg.sky2VBO.bindBuffer();
//				RenderSystem.glEnableClientState(32884);
//				RenderSystem.glVertexPointer(3, 5126, 12, 0);
//				rg.sky2VBO.drawArrays(7);
//				rg.sky2VBO.unbindBuffer();
//				RenderSystem.glDisableClientState(32884);
//			} else {
//				RenderSystem.callList(rg.glSkyList2);
//			}

      RenderSystem.popMatrix();
      float f18 = 1.0F;
      float f19 = -((float) (d0 + 65.0D));
      float f20 = -1.0F;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos(-1.0D, f19, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, f19, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, f19, -1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(-1.0D, f19, -1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, f19, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, f19, -1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(-1.0D, f19, -1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(-1.0D, f19, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
      tessellator.draw();
    }

    //if (world.func_239132_a_().isSkyColored()) {
    //  RenderSystem.color3f(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
    //} else {
      RenderSystem.color3f(f, f1, f2);
    //}

    ms.push();
    RenderSystem.translatef(0.0F, -((float) (d0 - 16.0D)), 0.0F);
    //RenderSystem.callList(rg.glSkyList2);
    ms.pop();
    RenderSystem.enableTexture();
    RenderSystem.depthMask(true);
  }
}
