package io.github.MinecraftSpaceProgram.MSP.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.container.GUIBlockContainer;
import io.github.MinecraftSpaceProgram.MSP.physics.orbital.CelestialBody;
import io.github.MinecraftSpaceProgram.MSP.physics.orbital.OrbitingBody;
import io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil;
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
import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.SolarSystem.SUN;
import static io.github.MinecraftSpaceProgram.MSP.util.RayCasting.rayTestPoints;
import static io.github.MinecraftSpaceProgram.MSP.util.RenderUtils.drawSkyBox;
import static io.github.MinecraftSpaceProgram.MSP.util.RenderUtils.drawTrajectory;
import static java.lang.Math.pow;
import static org.lwjgl.opengl.GL11.*;

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

    private CelestialBody focusedObject = SUN;

    private CelestialBody[] visibleObjects = SUN.satellites.toArray(new CelestialBody[0]);

    private Vector3f origin = new Vector3f(0, 0, 0);

    private String CameraText = "Camera: Sun";

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

    private MatrixStack MATRIX_STACK = new MatrixStack();

    private double right = 0.0D;
    private final double left = 0.0D;
    private final double top = 0.0D;
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
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.matrixMode(GL_MODELVIEW);
        RenderSystem.pushMatrix();

        // draws the planets
        glDepthRange(0.01, 1);
        func_230450_a_(matrixStack, partialTicks, mouseX, mouseY);

        glDepthRange(0, 0.01);
        // field_230706_i_ is minecraft
        assert this.field_230706_i_ != null;

        /*
        RenderSystem.enableBlend();

        square2(
                this.field_230706_i_,
                Tessellator.getInstance(),
                matrixStack.getLast().getMatrix(),
                new Vector3d(0, this.height(), 0),
                new Vector3d(this.width(), 0 ,0 ),
                Vector3d.ZERO,
                new ResourceLocation(MSP.MOD_ID, "textures/gui/background.png")
                );
         */

        //blit
        //this.field_230706_i_.getTextureManager().bindTexture(new ResourceLocation(MSP.MOD_ID, "textures/gui/banner.png"));
        //this.func_238474_b_(matrixStack, this.width() / 2 - 128, this.height() - 16, 0, 0, 256, 16);

        // top left
        //this.field_230706_i_.getTextureManager().bindTexture(new ResourceLocation(MSP.MOD_ID, "textures/gui/topleft.png"));
        //this.func_238474_b_(matrixStack, this.width() - 256, 0, 0, 0, 256, 32);

        // draws the orbit button
        //this.field_230706_i_.getTextureManager().bindTexture(new ResourceLocation(MSP.MOD_ID, "textures/gui/orbit.png"));
        //this.func_238474_b_(matrixStack, this.width() - 16, 0, 0, 0, 16, 16);

        // draws the information button
        //this.field_230706_i_.getTextureManager().bindTexture(new ResourceLocation(MSP.MOD_ID, "textures/gui/information.png"));
        //this.func_238474_b_(matrixStack, this.width() - 16, 17, 0, 0, 16, 16);


        // Banner text
        matrixStack.scale(0.75F, 0.75F, 1.0F);
        //font renderer
        this.field_230712_o_.func_238405_a_(
                matrixStack,
                this.CameraText,
                this.width() / 0.75F - this.field_230712_o_.getStringWidth(this.CameraText) - 16,
                this.height() / 0.75F - 16.0F,
                16777215); // white
        matrixStack.scale(1 / 0.75F, 1/ 0.75F, 1.0F);

        matrixStack.scale(0.5F, 0.5F, 1.0F);
        String s = "INERTIAL VELOCITY             ALTITUDE             APOGEE             PERIGEE             INCLINATION  ";
        this.field_230712_o_.func_238405_a_(
                matrixStack,
                s,
                this.width() / 0.5F - this.field_230712_o_.getStringWidth(s),
                3.0F,
                16777215); // white

        // idk wtf this is
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiContainerEvent.DrawBackground(this, matrixStack, mouseX, mouseY));

        // returns open gl to it's default state
        RenderSystem.popMatrix();
        glDepthRange(0, 1.0);

    }

    @Override
    protected void func_230450_a_(@Nonnull MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        // field_230706_i_ is minecraft
        assert this.field_230706_i_ != null;
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
        drawEntityOnScreen(pow(100 - this.zoom, 3));
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(GL_PROJECTION);
        RenderSystem.popMatrix();
    }

    public void drawEntityOnScreen(double scale) {
        RenderSystem.pushMatrix();

        // Matrix Stack Transformations
        MATRIX_STACK = new MatrixStack();
        MATRIX_STACK.scale((float) E(-1), (float) E(-1), (float) E(-1));
        MATRIX_STACK.translate(0.0D, 0.0D, 1000 * E(1));
        MATRIX_STACK.scale((float) scale, (float) scale, (float) scale);
        MATRIX_STACK.translate(-origin.getX(), -origin.getY(), -origin.getZ());
        MATRIX_STACK.rotate(this.myRotation);

        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

        // field_230706_i_ is minecraft
        assert this.field_230706_i_ != null;

        // skyBox
        drawSkyBox(
                skyBoxMaterial,
                this.myRotation,
                this.field_230706_i_
        );

        // a matrix stack for bodies orbiting the focused object
        MatrixStack matrixStackFocused = new MatrixStack();
        matrixStackFocused.scale((float) E(-1), (float) E(-1), (float) E(-1));
        matrixStackFocused.translate(0.0D, 0.0D, 1000 * E(1));
        matrixStackFocused.scale((float) scale, (float) scale, (float) scale);
        matrixStackFocused.rotate(this.myRotation);

        for (CelestialBody body : visibleObjects) {
            if (body.orbitingAround == focusedObject && body instanceof OrbitingBody) {
                body.draw(matrixStackFocused, irendertypebuffer$impl, this.zoom);
                drawTrajectory(matrixStackFocused.getLast().getMatrix(), irendertypebuffer$impl.getBuffer(RenderType.getLines()), new Color(1.0F,1.0F,1.0F,0.5F), body);
            } else {
                body.draw(MATRIX_STACK, irendertypebuffer$impl, this.zoom);

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

            // modifies the origin
            origin = new Vector3f(focusedObject.position);
            origin.transform(myRotation);
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

    /**
     * Called when the mouse is clicked
     */
    @Override
    public boolean func_231044_a_(double mouseX, double mouseY, int key_pressed) {
        if (key_pressed == 0) {
            Vector3d[] points = new Vector3d[visibleObjects.length + 1];
            double[] radii = new double[visibleObjects.length + 1];

            for (int i = 0; i < visibleObjects.length; i++) {
                if (visibleObjects[i].orbitingAround == focusedObject && visibleObjects[i] instanceof OrbitingBody) {
                    points[i] = focusedObject.position.add(visibleObjects[i].position);
                } else {
                    points[i] = visibleObjects[i].position;
                }
                radii[i] = zoomPlanet(visibleObjects[i].size, this.zoom);
            }

            // always adds the sun
            points[visibleObjects.length] = Vector3d.ZERO;
            radii[visibleObjects.length] = zoomPlanet(SUN.size, this.zoom);

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
                CelestialBody clickedObject = (rayResult.getSecond() < visibleObjects.length)?visibleObjects[rayResult.getSecond()]:SUN;
                MSP.LOGGER.debug("clicked on " + clickedObject.name);

                if (clickedObject != focusedObject) {
                    // changes the focused planet
                    focusedObject = clickedObject;

                    // changes the origin
                    origin = new Vector3f(clickedObject.position);
                    origin.transform(myRotation);

                    // changes the list of visible objects
                    if(focusedObject.orbitingAround != null){
                        visibleObjects = PhysicsUtil.concat(
                                focusedObject.satellites.toArray(new CelestialBody[0]),
                                focusedObject.orbitingAround.satellites.toArray(new CelestialBody[0]));
                    } else {
                        visibleObjects = focusedObject.satellites.toArray(new CelestialBody[0]);
                    }

                    // changes the banner text
                    this.CameraText = "Camera: " + focusedObject.name;

                }
            }
        }
        return false;
    }
}
