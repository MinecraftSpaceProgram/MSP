package io.github.MinecraftSpaceProgram.MSP.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.container.GUIBlockContainer;
import io.github.MinecraftSpaceProgram.MSP.physics.orbital.*;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.awt.*;

import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.E;
import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.zoomPlanet;
import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.SolarSystem.HEART_OF_GOLD;
import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.SolarSystem.SUN;
import static io.github.MinecraftSpaceProgram.MSP.util.RayCasting.rayTestPoints;
import static io.github.MinecraftSpaceProgram.MSP.util.RenderUtils.drawSkyBox;
import static io.github.MinecraftSpaceProgram.MSP.util.RenderUtils.drawTrajectory;
import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.*;

@SuppressWarnings("deprecation")
@Mod.EventBusSubscriber(modid = MSP.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
/*
 * Since the deobfuscation sucks here is a quick explanation
 *
 * this.field_230706_i_ returns the current Minecraft instance
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

    private final ResourceLocation skyBoxMaterial;

    private MatrixStack MATRIX_STACK = new MatrixStack();

    private double right = 0.0D;
    private final double left = 0.0D;
    private final double top = 0.0D;
    private double bottom = 0.0D;
    private final double NEAR = 1000.0D;
    private final double FAR = 100000.0D;

    private Button deleteNode;
    private TextFieldWidget progradeField;
    private TextFieldWidget radialField;
    private TextFieldWidget normalField;

    private double progradeSpeed = 0.0D;
    private double radialSpeed = 0.0D;
    private double normalSpeed = 0.0D;


    public GUIBlockScreen(GUIBlockContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.skyBoxMaterial = new ResourceLocation(MSP.MOD_ID, "textures/skybox/skybox2.png");
    }

    /**
     * Called at init
     */
    @Override
    public void init() {
        super.init();

        this.deleteNode = this.addButton(new Button(5, this.ySize - 5, 75, 20, new TranslationTextComponent("button.delete_node"), (p) -> {
            p.queueNarration(250);
            MSP.LOGGER.debug("Clicked button");
            HEART_OF_GOLD.orbit.node = null;
        }));

        this.progradeField = createTextFieldWidget(10, 35, 75, 12, "field.prograde");
        progradeField.setResponder(s -> {
            if (s.matches("(-|[+])?\\d+([.]\\d+)?")) {
                this.progradeSpeed = Double.parseDouble(s);
            }
            if (HEART_OF_GOLD.orbit.node != null) {
                HEART_OF_GOLD.orbit.node.setDeltaV(
                        HEART_OF_GOLD.orbit.node.PROGRADE.scale(this.progradeSpeed)
                                .add(HEART_OF_GOLD.orbit.node.NORMAL.scale(this.normalSpeed))
                                .add(HEART_OF_GOLD.orbit.node.RADIAL.scale(this.radialSpeed)));
            }
        });

        this.radialField = createTextFieldWidget(10, 72, 75, 12, "field.radial");
        radialField.setResponder(s -> {
            if (s.matches("(-|[+])?\\d+([.]\\d+)?")) {
                this.radialSpeed = Double.parseDouble(s);
            }
            if (HEART_OF_GOLD.orbit.node != null) {
                HEART_OF_GOLD.orbit.node.setDeltaV(
                        HEART_OF_GOLD.orbit.node.PROGRADE.scale(this.progradeSpeed)
                                .add(HEART_OF_GOLD.orbit.node.NORMAL.scale(this.normalSpeed))
                                .add(HEART_OF_GOLD.orbit.node.RADIAL.scale(this.radialSpeed)));
            }
        });

        this.normalField = createTextFieldWidget(10, 109, 75, 12, "field.normal");
        normalField.setResponder(s -> {
            if (s.matches("(-|[+])?\\d+([.]\\d+)?")) {
                this.normalSpeed = Double.parseDouble(s);
            }
            if (HEART_OF_GOLD.orbit.node != null) {
                HEART_OF_GOLD.orbit.node.setDeltaV(
                        HEART_OF_GOLD.orbit.node.PROGRADE.scale(this.progradeSpeed)
                                .add(HEART_OF_GOLD.orbit.node.NORMAL.scale(this.normalSpeed))
                                .add(HEART_OF_GOLD.orbit.node.RADIAL.scale(this.radialSpeed)));
            }
        });

        /*this.nameField = new TextFieldWidget(this.font(), 62, 24, 103, 12, new TranslationTextComponent("Test"));
        // change focus
        this.nameField.func_231049_c__(true);
        this.nameField.setMaxStringLength(35);
        this.nameField.setTextColor(-1);
        this.nameField.setDisabledTextColour(1000);
        this.nameField.setResponder(MSP.LOGGER::debug);
        this.nameField.setEnableBackgroundDrawing(true);
        this.nameField.setCanLoseFocus(true);

        // children
        this.field_230705_e_.add(this.nameField);


        this.nameField.setEnabled(true);
        this.nameField.setText("0");

         */
    }

    /**
     * Called to do the rendering
     *
     * @param matrixStack  ?
     * @param mouseX       current mouse x position
     * @param partialTicks ?
     */
    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.matrixMode(GL_MODELVIEW);
        RenderSystem.pushMatrix();

        // draws the planets
        glDepthRange(0.01, 1);
        func_230450_a_(matrixStack, partialTicks, mouseX, mouseY);

        glDepthRange(0, 0.01);
        assert this.minecraft != null;

        /*
        RenderSystem.enableBlend();

        square2(
                this.field_230706_i_,
                Tessellator.getInstance(),
                matrixStack.getLast().getMatrix(),
                new Vector3d(0, this.height(), 0),
                new Vector3d(this.width, 0 ,0 ),
                Vector3d.ZERO,
                new ResourceLocation(MSP.MOD_ID, "textures/gui/background.png")
                );
         */

        //blit
        //this.field_230706_i_.getTextureManager().bindTexture(new ResourceLocation(MSP.MOD_ID, "textures/gui/banner.png"));
        //this.func_238474_b_(matrixStack, this.width / 2 - 128, this.height() - 16, 0, 0, 256, 16);

        // top left
        //this.field_230706_i_.getTextureManager().bindTexture(new ResourceLocation(MSP.MOD_ID, "textures/gui/topleft.png"));
        //this.func_238474_b_(matrixStack, this.width - 256, 0, 0, 0, 256, 32);

        // draws the orbit button
        //this.field_230706_i_.getTextureManager().bindTexture(new ResourceLocation(MSP.MOD_ID, "textures/gui/orbit.png"));
        //this.func_238474_b_(matrixStack, this.width - 16, 0, 0, 0, 16, 16);

        // draws the information button
        //this.field_230706_i_.getTextureManager().bindTexture(new ResourceLocation(MSP.MOD_ID, "textures/gui/information.png"));
        //this.func_238474_b_(matrixStack, this.width - 16, 17, 0, 0, 16, 16);


        // Banner text
        matrixStack.scale(0.75F, 0.75F, 1.0F);
        //font renderer
        this.font.drawString(
                matrixStack,
                this.CameraText,
                this.width / 0.75F - this.font.getStringWidth(this.CameraText) - 16,
                this.height / 0.75F - 16.0F,
                16777215); // white
        matrixStack.scale(1 / 0.75F, 1 / 0.75F, 1.0F);

        matrixStack.translate(this.width, 0, 0);
        matrixStack.scale(0.5F, 0.5F, 1.0F);
        matrixStack.translate(50, 0,0);


        Orbit orbit = HEART_OF_GOLD.orbit;
        int WHITE = 16777215;
        int orbitColor = orbit.color.getRGB();

        drawString(matrixStack, this.font, I18n.format("map.inclination"), -50, 20, WHITE);
        drawString(matrixStack, this.font, I18n.format(String.format("%.3E", orbit.i * 180 / PI) + " deg"), -50, 40, orbitColor);

        drawString(matrixStack, this.font, I18n.format("map.perigee"), -150, 20, WHITE);
        drawString(matrixStack, this.font, I18n.format(String.format("%.3E", orbit.p / (1 + orbit.e)) + " m"), -150, 40, orbitColor);

        drawString(matrixStack, this.font, I18n.format("map.apogee"), -250, 20, WHITE);
        drawString(matrixStack, this.font, I18n.format(String.format("%.3E", orbit.p / (1 - orbit.e)) + " m"), -250, 40, orbitColor);

        drawString(matrixStack, this.font, I18n.format("map.altitude"), -350, 20, WHITE);
        drawString(matrixStack, this.font, I18n.format(
                String.format("%.3E", orbit.hovering
                        ? orbit.r(orbit.hoverAngle)
                        : 0.0D) + " m"
        ), -350, 40, orbitColor);

        drawString(matrixStack, this.font, I18n.format("map.velocity"), -450, 20, WHITE);
        drawString(matrixStack, this.font, I18n.format(
                String.format("%.3E", orbit.hovering
                        ? orbit.speed(orbit.hoverAngle).length()
                        : 0.0D) + " m/s"
        ), -450, 40, orbitColor);

        if (orbit.node != null){
            Orbit orbit1 = orbit.node.theoreticalTrajectory;
            int nodeColor = orbit1.color.getRGB();

            drawString(matrixStack, this.font, I18n.format(String.format("%.3E", orbit1.i * 180 / PI) + " deg"), -50, 60, nodeColor);

            drawString(matrixStack, this.font, I18n.format(String.format("%.3E", orbit1.p / (1 + orbit1.e)) + " m"), -150, 60, nodeColor);

            drawString(matrixStack, this.font, I18n.format(String.format("%.3E", orbit1.p / (1 - orbit1.e)) + " m"), -250, 60, nodeColor);

            drawString(matrixStack, this.font, I18n.format(
                    String.format("%.3E", orbit1.hovering
                            ? orbit1.r(orbit1.hoverAngle)
                            : orbit.r(orbit.node.angle)) + " m"
            ), -350, 60, nodeColor);

            drawString(matrixStack, this.font, I18n.format(
                    String.format("%.3E", orbit1.hovering
                            ? orbit1.speed(orbit1.hoverAngle).length()
                            : orbit.speed(orbit.node.angle).length()) + " m/s"
            ), -450, 60, nodeColor);

        }



        /*String s = "INERTIAL VELOCITY             ALTITUDE             APOGEE             PERIGEE             INCLINATION  ";
        this.font().func_238405_a_(
                matrixStack,
                s,
                this.width / 0.5F - this.font().getStringWidth(s),
                3.0F,
                16777215); // white
         */
        matrixStack.translate(-50, 0, 0);
        matrixStack.scale(2.0F, 2.0F, 1.0F);
        matrixStack.translate(-this.width, 0, 0);


        // called to do the rendering
        int GREY = -6250336;
        drawString(matrixStack, this.font, I18n.format("field.prograde"), 10, 25, GREY);
        drawString(matrixStack, this.font, I18n.format("field.unit"), 88, 37, GREY);
        this.progradeField.render(matrixStack, mouseX, mouseY, partialTicks);

        drawString(matrixStack, this.font, I18n.format("field.radial"), 10, 62, GREY);
        drawString(matrixStack, this.font, I18n.format("field.unit"), 88, 74, GREY);
        this.radialField.render(matrixStack, mouseX, mouseY, partialTicks);

        drawString(matrixStack, this.font, I18n.format("field.normal"), 10, 99, GREY);
        drawString(matrixStack, this.font, I18n.format("field.unit"), 88, 111, GREY);
        this.normalField.render(matrixStack, mouseX, mouseY, partialTicks);

        // draws the buttons
        for (Widget widget : this.buttons) {
            widget.render(matrixStack, mouseX, mouseY, partialTicks);
        }

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
        assert this.minecraft != null;
        MainWindow window = this.minecraft.getMainWindow();

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
        RenderSystem.translated(this.width / 2D, this.height / 2D, -3050.0D);
        drawEntityOnScreen(pow(100 - this.zoom, 3), mouseX, mouseY);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(GL_PROJECTION);
        RenderSystem.popMatrix();

        // better light https://community.khronos.org/t/light-sources-do-not-make-textures-brighter-than-texture-source-images/73500/2
        //GL11.glTexEnvf(GL_TEXTURE_ENV, );
    }

    public void drawEntityOnScreen(double scale, int mouseX, int mouseY) {
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
        assert this.minecraft != null;

        // skyBox
        drawSkyBox(
                skyBoxMaterial,
                this.myRotation,
                this.minecraft
        );

        // a matrix stack for bodies orbiting the focused object
        MatrixStack matrixStackFocused = new MatrixStack();
        matrixStackFocused.scale((float) E(-1), (float) E(-1), (float) E(-1));
        matrixStackFocused.translate(0.0D, 0.0D, 1000 * E(1));
        matrixStackFocused.scale((float) scale, (float) scale, (float) scale);
        matrixStackFocused.rotate(this.myRotation);

        for (CelestialBody body : visibleObjects) {
            if (body.orbitingAround == focusedObject && body instanceof OrbitingBody || body instanceof ArtificialSatellite) {
                if (body instanceof ArtificialSatellite) {
                    ((ArtificialSatellite) body).draw2(this.minecraft, matrixStackFocused);
                    ((ArtificialSatellite) body).orbit.draw(matrixStackFocused, irendertypebuffer$impl, this.minecraft);
                } else {
                    drawTrajectory(matrixStackFocused.getLast().getMatrix(), irendertypebuffer$impl.getBuffer(RenderType.getLines()), new Color(1.0F, 1.0F, 1.0F, 0.5F), body);
                    body.draw(matrixStackFocused, irendertypebuffer$impl, this.zoom);
                }
            } else {
                body.draw(MATRIX_STACK, irendertypebuffer$impl, this.zoom);
            }
        }

        // transforms the screen space to world space
        Vector4f start = new Vector4f(
                (float) ((mouseX - this.width / 2.0D) / this.width * (right - left)),
                (float) ((mouseY - this.height / 2.0D) / this.height * (bottom - top)),
                (float) NEAR,
                1.0F
        );
        Vector4f end = new Vector4f(
                (float) ((mouseX - this.width / 2.0D) / this.width * (right - left)),
                (float) ((mouseY - this.height / 2.0D) / this.height * (bottom - top)),
                (float) FAR,
                1.0F
        );

        Matrix4f transformationMatrix = MATRIX_STACK.getLast().getMatrix().copy();
        transformationMatrix.invert();
        start.transform(transformationMatrix);
        end.transform(transformationMatrix);

        start.perspectiveDivide();
        end.perspectiveDivide();

        HEART_OF_GOLD.orbit.hover(
                new Vector3d(start.getX(), start.getY(), start.getZ()),
                new Vector3d(end.getX(), end.getY(), end.getZ()),
                MATRIX_STACK,
                this.minecraft);

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
    public boolean mouseDragged(double mouseX, double mouseY, int key_pressed, double deltaX, double deltaY) {
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
        return super.mouseDragged(mouseX, mouseY, key_pressed, deltaX, deltaY);
    }

    /**
     * Called when the mouse scrolled
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        //scroll is +- 1
        if ((this.zoom > 0 || scroll < 0) && (this.zoom < 100 || scroll > 0)) {
            this.zoom += (int) -scroll;
        }
        return false;
    }

    /**
     * Called when the mouse is clicked
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int key_pressed) {
        if (key_pressed == 0) {
            // Creates a maneuver node
            if (HEART_OF_GOLD.orbit.hovering && HEART_OF_GOLD.orbit.node == null) {
                // the eccentricity
                double e = HEART_OF_GOLD.orbit.e;

                // the true anomaly
                double v = HEART_OF_GOLD.orbit.hoverAngle;
                v = v >= 0 ? v : 2 * PI + v;

                // the eccentric anomaly
                double E = 2.0D * atan(tan(v / 2.0D) * sqrt((1 - e) / (1 + e)));

                // the mean anomaly
                double M = E - e * sin(E);

                // the time of passage at the maneuver node
                double t = HEART_OF_GOLD.orbit.T + M * HEART_OF_GOLD.orbit.period() / (2 * PI);

                Color color = HEART_OF_GOLD.orbit.color;

                HEART_OF_GOLD.orbit.node =
                        new ManeuverNode(
                                (long) t,
                                v,
                                HEART_OF_GOLD.orbit,
                                new Vector3d(0, 0, 0),
                                HEART_OF_GOLD.orbit,
                                new Color(
                                        255 - color.getRed(),
                                        255 - color.getGreen(),
                                        255 - color.getBlue(),
                                        color.getAlpha())
                        );
            } else {
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
                        (float) ((mouseX - this.width / 2.0D) / this.width * (right - left)),
                        (float) ((mouseY - this.height / 2.0D) / this.height * (bottom - top)),
                        (float) NEAR,
                        1.0F
                );
                Vector4f end = new Vector4f(
                        (float) ((mouseX - this.width / 2.0D) / this.width * (right - left)),
                        (float) ((mouseY - this.height / 2.0D) / this.height * (bottom - top)),
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
                    CelestialBody clickedObject = (rayResult.getSecond() < visibleObjects.length) ? visibleObjects[rayResult.getSecond()] : SUN;
                    MSP.LOGGER.debug("clicked on " + clickedObject.name);

                    if (clickedObject != focusedObject) {
                        // changes the focused planet
                        focusedObject = clickedObject;

                        // changes the origin
                        origin = new Vector3f(clickedObject.position);
                        origin.transform(myRotation);

                        // changes the list of visible objects
                        if (focusedObject.orbitingAround != null) {
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
        }
        return super.mouseClicked(mouseX, mouseY, key_pressed);
    }

    /**
     * Called when a key is pressed
     */
    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == 256) {
            assert this.minecraft != null;
            assert this.minecraft.player != null;
            this.minecraft.player.closeScreen();
        }

        //return !this.nameField.func_231046_a_(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) && !this.nameField.canWrite() ? super.func_231046_a_(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) : true;
        return this.progradeField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || this.progradeField.canWrite()
                || this.normalField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || this.normalField.canWrite()
                || this.radialField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || this.radialField.canWrite()
                || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    private TextFieldWidget createTextFieldWidget(int posX, int posY, int sizeX, int sizeY, String title) {
        TextFieldWidget textFieldWidget = new TextFieldWidget(this.font, posX, posY, sizeX, sizeY, new TranslationTextComponent(title));
        // change focus
        textFieldWidget.setFocused2(false);
        textFieldWidget.setEnabled(true);
        textFieldWidget.setMaxStringLength(10);
        textFieldWidget.setTextColor(-1);
        textFieldWidget.setDisabledTextColour(1000);
        textFieldWidget.setEnableBackgroundDrawing(true);
        textFieldWidget.setCanLoseFocus(true);
        this.children.add(textFieldWidget);
        textFieldWidget.setText("0");
        return textFieldWidget;
    }
}
