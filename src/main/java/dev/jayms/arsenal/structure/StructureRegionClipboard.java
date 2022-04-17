package dev.jayms.arsenal.structure;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class StructureRegionClipboard {

    private static Map<UUID, StructureRegionClipboard> regionClipboards = new HashMap<>();

    public static StructureRegionClipboard getRegionClipboard(Player player) {
        StructureRegionClipboard regionClipboard = regionClipboards.get(player.getUniqueId());
        if (regionClipboard == null) {
            regionClipboard = new StructureRegionClipboard();
            regionClipboards.put(player.getUniqueId(), regionClipboard);
        }
        return regionClipboard;
    }

    private Location point1;
    private Location point2;

    public void setPoint1(Location point1) {
        this.point1 = point1;
    }

    public void setPoint2(Location point2) {
        this.point2 = point2;
    }

    public Location getPoint1() {
        return point1;
    }

    public Location getPoint2() {
        return point2;
    }

    public boolean inside(Location loc) {
        if (!isReady()) {
            return false;
        }

        Location minLoc = getMin();
        Location maxLoc = getMax();

        if (!loc.getWorld().getUID().equals(minLoc.getWorld().getUID())) {
            return false;
        }

        int minX = minLoc.getBlockX();
        int minY = minLoc.getBlockY();
        int minZ = minLoc.getBlockZ();

        int maxX = maxLoc.getBlockX();
        int maxY = maxLoc.getBlockY();
        int maxZ = maxLoc.getBlockZ();

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        return (minX <= x && x <= maxX) &&
                (minY <= y && y <= maxY) &&
                (minZ <= z && z <= maxZ);
    }

    public boolean hasTwoPoints() {
        return point1 != null && point2 != null;
    }

    public boolean inSameWorld() {
        return point1.getWorld().getUID().equals(point2.getWorld().getUID());
    }

    public boolean isReady() {
        return hasTwoPoints() && inSameWorld();
    }

    private void nullCheck() {
        if (point1 == null || point2 == null) {
            throw new NullPointerException("Need two points.");
        }
    }

    private void worldCheck() {
        if (!point1.getWorld().getUID().equals(point2.getWorld().getUID())) {
            throw new IllegalStateException("World mismatch.");
        }
    }

    public Location getMin() {
        nullCheck();
        worldCheck();

        return new Location(point1.getWorld(),
                Math.min(point1.getX(), point2.getX()),
                Math.min(point1.getY(), point2.getY()),
                Math.min(point1.getZ(), point2.getZ()));
    }

    public Location getMax() {
        nullCheck();
        worldCheck();

        return new Location(point1.getWorld(),
                Math.max(point1.getX(), point2.getX()),
                Math.max(point1.getY(), point2.getY()),
                Math.max(point1.getZ(), point2.getZ()));
    }

    public List<Block> getAllNonAirBlocks() {
        List<Block> blocks = new ArrayList<>();

        Location min = getMin();
        Location max = getMax();

        int minX = min.getBlockX();
        int minY = min.getBlockY();
        int minZ = min.getBlockZ();

        int maxX = max.getBlockX();
        int maxY = max.getBlockY();
        int maxZ = max.getBlockZ();

        World world = min.getWorld();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() != Material.AIR) {
                        blocks.add(block);
                    }
                }
            }
        }

        return blocks;
    }
}
