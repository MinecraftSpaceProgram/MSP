package io.github.MinecraftSpaceProgram.MSP.physics.orbital;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.MinecraftSpaceProgram.MSP.Main;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.E;
import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.zoomPlanet;
import static io.github.MinecraftSpaceProgram.MSP.util.RenderUtils.drawCube;

public class Sun extends CelestialBody {

    public double size = 1.392684 * E(9);
    Material material2;

    public Sun() {
        super(
                "Sun",
                1.9884 * E(30),
                "planets/default");
        this.position = Vec3d.ZERO;
        this.material2 = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(Main.MOD_ID, "planets/defaulttransparency"));
    }

    @Override
    public void updatePlanet(int time) {
    }

    @Override
    public Vec3d[] predict(int t1, int t2) {
        return new Vec3d[]{Vec3d.ZERO};
    }

    @Override
    public void draw(MatrixStack matrixstack, IRenderTypeBuffer renderBuffers, double zoom) {

        double adjustedSize = zoomPlanet(this.size, zoom);

        // moves the matrix stack to center the sun
        matrixstack.translate(- 2 * adjustedSize / 5, - 2 * adjustedSize / 5, - 2 * adjustedSize / 5);

        // draws the sun
        IVertexBuilder vertexBuilder2 = renderBuffers.getBuffer(RenderType.getSolid());
        drawCube(new Vec3d(4* adjustedSize / 5, 0, 0), new Vec3d(0, 4* adjustedSize / 5, 0), new Vec3d(0, 0, 4* adjustedSize / 5), matrixstack, vertexBuilder2, this.material);

        matrixstack.translate(adjustedSize * (-1/2F + 2/5F), adjustedSize* (-1/2F + 2/5F), adjustedSize* (-1/2F + 2/5F));

        // draws the "crown"
        IVertexBuilder vertexBuilder = renderBuffers.getBuffer(RenderType.getTranslucent());
        drawCube(new Vec3d(adjustedSize, 0, 0), new Vec3d(0, adjustedSize, 0), new Vec3d(0, 0, adjustedSize), matrixstack, vertexBuilder, this.material, 0.5F);

        // reverts the matrix stack to its original position
        matrixstack.translate(adjustedSize / 2, adjustedSize / 2, adjustedSize / 2);
    }
}
