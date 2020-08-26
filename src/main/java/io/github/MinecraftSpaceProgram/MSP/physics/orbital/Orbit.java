package io.github.MinecraftSpaceProgram.MSP.physics.orbital;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.util.RayCasting;
import io.github.MinecraftSpaceProgram.MSP.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.awt.*;

import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.E;
import static io.github.MinecraftSpaceProgram.MSP.physics.orbital.PhysicsUtil.*;
import static io.github.MinecraftSpaceProgram.MSP.util.RenderUtils.drawLine;
import static java.lang.Math.*;

/**
 * Additional information on all these parameters can be found p58 of <U>Fundamentals of Astrodynamics</U>.<br>
 * <i>All angles are expressed in radians and all distances in meters.</i>
 */
public class Orbit {

    /**
     * semi-latus rectum in m
     */
    public double p;

    /**
     * eccentricity
     * 0 <= e <= 1
     */
    public double e;

    /**
     * inclination in rad
     */
    public double i;

    /**
     * longitude of the ascending node in rad
     */
    public double W;

    /**
     * argument of periapsis in rad
     */
    public double w;

    /**
     * time of periapsis passage
     */
    public double T = 0;

    /**
     * The celestial body being orbited
     */
    public CelestialBody orbited;

    /**
     * The display color of the orbit
     */
    public Color color = new Color(0.0F, 0.7F, 0.7F, 0.8F);

    public boolean theoretical = false;

    public double startAngle = -PI;

    public double endAngle = PI;

    @Nullable
    public ManeuverNode node = null;

    public boolean hovering = false;
    public double hoverAngle;

    /**
     * gravitational parameter
     */
    private final double y;

    /**
     * @param r a position at a given time t0 in AU
     * @param v the speed at that given time t0
     */
    public Orbit(Vector3d r, Vector3d v, CelestialBody orbited) {

        r = r.scale(AU);

        this.orbited = orbited;
        this.y = this.orbited.mass * G;

        // angular momentum vector
        Vector3d h = r.crossProduct(v);

        // node vector
        Vector3d k = new Vector3d(0.0D, 0.0D, 1.0D);
        Vector3d n = k.crossProduct(h);

        // eccentricity vector
        Vector3d E = r.scale(v.lengthSquared() - y / r.length()).subtract(v.scale(r.dotProduct(v))).scale(1 / y);
        this.e = E.length();

        this.p = h.lengthSquared() / y;

        this.i = acos(h.z / h.length());
        this.W = ((n.y > 0) ? 1 : -1) * acos(n.x / n.length());
        this.w = ((E.z > 0) ? 1 : -1) * acos(n.dotProduct(E) / (n.length() * this.e));
    }

    /**
     * Base constructor
     */
    public Orbit(double p, double e, double i, double W, double w, double T, CelestialBody orbited) {

        this.orbited = orbited;
        this.y = this.orbited.mass * G;

        this.p = p;
        this.e = e;
        this.i = i;
        this.W = W;
        this.w = w;
        this.T = T;
    }

    /**
     * Draws the orbit
     */
    public void draw(MatrixStack matrixstack, IRenderTypeBuffer renderBuffers, Minecraft minecraft) {
        IVertexBuilder renderBuffer = renderBuffers.getBuffer(RenderType.getLines());

        Matrix4f matrixPos = matrixstack.getLast().getMatrix().copy();

        // draw the trajectory
        Vector3d previousPosition;
        Vector3d currentPosition = rWorld(0.0D);
        for (int i = 1; i < 1001; i++) {
            previousPosition = currentPosition;
            currentPosition = rWorld(i / 1000.0D * ((e >= 1) ? 1 : 2) * PI);
            drawLine(matrixPos, renderBuffer, new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), (int)(230.0D * i / 1000.0D) + 25), previousPosition, currentPosition);
        }

        // draws the POI
        // periapsis
        RenderUtils.drawLogo(
                minecraft,
                matrixstack,
                new ResourceLocation(MSP.MOD_ID, "textures/icon/periapsis.png"),
                this.rWorld(0),
                7.0F,
                this.color);
        // apoapsis
        RenderUtils.drawLogo(
                minecraft,
                matrixstack,
                new ResourceLocation(MSP.MOD_ID, "textures/icon/apoapsis.png"),
                this.rWorld(PI),
                7.0F,
                this.color);
        // ascending node
        RenderUtils.drawLogo(
                minecraft,
                matrixstack,
                new ResourceLocation(MSP.MOD_ID, "textures/icon/ascendingnode.png"),
                this.rWorld(-this.w),
                7.0F,
                this.color);
        // descending node
        RenderUtils.drawLogo(
                minecraft,
                matrixstack,
                new ResourceLocation(MSP.MOD_ID, "textures/icon/descendingnode.png"),
                this.rWorld(PI - this.w),
                7.0F,
                this.color);

        if (this.node != null) {
            // the node
            RenderUtils.drawLogo(
                    minecraft,
                    matrixstack,
                    new ResourceLocation(MSP.MOD_ID, "textures/icon/gear.png"),
                    this.rWorld(this.node.angle),
                    5.0F,
                    this.node.theoreticalTrajectory.color);

            this.node.theoreticalTrajectory.draw(matrixstack, renderBuffers, minecraft);
        }
    }

    /**
     * The period of the orbit in s
     */
    public double period() {
        if (this.e >= 1) {
            // The eccentricity of an ellipse is lesser then 1
            return Double.NaN;
        } else {
            // the semi-major axis
            double a = this.p / (1 - this.e * this.e);
            return PhysicsUtil.keplerPeriod(this.orbited.mass, a);
        }
    }

    /**
     * Polar equation of a conic section
     */
    public double r(double theta) {
        return this.p / (1 + this.e * cos(theta));
    }

    /**
     * Position in world space given theta,
     * see page 82 of <i>Fundamentals of Astrodynamics</i>
     */
    public Vector3d rWorld(double theta) {
        //Vector3f axisK = Vector3f.ZP.copy();
        //Vector3f axisN = Vector3f.XP.copy();
        //axisN.transform(new Quaternion(axisK, (float) this.W, false));
        //Vector3f axisW = Vector3f.ZP.copy();
        //axisK.transform(new Quaternion(axisN, (float) this.i, false));

        double r = r(theta) / AU;

        //r = rotateVector3d(r, new Quaternion(- (float) this.i, 0.0F, 0.0F, false));
        //r = rotateVector3d(r, new Quaternion(0.0F, 0.0F, - (float) this.W, false));
        return new Vector3d(
                r * (cos(this.W) * cos(this.w + theta) - sin(this.W) * sin(this.w + theta) * cos(i)),
                r * (sin(this.W) * cos(this.w + theta) + cos(this.W) * sin(this.w + theta) * cos(this.i)),
                r * sin(this.w + theta) * sin(this.i)

        );
    }

    /**
     * true anomaly as a function of time
     *
     * @param t the time since last passage at periapsis
     */
    public double v(double t) {
        if (e < 1) { // parabola
            // calculates the mean anomaly
            double M = 2 * PI / this.period() * t;

            // approximates the eccentric anomaly E
            double E = PhysicsUtil.keplerE(M, this.e);

            // calculates the true anomaly
            return ((M < PI) ? 1 : -1) * acos((cos(E) - e) / (1 - e * cos(E)));
        } else { // hyperbola
            // TODO add support for parabolae and hyperbolae
            return 0;
        }
    }

    /**
     * speed as a function of time
     *
     * @param t the time since last passage at periapsis
     */
    public Vector3d speed2(double t) {
        // gross approximation because this is really difficult
        // oh yeah and im lazy
        //double EPSILON = 1.0D / (10000 * ORBIT_PRECISION);
        double dt = E(-6);//* period();
        double v2 = v(t);
        double v1 = v(t + dt);
        Vector3d vdt = rWorld(v1).subtract(rWorld(v2));
        return vdt.scale(AU / dt);
    }

    public Vector3d speed(double theta) {
        // the norm of the speed
        double v = sqrt(this.y * (2 / r(theta) - (1 - this.e * this.e) / this.p));

        // d r(theta) / d theta (theta - theta0) + r0
        double temp =(this.e * sin(theta)) / (1 + e * cos(theta));

        Vector3d tangentSlope = rWorld(theta).normalize().scale(temp);

        // Page 82 of Fundamentals of Astrodynamics
        Vector3d normal = new Vector3d(sin(this.W) * sin(this.i), - cos(this.W) * sin(this.i), cos(this.i));

        Vector3d radial = normal.crossProduct(rWorld(theta));
        radial = radial.normalize();

        return tangentSlope.add(radial).normalize().scale(v);
    }


    // TODO if the hover point is not in the orbit range
    // TODO zoom ?
    // TODO hover POI ?
    public boolean hover(Vector3d start, Vector3d end, MatrixStack matrixStack, Minecraft minecraft) {

        if (this.node == null) {
            Pair<Double, Double> rayCastResult = RayCasting.closestAngleEllipse(
                    start,
                    end,
                    this
            );

            double theta = rayCastResult.getFirst();
            double distance = rayCastResult.getSecond();

            if (distance <= 0.05D) {
                theta = snap(theta);
                RenderUtils.drawLogo(
                        minecraft,
                        matrixStack,
                        new ResourceLocation(MSP.MOD_ID, "textures/icon/circle.png"),
                        this.rWorld(theta),
                        5.0F,
                        this.color
                );
                hoverAngle = theta;
                hovering = true;
                return true;
            } else {
                hovering = false;
                return false;
            }
        } else {
            this.hovering = false;
            return this.node.theoreticalTrajectory.hover(start, end, matrixStack, minecraft);
        }
    }

    @Override
    public String toString() {
        return "Orbit{" +
                "p=" + p +
                ", e=" + e +
                ", i=" + i +
                ", W=" + W +
                ", w=" + w +
                ", T=" + T +
                ", startAngle=" + startAngle +
                ", endAngle=" + endAngle +
                '}';
    }

    private double snap(double theta){
        double epsilon = 3 * E(-2);
        if (abs(theta) < epsilon){
            return 0.0D;
        }
        if (abs(theta - PI) < epsilon){
            return PI;
        }
        if (abs(theta - this.w) < epsilon){
            return this.w;
        }
        if (abs(theta - this.w - PI) < epsilon) {
            return PI + this.w;
        }
        return theta;
    }
}
