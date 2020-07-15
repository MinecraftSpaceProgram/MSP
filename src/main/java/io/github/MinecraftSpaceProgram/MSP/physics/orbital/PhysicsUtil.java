package io.github.MinecraftSpaceProgram.MSP.physics.orbital;

import static java.lang.Math.*;

public final class PhysicsUtil {
    /**
     * the universal gravitational constant
     */
    public static final double G = 6.674 * E(-11);

    public static final double AU = 1.495978707 * E(11);

    public static double E(int power) {
        if (power >= 0) {
            return pow(10, power);
        }
        return 1.0 / pow(10, -power);
    }

    /**
     * Calculates P using Kepler's 3rd law of motion
     *
     * @param M mass of the 2 bodies
     * @param a semi-major axis
     * @return period
     */
    public static double keplerPeriod(double M, double a) {
        return 2 * PI * sqrt(a * a * a / (G * M));
    }

    /**
     * Approximates a solution to Kepler's equation
     * M = E - e sin(E)
     * using Newton's method, that is: x_{n+1} = x_n - f(x_n)/f'(x_n)
     *
     * @param M mean anomaly
     * @param e eccentricity
     * @return eccentric anomaly (radians)
     */
    public static double keplerE(double M, double e) {
        // modifies the mean anomaly
        M /= 2* PI;
        M = 2 * PI *(M - floor(M));

        // approximates E in radians
        double E1;

        // elliptic rather than parabolic orbit
        if (e < 0.8) {
            E1 = M;
        } else {
            E1 = PI;
        }
        double E2 = E1 - e * sin(M) - M;

        double epsilon = E(-15);
        int iterations = 0;

        while (abs(E2) > epsilon) {
            E1 = E1 - E2 / (1 - e * cos(E1));
            E2 = E1 - e * sin(E1) - M;

            iterations++;
            if (iterations > 30) {
                throw new Error("Could not approximate planet's position" + abs(E2));
            }
        }

        //returns a result in radians
        return E1;
    }

    /**
     * sigmoid function:
     * f(x) = 1 / (1 + e^-x)
     */
    public static double sigmoid(double x) {
        return 1 / (1 + pow(Math.E, -x));
    }

    /**
     * overcomplicated zoom function for planets to be the right size when zoomed in and larger when zoomed out
     *
     * @param size the size of the planet
     * @param zoom how many times the scroll happened (0 < zoom < 100)
     */
    public static double zoomPlanet(double size, double zoom) {
        double lambda = sigmoid((90 - zoom) / 5);
        return lambda * size + 2 * (1 - lambda) * log(size) * E(9);
    }

}
