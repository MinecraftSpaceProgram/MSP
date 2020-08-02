package io.github.MinecraftSpaceProgram.MSP.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.container.LaunchpadControllerContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = MSP.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
/*
 * this.field_230706_i_ minecraft
 * this.field_230708_k_ width
 * this.field_230709_l_ height
 *
 * func_231045_a_ moused_dragged
 * func_231173_s_ hasShiftDown
 */
public class LaunchpadControllerScreen extends ContainerScreen<LaunchpadControllerContainer> {
    private final ResourceLocation GUI = new ResourceLocation(MSP.MOD_ID, "textures/gui/launchpad_controller.png");

    public LaunchpadControllerScreen(LaunchpadControllerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void func_230450_a_(@Nonnull MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        //noinspection deprecation
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.field_230706_i_ != null)
            this.field_230706_i_.getTextureManager().bindTexture(GUI);
        this.func_238474_b_(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }


}
