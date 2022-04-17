package dev.jayms.arsenal.util;

// https://github.com/EngineHub/WorldEdit/blob/master/worldedit-core/src/main/java/com/sk89q/worldedit/math/MathUtils.java
public class MathUtils {

    /**
     * Returns the cosine of an angle given in degrees. This is better than
     * just {@code Math.cos(Math.toRadians(degrees))} because it provides a
     * more accurate result for angles divisible by 90 degrees.
     *
     * @param degrees the angle
     * @return the cosine of the given angle
     */
    public static double dCos(double degrees) {
        int dInt = (int) degrees;
        if (degrees == dInt && dInt % 90 == 0) {
            dInt %= 360;
            if (dInt < 0) {
                dInt += 360;
            }
            switch (dInt) {
                case 0:
                    return 1.0;
                case 90:
                    return 0.0;
                case 180:
                    return -1.0;
                case 270:
                    return 0.0;
                default:
                    break;
            }
        }
        return Math.cos(Math.toRadians(degrees));
    }

    /**
     * Returns the sine of an angle given in degrees. This is better than just
     * {@code Math.sin(Math.toRadians(degrees))} because it provides a more
     * accurate result for angles divisible by 90 degrees.
     *
     * @param degrees the angle
     * @return the sine of the given angle
     */
    public static double dSin(double degrees) {
        int dInt = (int) degrees;
        if (degrees == dInt && dInt % 90 == 0) {
            dInt %= 360;
            if (dInt < 0) {
                dInt += 360;
            }
            switch (dInt) {
                case 0:
                    return 0.0;
                case 90:
                    return 1.0;
                case 180:
                    return 0.0;
                case 270:
                    return -1.0;
                default:
                    break;
            }
        }
        return Math.sin(Math.toRadians(degrees));
    }

}
