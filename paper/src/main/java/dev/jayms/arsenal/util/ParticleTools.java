package dev.jayms.arsenal.util;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class ParticleTools {

    public static void displayColoredParticle(final Location loc, Particle type, final String hexVal, final float xOffset, final float yOffset, final float zOffset) {
        int r = 0;
        int g = 0;
        int b = 0;
        if (hexVal.length() <= 6) {
            r = Integer.valueOf(hexVal.substring(0, 2), 16).intValue();
            g = Integer.valueOf(hexVal.substring(2, 4), 16).intValue();
            b = Integer.valueOf(hexVal.substring(4, 6), 16).intValue();
        } else if (hexVal.length() <= 7 && hexVal.substring(0, 1).equals("#")) {
            r = Integer.valueOf(hexVal.substring(1, 3), 16).intValue();
            g = Integer.valueOf(hexVal.substring(3, 5), 16).intValue();
            b = Integer.valueOf(hexVal.substring(5, 7), 16).intValue();
        }

        if (type != Particle.REDSTONE && type != Particle.SPELL_MOB && type != Particle.SPELL_MOB_AMBIENT) {
            type = Particle.REDSTONE;
        }
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(r, g, b), 1);
        loc.getWorld().spawnParticle(type, loc, 1, xOffset, yOffset, zOffset, 0, dustOptions, true);
    }

    public static void displayColoredParticle(final Location loc, final String hexVal) {
        displayColoredParticle(loc, Particle.REDSTONE, hexVal, 0, 0, 0);
    }

    public static void displayColoredParticle(final Location loc, final String hexVal, final float xOffset, final float yOffset, final float zOffset) {
        displayColoredParticle(loc, Particle.REDSTONE, hexVal, xOffset, yOffset, zOffset);
    }

    public static void drawLine(Location start, Location dest, int particles, ParticlePlay play) {
        Vector dir = dest.clone().subtract(start).toVector();
        double length = dir.length();
        double ratio = length / particles;
        Vector d = dir.normalize().multiply(ratio);
        Location loc = start.clone();
        for (int i = 0; i < particles; i++) {
            loc.add(d);
            play.play(loc);
        }
    }

    @FunctionalInterface
    public interface ParticlePlay {

        void play(Location loc);

    }

}
