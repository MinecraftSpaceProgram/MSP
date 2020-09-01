package io.github.MinecraftSpaceProgram.MSP.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.entity.RocketEntity;
import io.github.MinecraftSpaceProgram.MSP.util.BlockStorage;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

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
        World world = entity.getWorldObj();
        for (int xx = 0; xx < storage.sizeX; xx++) {
            for (int zz = 0; zz < storage.sizeZ; zz++) {
                for (int yy = 0; yy < storage.sizeY; yy++) {
                    BlockState blockState = storage.getBlockState(new BlockPos(xx, yy, zz));
                    if (blockState != null) {
                        try {
                            FallingBlockEntity entityIn = new FallingBlockEntity(
                                    world,
                                    entity.getPosX(),
                                    entity.getPosY(),
                                    entity.getPosZ(),
                                    blockState
                            );

                            BlockState blockstate = entityIn.getBlockState();
                            if (blockstate.getRenderType() == BlockRenderType.MODEL) {
                                if (blockstate != world.getBlockState(entityIn.func_233580_cy_()) && blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
                                    matrixStack.push();
                                    matrixStack.translate(xx, yy, zz);
                                    Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(blockState, matrixStack, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
                                    matrixStack.pop();
                                }
                            }
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
