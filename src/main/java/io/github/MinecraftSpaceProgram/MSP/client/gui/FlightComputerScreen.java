package io.github.MinecraftSpaceProgram.MSP.client.gui;

import io.github.MinecraftSpaceProgram.MSP.Main;
import io.github.MinecraftSpaceProgram.MSP.container.FlightComputerContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlightComputerScreen extends ContainerScreen<FlightComputerContainer> {

    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Main.MOD_ID, "textures/gui/flight_computer.png");

    public FlightComputerScreen(FlightComputerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.guiLeft = 0;
        this.guiTop = 0;
        this.xSize = 50;
        this.ySize = 50;
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        this.renderBackground();
        super.render(mouseX,mouseY,partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        this.font.drawString(this.title.getFormattedText(), 0.0F, 0.0F, 4210752);
        this.font.drawString(this.playerInventory.getName().getFormattedText(), 0.0F, 90.0F, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        this.blit(x, y, 0, 0 , this.xSize, this.ySize);
    }
}
