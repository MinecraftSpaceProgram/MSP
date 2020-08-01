package io.github.MinecraftSpaceProgram.MSP.physics.orbital;

import static java.lang.Math.PI;

/**
 * Class describing a body in a stable orbit
 */
public abstract class OrbitingBody extends CelestialBody{

    /**
    * Longitude of the ascending node
    */
    public double N;

    /**
     * inclination to the ecliptic
     */
    public double i;

    /**
     * argument of perihelion
     * angle of the body's ascending node to it's perihelion
     */
    public double w;

    /**
     * semi-major axis
     */
    public double a;

    /**
     * eccentricity
     */
    public double e;

    /**
     * mean anomaly
     * fraction of the elliptical orbit that has elapsed since
     * the orbiting body passed its perihelion expressed as an angle
     */
    public double M;



    /**
     * longitude of perihelion
     */
    public double w1;

    /**
     * mean longitude
     */
    public double L;

    /**
     * perihelion distance
     */
    public double q;

    /**
     * aphelion distance
     */
    public double Q;

    /**
     * orbital period (days)
     * time a planet takes to complete one orbit around the sun
     */
    public double P;

    /**
     * mean motion
     */
    public double n;

    /**
     * time of perihelion
     */
    public double T;

    /**
     * true anomaly
     * angle between position and perihelion
     */
    public double v;

    /**
     * eccentric anomaly
     */
    public double E;

    /**
     * the last time the planet's trajectory was calculated
     */
    public double lastUpdate = -1;


    public OrbitingBody(String name, double mass, String texturePath, CelestialBody orbitingAround,
                        double N, double i, double w, double a, double e, double M) {
        super(name, mass, texturePath);

        // sets the orbiting around
        this.orbitingAround = orbitingAround;
        this.orbitingAround.satellites.add(this);

        // sets the primary orbital elements
        this.N = N;
        this.i = i;
        this.w = w;
        this.a = a;
        this.e = e;
        this.M = M;

        // calculates related orbital elements
        this.P = PhysicsUtil.keplerPeriod(mass + orbitingAround.mass, this.a) / (3600 * 24);
        this.n = 2 * PI / P;
        this.w1 = this.N + this.w;
        this.L = M + w1;
        this.q = this.a * (1 - this.e);
        this.Q = this.a * (1 + this.e);
    }
}
