package io.github.MinecraftSpaceProgram.MSP.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.MinecraftSpaceProgram.MSP.entity.RocketEntity;
import io.github.MinecraftSpaceProgram.MSP.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;

@SuppressWarnings({"deprecation"})
public class RocketGui extends IngameGui {
  public RocketGui(Minecraft mcIn) {
    super(mcIn);
  }

  @SubscribeEvent
  public void onPreRenderGui(RenderGameOverlayEvent.Pre event) {
    PlayerEntity player =
        this.mc.getRenderViewEntity() instanceof PlayerEntity
            ? (PlayerEntity) this.mc.getRenderViewEntity()
            : null;
    if (player == null) return;
    if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
      int width = this.mc.getMainWindow().getScaledWidth();
      int height = this.mc.getMainWindow().getScaledHeight();

      Entity rocket = player.getRidingEntity();
      if (rocket instanceof RocketEntity) {
        if (!(this.mc.currentScreen instanceof ContainerScreen)) {
          this.RenderInventory(width, height, (RocketEntity) rocket);
        }
      }
    }
  }

  private void RenderInventory(int width, int height, RocketEntity rocket) {
    int WHITE = 16777215;
    RenderSystem.pushMatrix();
    MatrixStack matrixStack = new MatrixStack();
    matrixStack.translate(width / 2.0F, 5.0F, -255.0D);

    // Displays the current altitude of the rocket
    matrixStack.push();
    matrixStack.scale(2.0F, 2.0F, 2.0F);
    drawString(
        matrixStack,
        this.getFontRenderer(),
        I18n.format((int) rocket.trueAltitude + "m"),
        -this.getFontRenderer().getStringWidth((int) rocket.trueAltitude + "m") / 2,
        0,
        WHITE);
    matrixStack.pop();

    // Displays the current speed at which the rocket is traveling
    matrixStack.push();
    matrixStack.translate(0, 20, 0);
    drawString(
        matrixStack,
        this.getFontRenderer(),
        I18n.format((int) rocket.trueSpeed + "m/s"),
        -this.getFontRenderer().getStringWidth((int) rocket.trueSpeed + "m/s") / 2,
        0,
        WHITE);
    matrixStack.pop();

    // draws the remaining fuel
    matrixStack.push();
    matrixStack.translate(width / 4.0F, height * 0.75F, 0);
    drawString(
        matrixStack,
        this.getFontRenderer(),
        I18n.format((int) rocket.getFuel() + "kg"),
        -this.getFontRenderer().getStringWidth((int) rocket.getFuel() + "kg") / 2,
        0,
        WHITE);
    matrixStack.translate(0, 20, 0);

    RenderUtils.drawProgressBar(
        mc,
        matrixStack.getLast().getMatrix(),
        rocket.getFuel() / rocket.getFuelCapacity(),
        Color.WHITE,
        Color.decode("#8b8b8b"),
        Color.decode("#c6c6c6"),
        80,
        15);
    RenderSystem.popMatrix();
  }
}
