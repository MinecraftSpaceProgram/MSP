package io.github.MinecraftSpaceProgram.MSP.util;

import com.mojang.datafixers.util.Pair;
import io.github.MinecraftSpaceProgram.MSP.physics.orbital.Orbit;
import io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;

import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.AU;
import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.toCartesian;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
// TODO: 7/17/2020 add ray test for AABB and not only spheres

public final class RayCasting {

    /**
     * Returns the closest to P belonging to (AB)
     */
    private static Vector3d closestPoint(Vector3d A, Vector3d B, Vector3d P) {
        Vector3d AB = B.subtract(A);
        Vector3d AP = P.subtract(A);
        // AH is the projection of AP on AB
        Vector3d AH = AB.scale(AP.dotProduct(AB) / AB.dotProduct(AB));
        return A.add(AH);
    }

    /**
     * @return the mean anomaly of the point of the ellipse closest to (AB)
     */
    public static Pair closestAngleEllipse(Vector3d A, Vector3d B, Orbit orbit) {
        // the rotation of the elliptical plane
        Quaternion rotationOfEllipticalPlane = new Quaternion(0.0F, 0.0F, (float) orbit.W, false);
        rotationOfEllipticalPlane.multiply(new Quaternion((float) orbit.i, 0.0F, 0.0F, false));
        rotationOfEllipticalPlane.multiply(new Quaternion(0.0F, 0.0F, (float) orbit.w, false));

        // the position of the main foci of the ellipse
        Vector3d origin = orbit.orbited.position;

        // the directing vector of (AB) in the elliptical plane coordinates
        Vector3d u = PhysicsUtil.changeBasis(B.subtract(A), origin, rotationOfEllipticalPlane);
        u = u.normalize();

        Vector3d AA = PhysicsUtil.changeBasis(A, origin, rotationOfEllipticalPlane);

        // the intersection of (AB) and the orbital plane
        //  P.yes this assumes it exists :)
        Vector3d P = AA.subtract(u.scale(AA.z / u.z));

        double theta = Math.atan2(P.y, P.x);

        // newton's method
        double p = orbit.p / AU;
        double e = orbit.e;
        double x = P.x;
        double y = P.y;
        double mu = u.x;
        double nu = u.y;

        for (int i = 0; i < 10; i++) {
            final double temp1 = mu * cos(theta) + nu * sin(theta);
            final double temp2 = nu * (e + cos(theta)) - mu * sin(theta);
            final double temp3 = y * (e + cos(theta)) - x * sin(theta);
            final double temp4 = -nu * sin(theta) - mu * cos(theta);

            final double f =
                    p * temp1 * temp2
                            - (1 + e * cos(theta)) * (mu * x + nu * y) * temp2
                            + (1 + e * cos(theta)) * temp3
                            - e * p * sin(theta);

            final double df =
                    p * (-mu * sin(theta) + nu * cos(theta)) * temp2
                            + p * temp1 * temp4
                            + e * sin(theta) * (mu * x + nu * y) * temp2
                            - (1 + e * cos(theta)) * (mu * x + nu * y) * temp4
                            - e * sin(theta) * temp3
                            + (1 + e * cos(theta)) * (-y * sin(theta) - x * cos(theta))
                            - e * p * cos(theta);

            theta = theta - (f / df);
        }

        // closest point on the ray using (1)
        double lambda = orbit.r(theta) / AU *  (mu*cos(theta) + nu*sin(theta)) - mu*x - nu*y;
        Vector3d closestPoint = P.add(u.scale(lambda));

        // distance between array and the ray
        double distance = closestPoint.distanceTo(toCartesian(orbit.r(theta) / AU, theta));

        return new Pair(theta, distance);
    }

    /**
     * @param center the center of the object which can be collided
     * @param radius the radius of the object
     * @param start  the start of the ray
     * @param end    the end of the ray
     * @return True if the ray collided with an object located at center
     */
    private static boolean rayTest(Vector3d center, double radius, Vector3d start, Vector3d end) {
        Vector3d H = closestPoint(start, end, center);
        return H.distanceTo(center) < radius;
    }

    /**
     * @param points the points the ray can collide with
     * @param radii  the radii of the points
     * @param start  the start point of the ray
     * @param end    the end point of the ray
     * @return if there was a collision returns false, -1 else returns true and the index of the closest point
     */
    public static Pair<Boolean, Integer> rayTestPoints(Vector3d[] points, double[] radii, Vector3d start, Vector3d end) {
        boolean foundCollision = false;
        int closestPointIdx = -1;
        double minDistanceToStart = Double.MAX_VALUE;
        for (int i = 0; i < points.length; i++) {
            if (rayTest(points[i], radii[i], start, end)) {
                double dst = start.distanceTo(points[i]);
                if (dst < minDistanceToStart) {
                    closestPointIdx = i;
                    foundCollision = true;
                    minDistanceToStart = dst;
                }
            }
        }
        return new Pair<>(foundCollision, closestPointIdx);
    }
}
