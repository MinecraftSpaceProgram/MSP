package io.github.MinecraftSpaceProgram.MSP.physics.orbital;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.MinecraftSpaceProgram.MSP.MSP;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Arrays;

import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.AU;
import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.zoomPlanet;
import static io.github.MinecraftSpaceProgram.MSP.util.RenderUtils.drawCube;
import static java.lang.Math.*;

/**
 * all the angles are given in degrees
 */

public class Planet extends OrbitingBody{

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
        this.lastUpdate = 0;

        MSP.LOGGER.debug(this::toString);
    }

    @Override
    public Vector3d[] predict(int t1, int t2) {
        Vector3d[] res = new Vector3d[t2 - t1];
        MSP.LOGGER.debug("Calculating trajectory for " + this.name);
        for(int t = t1; t < t2; t++){
            res[t - t1] = this.calculatePosition(t);
        }
        return res;
    }

    @Override
    public void updatePlanet(int time) {
        this.position = calculatePosition(time);
    }

    /**
     * Calculates the position of a planet in cartesian space at a given time
     * @return Vector3d corresponding to the x, y, z position of a planet in AU
     * Rotation is fucked up here
     */
    @Override
    public Vector3d calculatePosition(int time){
        // if the trajectory is known returns the known position
        if (this.trajectory != null && this.lastUpdate + this.T > time){
            return trajectory[(int)(time - this.lastUpdate)];
        } else {
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

            return new Vector3d(xh / AU, yh / AU, zh / AU);
        }
    }

    @Override
    public String toString() {
        return "Planet{" +
                "size=" + size +
                ", angle=" + angle +
                ", Sideral Rotation=" + sideralRotation +
                ", tilt=" + tilt +
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

        drawCube(new Vector3d(adjustedSize, 0, 0), new Vector3d(0, adjustedSize, 0), new Vector3d(0, 0, adjustedSize), matrixstack, vertexBuilder, material);

        matrixstack.rotate(this.tilt);
        this.tilt.conjugate();
        matrixstack.translate(- this.position.x + adjustedSize / 2, - this.position.y + adjustedSize / 2, - this.position.z + adjustedSize / 2);
    }
}
