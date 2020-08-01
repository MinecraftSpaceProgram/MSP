package io.github.MinecraftSpaceProgram.MSP.physics.orbital;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.MinecraftSpaceProgram.MSP.MSP;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("deprecation")
public abstract class CelestialBody {

    public String name;

    // the mas of the celestial body, unit kg
    public double mass;

    // the position of the celestial body in a=cartesian space, unit AU
    public Vector3d position;

    public Vector3d speed;

    public Vector3d acceleration;

    public Vector3d[] trajectory;

    public RenderMaterial material;

    /**
     * display size of a planet
     */
    public double size;

    /**
     * list of satellites of this
     */
    public List<CelestialBody> satellites = new ArrayList<>();

    /**
     * The body around which is being orbited
     */
    public CelestialBody orbitingAround;

    public CelestialBody(String name, double mass, String path){
        this.name = name;
        this.mass = mass;
        this.material = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(MSP.MOD_ID, path));

    }

    public abstract Vector3d[] predict(int t1, int t2);

    public abstract Vector3d calculatePosition(int time);

    public abstract void updatePlanet(int time);

    /**
     * Draws the celestial body on the screen
     * @param matrixStack the current transformation matrix
     * @param renderBuffers the vertex builder used to draw the line
     */
    public abstract void draw(MatrixStack matrixStack, IRenderTypeBuffer renderBuffers, double zoom);

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
