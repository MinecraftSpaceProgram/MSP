package io.github.MinecraftSpaceProgram.MSP.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.container.GUIBlockContainer;
import io.github.MinecraftSpaceProgram.MSP.physics.orbital.CelestialBody;
import io.github.MinecraftSpaceProgram.MSP.physics.orbital.Planet;
import io.github.MinecraftSpaceProgram.MSP.physics.orbital.SolarSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.awt.*;

import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.E;
import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.zoomPlanet;
import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.SolarSystem.SOLAR_SYSTEM;
import static io.github.MinecraftSpaceProgram.MSP.util.RayCasting.rayTestPoints;
import static io.github.MinecraftSpaceProgram.MSP.util.RenderUtils.drawSkyBox;
import static io.github.MinecraftSpaceProgram.MSP.util.RenderUtils.drawTrajectory;
import static java.lang.Math.pow;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;

@SuppressWarnings("deprecation")
@Mod.EventBusSubscriber(modid = MSP.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
/*
 * Since the deobfuscation sucks here is a quick explanation
 *
 * this.field_230706_i_ returns the current Minecraft instance
 *
 * this.field_230708_k_ is the width
 * this.field_230709_l_ is the height
 *
 * func_231045_a_ is moused_dragged
 *
 */
public class GUIBlockScreen extends ContainerScreen<GUIBlockContainer> {

    private Quaternion myRotation = Quaternion.ONE;

    private int zoom = 30;

    private CelestialBody focusedObject = SolarSystem.SUN;

    private Vector3f origin = new Vector3f(0, 0, 0);

    public int width() {
        return this.field_230708_k_;
    }

    public int height() {
        return this.field_230709_l_;
    }

    public boolean hasShiftDown() {
        return func_231173_s_();
    }

    private final ResourceLocation skyBoxMaterial;

    private final MatrixStack RENDER_MATRIX_STACK = new MatrixStack();
    private MatrixStack MATRIX_STACK = new MatrixStack();

    private double right = 0.0D;
    private double left = 0.0D;
    private double top = 0.0D;
    private double bottom = 0.0D;
    private final double NEAR = 1000.0D;
    private final double FAR = 100000.0D;

    public GUIBlockScreen(GUIBlockContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.skyBoxMaterial = new ResourceLocation(MSP.MOD_ID, "textures/skybox/skybox2.png");


    }

    /**
     * Called to do the rendering
     *
     * @param matrixStack  ?
     * @param mouseX       current mouse x position
     * @param partialTicks ?
     */
    @Override
    public void func_230430_a_(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        func_230450_a_(matrixStack, partialTicks, mouseX, mouseY);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiContainerEvent.DrawBackground(this, matrixStack, mouseX, mouseY));
    }

    @Override
    protected void func_230450_a_(@Nonnull MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        MainWindow window = this.field_230706_i_.getMainWindow();

        matrixStack.clear();

        RenderSystem.clear(256, Minecraft.IS_RUNNING_ON_MAC);
        RenderSystem.matrixMode(GL_PROJECTION);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();

        /*RenderSystem.multMatrix(
                Matrix4f.perspective(
                        50.0D,
                        (float) getMinecraft().getMainWindow().getFramebufferWidth() / (float) getMinecraft().getMainWindow().getFramebufferHeight(),
                        1000.0F,
                        100000.0F));*/
        right = window.getFramebufferWidth() / window.getGuiScaleFactor();
        bottom = window.getFramebufferHeight() / window.getGuiScaleFactor();
        RenderSystem.ortho(
                left,
                right,
                bottom,
                top,
                NEAR,
                FAR);

        RenderSystem.matrixMode(GL_MODELVIEW);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.translated(this.width() / 2D, this.height() / 2D, -3050.0D);
        drawEntityOnScreen(this.width() / 2, this.height() / 2, pow(100 - this.zoom, 3));
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(GL_PROJECTION);
        RenderSystem.popMatrix();
    }

    public void drawEntityOnScreen(int posX, int posY, double scale) {
        RenderSystem.pushMatrix();
        MATRIX_STACK = new MatrixStack();
        //MATRIX_STACK.scale((float) E(-12), (float) E(-12), (float) E(-12));
        MATRIX_STACK.scale((float) E(-1), (float) E(-1), (float) E(-1));
        //MATRIX_STACK.translate(0.0D, 0.0D, 1000 * E(12));
        MATRIX_STACK.translate(0.0D, 0.0D, 1000 * E(1));
        MATRIX_STACK.scale((float) scale, (float) scale, (float) scale);
        MATRIX_STACK.translate(-origin.getX(), -origin.getY(), -origin.getZ());
        MATRIX_STACK.rotate(this.myRotation);

        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

        // skyBox
        drawSkyBox(
                skyBoxMaterial,
                this.myRotation,
                this.field_230706_i_.getMainWindow(),
                this.field_230706_i_);

        for (Planet planet : SOLAR_SYSTEM) {
            if (planet.orbitingAround == focusedObject) {
                planet.draw(MATRIX_STACK, irendertypebuffer$impl, this.zoom);
                //if (planet.a * pow(100 - this.zoom, 3) * E(-12) < this.width()) {
                    drawTrajectory(MATRIX_STACK.getLast().getMatrix(), irendertypebuffer$impl.getBuffer(RenderType.getLines()), Color.WHITE, planet);
                //}
            }
        }

        // this needs to be last
        RenderSystem.enableBlend();
        SolarSystem.SUN.draw(MATRIX_STACK, irendertypebuffer$impl, this.zoom);

        irendertypebuffer$impl.finish();
        RenderSystem.popMatrix();
    }

    /**
     * Called when the mouse is dragged
     *
     * @param mouseX      the final x position of the mouse on screen
     * @param key_pressed which mouse key was pressed
     * @param deltaX      the distance along the x axis the mouse was dragged
     * @return always returns false because I'm not quite sure why there even is a return
     */
    @Override
    public boolean func_231045_a_(double mouseX, double mouseY, int key_pressed, double deltaX, double deltaY) {
        // key_pressed is equal to 0 if the left mouse button is pressed
        // 2 is scroll
        if (key_pressed == 2) {
            // if shift is pressed then translates the plain, else rotates it
            if (hasShiftDown()) {
                Vector3f translation = new Vector3f((float) deltaX * 0.1F, (float) deltaY * 0.1F, 0);

                // rotates the vector back to it's original position in space
                Quaternion quaternion = new Quaternion(this.myRotation);
                quaternion.conjugate();
                translation.transform(quaternion);

                //this.ORIGIN.add(translation);

            } else {
                Quaternion revRotation = myRotation.copy();
                revRotation.conjugate();
                origin.transform(revRotation);

                Quaternion xyRotation = new Quaternion(
                        Vector3f.ZP,
                        (float) -deltaX * 0.01F,
                        false);

                Quaternion zRotation = new Quaternion(
                        Vector3f.XP,
                        (float) -deltaY * 0.01F,
                        false);

                zRotation.multiply(this.myRotation);
                zRotation.multiply(xyRotation);

                this.myRotation = zRotation;
                origin.transform(myRotation);
            }
        }
        return false;
    }

    /**
     * Called when the mouse scrolled
     */
    @Override
    public boolean func_231043_a_(double mouseX, double mouseY, double scroll) {
        //scroll is +- 1
        if ((this.zoom > 0 || scroll < 0) && (this.zoom < 100 || scroll > 0)) {
            this.zoom += (int) -scroll;
            MSP.LOGGER.debug("ZOOM: " + zoom);
        }
        return false;
    }

    // TODO: 7/18/2020 this rayCasting implementation is pretty terrible need to transform clicks rather then all of world
    /**
     * Called when the mouse is clicked
     */
    @Override
    public boolean func_231044_a_(double mouseX, double mouseY, int key_pressed) {
        if (key_pressed == 0) {
            Vector3d[] points = new Vector3d[SOLAR_SYSTEM.length];
            double[] radii = new double[SOLAR_SYSTEM.length];

            for (int i = 0; i < SOLAR_SYSTEM.length; i++) {
                points[i] = SOLAR_SYSTEM[i].position;
                radii[i] = zoomPlanet(SOLAR_SYSTEM[i].size, this.zoom);
            }

            // transforms the screen space to world space
            Vector4f start = new Vector4f(
                    (float) ((mouseX - this.width() / 2.0D) / this.width() * (right - left)),
                    (float) ((mouseY - this.height() / 2.0D) / this.height() * (bottom - top)),
                    (float) NEAR,
                    1.0F
            );
            Vector4f end = new Vector4f(
                    (float) ((mouseX - this.width() / 2.0D) / this.width() * (right - left)),
                    (float) ((mouseY - this.height() / 2.0D) / this.height() * (bottom - top)),
                    (float) FAR,
                    1.0F
            );

            Matrix4f transformationMatrix = MATRIX_STACK.getLast().getMatrix().copy();
            transformationMatrix.invert();
            start.transform(transformationMatrix);
            end.transform(transformationMatrix);

            start.perspectiveDivide();
            end.perspectiveDivide();

            Pair<Boolean, Integer> rayResult = rayTestPoints(
                    points,
                    radii,
                    new Vector3d(start.getX(), start.getY(), start.getZ()),
                    new Vector3d(end.getX(), end.getY(), end.getZ())
            );

            if (rayResult.getFirst()) {
                Planet planet = SOLAR_SYSTEM[rayResult.getSecond()];
                MSP.LOGGER.debug("clicked on " + planet.name);
                origin = new Vector3f(planet.position);
                origin.transform(myRotation);
            }
        }
        return false;
    }
}
