package io.github.MinecraftSpaceProgram.MSP.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.awt.*;

import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.E;
import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.SolarSystem.SOLAR_SYSTEM;
import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.SolarSystem.SUN;
import static io.github.MinecraftSpaceProgram.MSP.util.RenderUtils.drawSkyBox;
import static io.github.MinecraftSpaceProgram.MSP.util.RenderUtils.drawTrajectory;
import static java.lang.Math.pow;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;

@SuppressWarnings("deprecation")
@Mod.EventBusSubscriber(modid=MSP.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
/**
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

    public int width() {
        return this.field_230708_k_;
    }

    public int height() {
        return this.field_230709_l_;
    }

    public boolean hasShiftDown(){
        return this.func_231173_s_();
    }

    private ResourceLocation skyBoxMaterial;

    public GUIBlockScreen(GUIBlockContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.skyBoxMaterial = new ResourceLocation(MSP.MOD_ID, "textures/skybox/skybox2.png");
    }

    @Override
    protected void func_230450_a_(@Nonnull MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        MSP.LOGGER.debug("height: " + height());
        MSP.LOGGER.debug("width: " + width());

        MainWindow window = this.field_230706_i_.getMainWindow();

        matrixStack.clear();

        RenderSystem.clear(256, Minecraft.IS_RUNNING_ON_MAC);
        RenderSystem.matrixMode(GL_PROJECTION);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(
                0.0D,
                window.getFramebufferWidth() / window.getGuiScaleFactor(),
                window.getFramebufferHeight() / window.getGuiScaleFactor(),
                0.0D,
                1000.0D,
                100000.0D);
        RenderSystem.matrixMode(GL_MODELVIEW);
        RenderSystem.loadIdentity();
        RenderSystem.translatef(0.0F, 0.0F, -3050.0F);

        drawEntityOnScreen(this.width() / 2, this.height()/ 2, pow(100 - this.zoom, 3 ));
    }

    public void drawEntityOnScreen(int posX, int posY, double scale) {

        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)posX, (float)posY, 0.0F);
        RenderSystem.scalef((float) E(-12), (float) E(-12), (float) E(-12));

        MatrixStack matrixstack = new MatrixStack();
        matrixstack.translate(0.0D, 0.0D, 1000 *  E(12));
        matrixstack.scale((float)scale, (float)scale, (float)scale);

        matrixstack.translate(this.ORIGIN.getX(), this.ORIGIN.getY(), this.ORIGIN.getZ());

        matrixstack.rotate(this.myRotation);

        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

        // skyBox
        drawSkyBox(
                skyBoxMaterial,
                this.myRotation,
                this.field_230706_i_.getMainWindow(),
                this.field_230706_i_);



        // draws the planets
        for (Planet planet:
             SOLAR_SYSTEM) {
            if(planet.orbitingAround == focusedObject || planet.orbitingAround == SUN){
                planet.draw(matrixstack, irendertypebuffer$impl, this.zoom);
                if (planet.a * pow(100 - this.zoom, 3 ) * E(-12) < this.width()) {
                    drawTrajectory(matrixstack.getLast().getMatrix(), irendertypebuffer$impl.getBuffer(RenderType.getLines()), Color.WHITE, planet);
                }
            }
        }

        // this needs to be last
        RenderSystem.enableBlend();
        SolarSystem.SUN.draw(matrixstack, irendertypebuffer$impl, this.zoom);

        irendertypebuffer$impl.finish();

        RenderSystem.popMatrix();
    }

    private Quaternion myRotation = Quaternion.ONE;

    private int zoom = 30;

    private CelestialBody focusedObject;

    private final Vector3f ORIGIN = new Vector3f(0,0,0);

    @Override
    public boolean func_231045_a_(double mouseX, double mouseY, int key_pressed, double deltaX, double deltaY){

        // key_pressed is equal to 0 if the left mouse button is pressed
        if(key_pressed == 0){
            // if shift is pressed then translates the plain, else rotates it
            if(hasShiftDown()){
                Vector3f translation = new Vector3f((float) deltaX * 0.1F,  (float) deltaY * 0.1F, 0);

                // rotates the vector back to it's original position in space
                Quaternion quaternion = new Quaternion(this.myRotation);
                quaternion.conjugate();
                translation.transform(quaternion);

                this.ORIGIN.add(translation);

            } else {
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
            }
        }
        return false;
    }

    /**
     * mouse scrolled
     */
    @Override
    public boolean func_231043_a_(double mouseX, double mouseY, double scroll){
        //scroll is +- 1
        if( (this.zoom > 0 || scroll < 0) && (this.zoom < 100 || scroll > 0) ){
            this.zoom += (int) - scroll;
            MSP.LOGGER.debug("ZOOM: " + zoom);
        }
        return false;
    }


}
