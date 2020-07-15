package io.github.MinecraftSpaceProgram.MSP.physics.orbital;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.MinecraftSpaceProgram.MSP.Main;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public abstract class CelestialBody {

    public String name;

    public double mass;

    public Vec3d position;

    public Vec3d speed;

    public Vec3d acceleration;

    public Vec3d[] trajectory;

    public Material material;

    public CelestialBody(String name, double mass, String path){
        this.name = name;
        this.mass = mass;
        this.material = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(Main.MOD_ID, path));

    }

    public abstract Vec3d[] predict(int t1, int t2);

    public abstract void updatePlanet(int time);

    /**
     * Draws the celestial body on the screen
     * @param matrixstack the current transformation matrix
     * @param renderBuffers the vertex builder used to draw the line
     */
    public abstract void draw(MatrixStack matrixstack, IRenderTypeBuffer renderBuffers, double zoom);

    @Override
    public String toString() {
        return "CelestialBody{" +
                "name='" + name + '\'' +
                ", mass=" + mass +
                ", position=" + position +
                ", speed=" + speed +
                ", acceleration=" + acceleration +
                '}';
    }
}
