package io.github.MinecraftSpaceProgram.MSP.physics.orbital;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.MinecraftSpaceProgram.MSP.Main;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;

import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.zoomPlanet;
import static io.github.MinecraftSpaceProgram.MSP.util.RenderUtils.drawCube;
import static java.lang.Math.*;

/**
 * all the angles are given in degrees
 */

public class Planet extends OrbitingBody{

    /**
     * display size of a planet
     */
    public double size;

    /**
     * current angle of a planet around itself
     */
    public double angle;

    /**
     * rotation speed of a planet
     */
    public double sideralRotation;

    /**
     * tilt of a planet
     */
    public Quaternion tilt;

    /**
     * list of satellites of this
     */
    public List<CelestialBody> satellites;

    public Planet(String name, double mass, String texturePath, CelestialBody orbitingAround,
                  double N, double i, double w, double a, double e, double M,
                  double size, double angle, double sideralRotation, Quaternion tilt) {

        super(name, mass, texturePath, orbitingAround, N, i, w, a, e, M);

        // aesthetics
        this.size = size;
        this.angle = angle;
        this.sideralRotation = sideralRotation;
        this.tilt = tilt;

        this.trajectory = this.predict(0, (int) this.P - 1);

        Main.LOGGER.debug(this::toString);
    }

    @Override
    public Vec3d[] predict(int t1, int t2) {
        Vec3d[] res = new Vec3d[t2 - t1];
        Main.LOGGER.debug("Calculating trajectory for " + this.name);
        for(int t = t1; t < t2; t++){
            res[t - t1] = this.calculatePosition(t);
        }
        return res;
    }

    @Override
    public void updatePlanet(int time) {
        this.position = calculatePosition(time);
    }

    private Vec3d calculatePosition(int time){
        // calculates the mean anomaly
        this.M = this.n * time;

        // approximates E
        this.E = PhysicsUtil.keplerE(this.M, this.e);

        // calculates the distance to the sun and the mean anomaly
        double xv = this.a * (cos(this.E) - this.e);
        double yv = this.a * (sqrt(1 - this.e * this.e) * sin(this.E));
        this.v = atan2(yv, xv);
        double r = sqrt(xv * xv + yv * yv);

        // calculates position in space
        double xh = r * (cos(N) * cos(v + w) - sin(N) * sin(v + w) * cos(i));
        double yh = r * (sin(N) * cos(v + w) + cos(N) * sin(v + w) * cos(i));
        double zh = r * sin(v + w) * sin(i);

        return new Vec3d(xh, yh, zh);
    }

    @Override
    public String toString() {
        return "Planet{" +
                "size=" + size +
                ", angle=" + angle +
                ", sideralRotation=" + sideralRotation +
                ", tilt=" + tilt +
                ", satellites=" + satellites +
                ", orbitingAround=" + orbitingAround +
                ", N=" + N +
                ", i=" + i +
                ", w=" + w +
                ", a=" + a +
                ", e=" + e +
                ", M=" + M +
                ", w1=" + w1 +
                ", L=" + L +
                ", q=" + q +
                ", Q=" + Q +
                ", P=" + P +
                ", n=" + n +
                ", T=" + T +
                ", v=" + v +
                ", E=" + E +
                ", name='" + name + '\'' +
                ", mass=" + mass +
                ", position=" + position +
                ", speed=" + speed +
                ", acceleration=" + acceleration +
                ", trajectory=" + Arrays.toString(trajectory) +
                '}';
    }

    @Override
    public void draw(MatrixStack matrixstack, IRenderTypeBuffer renderBuffers, double zoom) {

        double adjustedSize = zoomPlanet(this.size, zoom);

        this.updatePlanet(1);

        // IVertex builder to draw solids
        IVertexBuilder vertexBuilder = renderBuffers.getBuffer(RenderType.getSolid());

        matrixstack.translate(this.position.x - adjustedSize / 2, this.position.y - adjustedSize / 2 , this.position.z - adjustedSize / 2);
        matrixstack.rotate(this.tilt);
        this.tilt.conjugate();

        drawCube(new Vec3d(adjustedSize, 0, 0), new Vec3d(0, adjustedSize, 0), new Vec3d(0, 0, adjustedSize), matrixstack, vertexBuilder, material);

        matrixstack.rotate(this.tilt);
        this.tilt.conjugate();
        matrixstack.translate(- this.position.x + adjustedSize / 2, - this.position.y + adjustedSize / 2, - this.position.z + adjustedSize / 2);
    }
}
