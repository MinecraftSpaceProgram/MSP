package io.github.MinecraftSpaceProgram.MSP.physics.orbital;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.MinecraftSpaceProgram.MSP.MSP;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("deprecation")
public abstract class CelestialBody {

    /**
     * The display name of the celestial body
     */
    public String name;

    /**
     * The mass of the celestial body in kg
     */
    public double mass;

    // the position of the celestial body in a cartesian space, unit AU
    @Deprecated
    public Vector3d position;

    /**
     * The trajectory of the celestial body
     */
    public Vector3d[] trajectory;

    /**
     * The velocity of the celestial body
     */
    public Vector3d[] velocity;

    @Deprecated
    public RenderMaterial material;

    public ResourceLocation texture;

    /**
     * Display size of the body
     */
    public double size;

    /**
     * Satellites of this body
     */
    public List<CelestialBody> satellites = new ArrayList<>();

    /**
     * The body around which is being orbited
     */
    @Nullable
    @Deprecated
    public CelestialBody orbitingAround;

    public CelestialBody(String name, double mass, String path){
        this.name = name;
        this.mass = mass;
        this.texture = new ResourceLocation(MSP.MOD_ID, path);
        this.material = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, this.texture);

    }

    public abstract Vector3d[] predict(int t1, int t2);

    public abstract Vector3d calculatePosition(int time);

    public abstract void update(int time);

    /**
     * Draws the celestial body on the screen
     */
    public abstract void draw(MatrixStack matrixStack, IRenderTypeBuffer renderBuffers, double zoom);

    @Override
    public String toString() {
        return "CelestialBody{" +
                "name='" + name + '\'' +
                ", mass=" + mass +
                '}';
    }
}
