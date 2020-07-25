package io.github.MinecraftSpaceProgram.MSP.physics.orbital;

import net.minecraft.util.math.vector.Quaternion;

import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.E;
import static java.lang.Math.toRadians;

public class SolarSystem {

    public static final Sun SUN = new Sun();

    public static final Planet MERCURY = new Planet(
            "Mercury",
            3.3011 * E(23),
            "planets/mercury",
            SUN,
            toRadians(48.3313),
            toRadians(7.0047),
            toRadians(29.1241),
            0.387098 * PhysicsUtil.AU,
            0.205635,
            toRadians(168.6562),
            4.880 * E(6),
            0,
            1 / (14075F * 3600F),
            Quaternion.ONE);

    public static final Planet VENUS = new Planet(
            "Venus",
            4.8675 * E(24),
            "planets/venus",
            SUN,
            toRadians(76.6799),
            toRadians(3.3946),
            toRadians(54.8910),
            0.723330 * PhysicsUtil.AU,
            0.006773,
            toRadians(48.0052),
            12.102 * E(6),
            0,
            0,
            Quaternion.ONE);

    public static final Planet EARTH = new Planet(
            "Earth",
            5.97237 * E(24),
            "planets/earth",
            SUN,
            0,
            0,
            toRadians(282.9404),
            PhysicsUtil.AU,
            0.016709,
            toRadians(356.0470),
            12.74 * E(6),
            0,
            0,
            Quaternion.ONE);

    public static final Planet MOON = new Planet(
            "Moon",
            7.342 * E(22),
            "planets/moon",
            EARTH,
            toRadians(125.1228),
            toRadians(5.1454),
            toRadians(318.0634),
            0.00257 * PhysicsUtil.AU,
            0.054900,
            toRadians(115.3654),
            3.46 * E(6),
            0,
            0,
            Quaternion.ONE);

    public static final Planet MARS = new Planet(
            "Mars",
            6.4171 * E(23),
            "planets/mars",
            SUN,
            toRadians(49.5574),
            toRadians(1.8497),
            toRadians(286.5016),
            1.523688 * PhysicsUtil.AU,
            0.093405,
            toRadians(18.6021),
            6.77 * E(6),
            0,
            0,
            Quaternion.ONE);

    public static final Planet JUPITER = new Planet(
            "Jupiter",
            1.898 * E(27),
            "planets/jupyter",
            SUN,
            toRadians(100.4542),
            toRadians(1.303),
            toRadians(273.8777),
            5.20256 * PhysicsUtil.AU,
            0.048498,
            toRadians(19.8950),
            139 * E(6),
            0,
            0,
            Quaternion.ONE);

    public static final Planet[] SOLAR_SYSTEM = {
            MERCURY,
            VENUS,
            EARTH,
            MARS,
            JUPITER};
}
