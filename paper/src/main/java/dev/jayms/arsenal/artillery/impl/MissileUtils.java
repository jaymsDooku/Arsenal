package dev.jayms.arsenal.artillery.impl;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public final class MissileUtils {

    public static List<Location> computePath(Location loc, Location target) {
        Vector p1 = loc.toVector();
        Vector p3 = target.toVector();
        Vector p2 = new Vector((p1.getX() + p3.getX()) / 2, p1.getY() + 50, (p1.getZ() + p3.getZ()) / 2);

        List<Location> path = new ArrayList<>();
        for (double i = 0.01; i <= 1; i += 0.01) {
            double curveX = Math.pow((1 - i), 3) * p1.getX() +
                    3 * Math.pow((1 - i), 2) * i * p2.getX() +
                    3 * Math.pow((1 - i), 1) * Math.pow(i, 2) * p2.getX() +
                    Math.pow(i, 3) * p3.getX();
            double curveY = Math.pow((1 - i), 3) * p1.getY() +
                    3 * Math.pow((1 - i), 2) * i * p2.getY() +
                    3 * Math.pow((1 - i), 1) * Math.pow(i, 2) * p2.getY() +
                    Math.pow(i, 3) * p3.getY();
            double curveZ = Math.pow((1 - i), 3) * p1.getZ() +
                    3 * Math.pow((1 - i), 2) * i * p2.getZ() +
                    3 * Math.pow((1 - i), 1) * Math.pow(i, 2) * p2.getZ() +
                    Math.pow(i, 3) * p3.getZ();
            path.add(new Location(loc.getWorld(), curveX, curveY, curveZ));
        }
        return path;
    }

}
