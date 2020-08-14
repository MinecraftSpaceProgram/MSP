package io.github.MinecraftSpaceProgram.MSP.physics.orbital;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;

import java.util.List;

import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.*;
import static io.github.MinecraftSpaceProgram.MSP.util.RenderUtils.drawLogo;

public class ArtificialSatellite extends CelestialBody
{
    /**
     * The current orbit
     */
    public Orbit orbit;

    public List<Orbit> orbits;

    public ArtificialSatellite(String name, double mass, @Nonnull Orbit orbit) {
        super( name, mass, "textures/icon/satellite.png");
        this.orbit = orbit;
        this.orbit.orbited.satellites.add(this);
        this.orbitingAround = this.orbit.orbited;

        //period
        double T = this.orbit.period();

        this.trajectory = new Vector3d[ORBIT_PRECISION];
        this.velocity = new Vector3d[ORBIT_PRECISION];

        for(int t = 0; t < ORBIT_PRECISION; t++){
            double v = this.orbit.v((double)t / ORBIT_PRECISION * T);
            trajectory[t] = this.orbit.rWorld(v);
            velocity[t] = this.orbit.speed((double)t / ORBIT_PRECISION * T);
        }

        this.position = trajectory[200];
    }

    public ArtificialSatellite(String name, double mass, String path) {
        super(name, mass, path);
    }

    @Override
    public Vector3d[] predict(int t1, int t2) {
        return new Vector3d[0];
    }

    @Override
    // TODO if not an orbit
    public Vector3d calculatePosition(int time) {
        if(this.orbit != null) {
            double v = this.orbit.v((double) time / ORBIT_PRECISION * this.orbit.period());
            return toCartesian(this.orbit.r(v), v).scale(1.0D / AU);
        } else {
            return null;
        }
    }

    @Override
    public void update(int time) { }

    @Override
    public void draw(MatrixStack matrixStack, IRenderTypeBuffer renderBuffers, double zoom) { }

    public void draw2(Minecraft minecraft, MatrixStack matrixStack) {
        drawLogo(minecraft, matrixStack, this.texture, this.position, 7.0F);
    }
}
