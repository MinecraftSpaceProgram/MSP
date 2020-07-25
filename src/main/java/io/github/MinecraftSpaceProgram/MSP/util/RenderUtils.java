package io.github.MinecraftSpaceProgram.MSP.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.MinecraftSpaceProgram.MSP.physics.orbital.CelestialBody;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;

import java.awt.*;

import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.AU;
import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.E;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;

@SuppressWarnings("deprecation")
public final class RenderUtils {

    /**
     * Draw a coloured line from a starting vertex to an end vertex
     *
     * @param matrixPos    the current transformation matrix
     * @param renderBuffer the vertex builder used to draw the line
     * @param startVertex  the starting vertex
     * @param endVertex    the end vertex
     */
    public static void drawLine(Matrix4f matrixPos, IVertexBuilder renderBuffer,
                                Color color, Vector3d startVertex, Vector3d endVertex) {
        renderBuffer.pos(matrixPos, (float) startVertex.getX(), (float) startVertex.getY(), (float) startVertex.getZ())
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .endVertex();
        renderBuffer.pos(matrixPos, (float) endVertex.getX(), (float) endVertex.getY(), (float) endVertex.getZ())
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .endVertex();
    }

    /**
     * Draws a crosshair indicating  x, y, z axis on screen at a given point
     * @param matrixStack a matrix stack to move
     * @param renderBuffers a render buffer to draw
     * @param origin where to draw the crosshair
     */
    public static void drawCrosshairs(MatrixStack matrixStack, IRenderTypeBuffer renderBuffers, Vector3d origin) {
        // IVertex builder to draw lines
        IVertexBuilder vertexBuilderLines = renderBuffers.getBuffer(RenderType.getLines());

        // retrieves the current transformation matrix
        Matrix4f matrixPos = matrixStack.getLast().getMatrix();

        // draws the crosshair
        drawLine(matrixPos, vertexBuilderLines, Color.RED, origin, origin.add(new Vector3d(10, 0, 0)));
        drawLine(matrixPos, vertexBuilderLines, Color.GREEN, origin, origin.add(new Vector3d(0, 10, 0)));
        drawLine(matrixPos, vertexBuilderLines, Color.BLUE, origin, origin.add(new Vector3d(0, 0, 10)));
    }

    /**
     * Draws the trajectory of a given celestial body
     *
     * @param matrixPos    the current transformation matrix
     * @param renderBuffer the vertex builder used to draw the line
     * @param body         the celestial body
     */
    public static void drawTrajectory(Matrix4f matrixPos, IVertexBuilder renderBuffer, Color color, CelestialBody body) {
        Vector3d[] trajectory = body.trajectory;
        if (body.trajectory != null) {
            for (int i = 0; i < trajectory.length - 1; i++) {
                drawLine(matrixPos, renderBuffer, color, trajectory[i], trajectory[i + 1]);
            }
            drawLine(matrixPos, renderBuffer, color, trajectory[trajectory.length - 1], trajectory[0]);
        }
    }

    /**
     * Adds a vertex at point A to a mesh
     */
    private static void add(IVertexBuilder renderer, Matrix4f matrixPos, Vector3d A, Vector3d N, float u, float v, float opacity) {
        renderer.pos(matrixPos, (float) A.x, (float) A.y, (float) A.z)
                .color(1.0F, 1.0F, 1.0F, opacity)
                .tex(u, v)
                .lightmap(0, 240)
                .normal((float) N.x, (float) N.y, (float) N.z)
                .endVertex();
    }

    /**
     * Draws colored parallelogram between 2 vectors A and B at C
     */
    private static void square(IVertexBuilder renderer, Matrix4f matrixPos, Vector3d A, Vector3d B, Vector3d C, RenderMaterial material, float opacity) {
        Vector3d N = A.crossProduct(B).normalize();
        float u = material.getSprite().getMinU();
        float U = material.getSprite().getMaxU();
        float v = material.getSprite().getMinV();
        float V = material.getSprite().getMaxV();
        add(renderer, matrixPos, C, N, u, v, opacity);
        add(renderer, matrixPos, A.add(C), N, U, v, opacity);
        add(renderer, matrixPos, A.add(B).add(C), N, U, V, opacity);
        add(renderer, matrixPos, B.add(C), N, u, V, opacity);
    }

    /**
     * Draws a fucking cube
     * around point A, B and C
     */
    public static void drawCube(Vector3d A, Vector3d B, Vector3d C,
                                MatrixStack matrixStack, IVertexBuilder renderBuffer, RenderMaterial material, float opacity) {
        // retrieves a position matrix
        Matrix4f matrixPos = matrixStack.getLast().getMatrix();

        // draws the 6 sides
        square(renderBuffer, matrixPos, A, B, Vector3d.ZERO, material, opacity);
        square(renderBuffer, matrixPos, B, A, C, material, opacity);

        square(renderBuffer, matrixPos, C, A, Vector3d.ZERO, material, opacity);
        square(renderBuffer, matrixPos, A, C, B, material, opacity);

        square(renderBuffer, matrixPos, B, C, Vector3d.ZERO, material, opacity);
        square(renderBuffer, matrixPos, C, B, A, material, opacity);
    }

    public static void drawCube(Vector3d A, Vector3d B, Vector3d C,
                                MatrixStack matrixStack, IVertexBuilder renderBuffer, RenderMaterial material) {
        drawCube(A, B, C, matrixStack, renderBuffer, material, 0.0F);
    }

    /**
     * Draws an inverted cube (used for Sky Box)
     */
    public static void drawInvertedCube(Vector3d A, Vector3d B, Vector3d C,
                                        Minecraft mc, Tessellator tessellator, MatrixStack matrixStack, BufferBuilder renderBuffer, ResourceLocation material) {
        // retrieves a position matrix
        Matrix4f matrixPos = matrixStack.getLast().getMatrix();

        // draws the 6 sides
        square2(mc, tessellator, renderBuffer, matrixPos, B, A, Vector3d.ZERO, material);
        square2(mc, tessellator, renderBuffer, matrixPos, A, B, C, material);

        square2(mc, tessellator, renderBuffer, matrixPos, A, C, Vector3d.ZERO, material);
        square2(mc, tessellator, renderBuffer, matrixPos, C, A, B, material);

        square2(mc, tessellator, renderBuffer, matrixPos, C, B, Vector3d.ZERO, material);
        square2(mc, tessellator, renderBuffer, matrixPos, B, C, A, material);
    }

    private static void square2(Minecraft mc, Tessellator tessellator, BufferBuilder bufferbuilder, Matrix4f matrixPos, Vector3d A, Vector3d B, Vector3d C, ResourceLocation material) {
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        mc.getTextureManager().bindTexture(material);
        add2(bufferbuilder, matrixPos, C, 0.0F, 0.0F);
        add2(bufferbuilder, matrixPos, A.add(C), 1.0F, 0.0F);
        add2(bufferbuilder, matrixPos, A.add(B).add(C), 1.0F, 1.0F);
        add2(bufferbuilder, matrixPos, B.add(C), 0.0F, 1.0F);
        tessellator.draw();
    }

    private static void add2(BufferBuilder bufferbuilder, Matrix4f matrixPos, Vector3d A, float u, float v) {
        bufferbuilder.pos(matrixPos, (float) A.x, (float) A.y, (float) A.z)
                .tex(u, v)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .endVertex();
    }

    /**
     * Draws the skybox
     */
    public static void drawSkyBox(ResourceLocation skyBoxMaterial, Quaternion myRotation, MainWindow window, Minecraft minecraft){

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        // sets up perspective
        RenderSystem.matrixMode(GL_PROJECTION);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.ortho(
                0.0D,
                window.getFramebufferWidth() / window.getGuiScaleFactor(),
                window.getFramebufferHeight() / window.getGuiScaleFactor(),
                0.0D,
                1000.0D,
                100000.0D);

        /*RenderSystem.multMatrix(
                Matrix4f.perspective(
                        70.0D,
                        (float) minecraft.getMainWindow().getFramebufferWidth() / (float) minecraft.getMainWindow().getFramebufferHeight(),
                        1000.0F,
                        100000.0F));
        RenderSystem.multMatrix(
                Matrix4f.perspective(
                        85.0D,
                        (float)mc.getMainWindow().getFramebufferWidth() / (float)mc.getMainWindow().getFramebufferHeight(),
                        0.05F,
                        10.0F)
        );*/

        // render system parameters
        RenderSystem.matrixMode(GL_MODELVIEW);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef(0.0F, 0.0F, -3050.0F);
        RenderSystem.scalef((float) E(-12), (float) E(-12), (float) E(-12));
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.0D, 0.0D, 1000 *  E(12));
        matrixStack.scale(75, 75, 75);
        matrixStack.rotate(myRotation);
        matrixStack.translate(-50* AU, -50* AU, -50* AU);

        drawInvertedCube(
                new Vector3d(100 * AU, 0, 0),
                new Vector3d(0, 100 * AU, 0),
                new Vector3d(0, 0, 100 * AU),
                minecraft,
                tessellator,
                matrixStack,
                bufferbuilder,
                skyBoxMaterial
        );


        // restores render system to it's default settings
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.matrixMode(GL_PROJECTION);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(GL_MODELVIEW);
        RenderSystem.popMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }

}
