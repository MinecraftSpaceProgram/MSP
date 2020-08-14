package io.github.MinecraftSpaceProgram.MSP.physics.orbital;

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
     * Trajectory if the maneuver is followed
     */
    public Orbit theoreticalTrajectory;

}
