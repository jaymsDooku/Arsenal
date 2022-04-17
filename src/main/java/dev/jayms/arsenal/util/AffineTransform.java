package dev.jayms.arsenal.util;

import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

//https://github.com/EngineHub/WorldEdit/blob/master/worldedit-core/src/main/java/com/sk89q/worldedit/math/transform/AffineTransform.java
public class AffineTransform {

    private final double m00;
    private final double m01;
    private final double m02;
    private final double m03;

    private final double m10;
    private final double m11;
    private final double m12;
    private final double m13;

    private final double m20;
    private final double m21;
    private final double m22;
    private final double m23;

    public AffineTransform() {
        m00 = m11 = m22 = 1;
        m01 = m02 = m03 = 0;
        m10 = m12 = m13 = 0;
        m20 = m21 = m23 = 0;
    }

    public AffineTransform(double xx, double yx, double zx, double tx,
                           double xy, double yy, double zy, double ty, double xz, double yz,
                           double zz, double tz) {
        m00 = xx;
        m01 = yx;
        m02 = zx;
        m03 = tx;
        m10 = xy;
        m11 = yy;
        m12 = zy;
        m13 = ty;
        m20 = xz;
        m21 = yz;
        m22 = zz;
        m23 = tz;
    }

    public AffineTransform concatenate(AffineTransform that) {
        double n00 = m00 * that.m00 + m01 * that.m10 + m02 * that.m20;
        double n01 = m00 * that.m01 + m01 * that.m11 + m02 * that.m21;
        double n02 = m00 * that.m02 + m01 * that.m12 + m02 * that.m22;
        double n03 = m00 * that.m03 + m01 * that.m13 + m02 * that.m23 + m03;
        double n10 = m10 * that.m00 + m11 * that.m10 + m12 * that.m20;
        double n11 = m10 * that.m01 + m11 * that.m11 + m12 * that.m21;
        double n12 = m10 * that.m02 + m11 * that.m12 + m12 * that.m22;
        double n13 = m10 * that.m03 + m11 * that.m13 + m12 * that.m23 + m13;
        double n20 = m20 * that.m00 + m21 * that.m10 + m22 * that.m20;
        double n21 = m20 * that.m01 + m21 * that.m11 + m22 * that.m21;
        double n22 = m20 * that.m02 + m21 * that.m12 + m22 * that.m22;
        double n23 = m20 * that.m03 + m21 * that.m13 + m22 * that.m23 + m23;
        return new AffineTransform(
                n00, n01, n02, n03,
                n10, n11, n12, n13,
                n20, n21, n22, n23);
    }

    public AffineTransform rotateY(double theta) {
        double cot = MathUtils.dCos(theta);
        double sit = MathUtils.dSin(theta);
        return concatenate(
                new AffineTransform(
                        cot, 0, sit, 0,
                        0, 1, 0, 0,
                        -sit, 0, cot, 0));
    }

    public Vector apply(Vector vector) {
        return new Vector(
                vector.getX() * m00 + vector.getY() * m01 + vector.getZ() * m02 + m03,
                vector.getX() * m10 + vector.getY() * m11 + vector.getZ() * m12 + m13,
                vector.getX() * m20 + vector.getY() * m21 + vector.getZ() * m22 + m23);
    }

}
