package io.github.MinecraftSpaceProgram.MSP.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.entity.RocketEntity;
import io.github.MinecraftSpaceProgram.MSP.util.BlockStorage;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RocketRenderer extends EntityRenderer<RocketEntity> {

    public RocketRenderer(EntityRendererManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.5F;
    }

    @Override
    public ResourceLocation getEntityTexture(RocketEntity entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }

    @Override
    public void render(RocketEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        BlockStorage storage = entity.getStorage();

        matrixStack.push();
        matrixStack.rotate(Vector3f.YP.rotationDegrees(entity.getYaw(partialTicks)));
        //Render Each block
        BlockRendererDispatcher BLOCK_RENDERER = Minecraft.getInstance().getBlockRendererDispatcher();
        for (int xx = 0; xx < storage.sizeX; xx++) {
            for (int zz = 0; zz < storage.sizeZ; zz++) {
                for (int yy = 0; yy < storage.sizeY; yy++) {
                    BlockState blockState = storage.getBlockState(new BlockPos(xx, yy, zz));
                    if (blockState != null) {
                        try {
                            matrixStack.push();
                            matrixStack.translate(xx - 0.5D, yy, zz - 0.5D);
                            BLOCK_RENDERER.renderBlock(blockState, matrixStack, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
                            matrixStack.pop();
                        } catch (NullPointerException e) {
                            MSP.LOGGER.debug(blockState.getBlock().getRegistryName() + " cannot be rendered on rocket at " + entity.getPositionVec());
                        }
                    }
                }
            }
        }
        matrixStack.pop();
    }

}
