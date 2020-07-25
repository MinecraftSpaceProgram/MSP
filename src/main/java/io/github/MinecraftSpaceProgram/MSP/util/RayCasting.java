package io.github.MinecraftSpaceProgram.MSP.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.math.vector.Vector3d;
// TODO: 7/17/2020 add ray test for AABB and not only spheres

public final class RayCasting {

    /**
     * Returns the closest to P belonging to (AB)
     */
    private static Vector3d closestPoint(Vector3d A, Vector3d B, Vector3d P){
        Vector3d AB = B.subtract(A);
        Vector3d AP = P.subtract(A);
        // AH is the projection of AP on AB
        Vector3d AH = AB.scale(AP.dotProduct(AB) / AB.dotProduct(AB));
        return A.add(AH);
    }

    /**
     * Returns True if the ray collided with an object located at center
     *
     * @param center the center of the object which can be collided
     * @param radius the radius of the object
     * @param start the start of the ray
     * @param end the end of the ray
     */
    private static boolean rayTest(Vector3d center, double radius, Vector3d start, Vector3d end){
        Vector3d H = closestPoint(start, end, center);
        return H.distanceTo(center) < radius;
    }

    /**
     * @param points the points the ray can collide with
     * @param radii the radii of the points
     * @param start the start point of the ray
     * @param end the end point of the ray
     * @return if there was a collision returns false, -1 else returns true and the index of the closest point
     */
    public static Pair<Boolean, Integer> rayTestPoints(Vector3d[] points, double[] radii, Vector3d start, Vector3d end){
        boolean foundCollision = false;
        int closestPointIdx = -1;
        double minDistanceToStart = Double.MAX_VALUE;
        for (int i = 0; i < points.length; i++) {
            if (rayTest(points[i], radii[i], start, end)){
                double dst = start.distanceTo(points[i]);
                if (dst < minDistanceToStart){
                    closestPointIdx = i;
                    foundCollision = true;
                    minDistanceToStart = dst;
                }
            }
        }
        return new Pair<>(foundCollision, closestPointIdx);
    }
}
