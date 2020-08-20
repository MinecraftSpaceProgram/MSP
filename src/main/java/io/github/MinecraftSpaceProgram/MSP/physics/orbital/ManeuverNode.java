package io.github.MinecraftSpaceProgram.MSP.physics.orbital;

import net.minecraft.util.math.vector.Vector3d;

import java.awt.*;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class ManeuverNode {

    /**
     * The time at which the maneuver takes place
     */
    public long executionTime;

    /**
     * angle of the ellipse where the maneuver will take place
     */
    public double angle;

    /**
     * delta-V
     */
    public Vector3d deltaV;

    /**
     * Color
     */
    private final Color color;

    /**
     * Trajectory if the maneuver is followed
     */
    public Orbit theoreticalTrajectory;

    private final Orbit parentOrbit;

    private final Vector3d orbitalSpeed;

    public final Vector3d PROGRADE;
    public final Vector3d RADIAL;
    public final Vector3d NORMAL;


    public ManeuverNode(long executionTime, double angle, Orbit orbit, Vector3d deltaV, Orbit parentOrbit, Color color) {
        this.executionTime = executionTime;
        this.angle = angle;
        this.deltaV = deltaV;
        this.color = color;
        this.parentOrbit = parentOrbit;
        this.orbitalSpeed = orbit.speed(angle);

        this.PROGRADE = this.orbitalSpeed.normalize();
        this.NORMAL = new Vector3d(sin(this.parentOrbit.W) * sin(this.parentOrbit.i), -cos(this.parentOrbit.W) * sin(this.parentOrbit.i), cos(this.parentOrbit.i));
        this.RADIAL = NORMAL.crossProduct(PROGRADE);

        this.theoreticalTrajectory = new Orbit(
                orbit.rWorld(angle),
                this.orbitalSpeed.add(deltaV),
                orbit.orbited
        );

        this.theoreticalTrajectory.color = this.color;
    }

    public void setDeltaV(Vector3d deltaV) {
        this.deltaV = deltaV;
        this.theoreticalTrajectory = new Orbit(
                this.parentOrbit.rWorld(this.angle),
                this.orbitalSpeed.add(this.deltaV),
                this.parentOrbit.orbited
        );
        this.theoreticalTrajectory.color = this.color;
    }
}
