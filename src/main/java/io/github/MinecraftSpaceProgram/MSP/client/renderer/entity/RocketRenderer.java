package io.github.MinecraftSpaceProgram.MSP.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.entity.RocketEntity;
import io.github.MinecraftSpaceProgram.MSP.util.BlockStorage;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RocketRenderer extends EntityRenderer<RocketEntity> implements IEntityRenderer<RocketEntity, EntityModel<RocketEntity>> {
    protected EntityModel<RocketEntity> model = new EntityModel<RocketEntity>() {
        public void setRotationAngles(RocketEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) { }
        public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { }
    };
    private static final BlockRendererDispatcher BLOCK_RENDERER = Minecraft.getInstance().getBlockRendererDispatcher();

    public RocketRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    @Nonnull
    public EntityModel<RocketEntity> getEntityModel() {
        return model;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public ResourceLocation getEntityTexture(RocketEntity entity) {
        return null;
    }

    @Override
    public void render(RocketEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        BlockStorage storage = entity.getStorage();
        if (storage == null)
            return;

        RenderHelper.disableStandardItemLighting();
        Minecraft.getInstance().getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);

        for (int dx = 0; dx < storage.sizeX; ++dx) {
            for (int dy = 0; dy < storage.sizeY; ++dy) {
                for (int dz = 0; dz < storage.sizeZ; ++dz) {
                    BlockPos pos = new BlockPos(dx,dy,dz);
                    BlockState blockState = storage.getBlockState(pos);
                    if (blockState != null) {
                        try {
                            BLOCK_RENDERER.renderBlock(blockState, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
                        }
                        catch (NullPointerException e) {
                            MSP.LOGGER.error(blockState.getBlock().getRegistryName() + String.format(" cannot be rendered at (%f,%f,%f)", entity.getPosX(), entity.getPosY(), entity.getPosZ()));
                        }
                    }
                }
            }
        }

        for (TileEntity tile : storage.getTileEntityList()) {
            TileEntityRenderer<TileEntity> renderer = TileEntityRendererDispatcher.instance.getRenderer(tile);
            if (renderer != null)
                TileEntityRendererDispatcher.instance.renderTileEntity(tile, partialTicks, matrixStackIn, bufferIn);
        }

        matrixStackIn.translate(entity.getPosX() - storage.sizeX / 2f, entity.getPosY() - storage.sizeY / 2f, entity.getPosZ() - storage.sizeZ / 2f);
        RenderHelper.enableStandardItemLighting();
    }
}
