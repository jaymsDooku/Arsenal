package dev.jayms.arsenal.structure;

import dev.jayms.arsenal.util.AffineTransform;
import dev.jayms.arsenal.util.BlockFaceTransform;
import dev.jayms.arsenal.util.MaterialTools;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.material.PressurePlate;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StructureBlock {

    private int relX;
    private int relY;
    private int relZ;
    private Material material;
    private String data;

    private StructureBlock(StructureBlockBuilder builder) {
        this.relX = builder.relX;
        this.relY = builder.relY;
        this.relZ = builder.relZ;
        this.material = builder.material;
        this.data = builder.data;
    }

    public static StructureBlock fromMap(Map<String, Object> map) {
        double relXDouble = (double) map.get("relX");
        double relYDouble = (double) map.get("relY");
        double relZDouble = (double) map.get("relZ");
        int relX = (int) relXDouble;
        int relY = (int) relYDouble;
        int relZ = (int) relZDouble;
        Material material = Material.valueOf((String) map.get("material"));
        String data = (String) map.get("data");
        return StructureBlock.builder(material)
                .pos(relX, relY, relZ)
                .data(data)
                .build();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("relX", relX);
        map.put("relY", relY);
        map.put("relZ", relZ);
        map.put("material", material.toString());
        map.put("data", data);
        return map;
    }

    public double distanceToOrigin() {
        return Math.sqrt(distanceToOriginSquared());
    }

    public double distanceToOriginSquared() {
        return (relX * relX) + (relY * relY) + (relZ * relZ);
    }

    public int getRelX() {
        return relX;
    }

    public int getRelY() {
        return relY;
    }

    public int getRelZ() {
        return relZ;
    }

    public Material getMaterial() {
        return material;
    }

    public String getData() {
        return data;
    }

    public BlockData getBlockData() {
        return Bukkit.createBlockData(data);
    }

    public boolean isDependent() {
        if (MaterialTools.isPressurePlate(material)) {
            return true;
        }

        BlockData blockData = getBlockData();
        if (blockData instanceof WallSign) {
            return true;
        }
        if (blockData instanceof Sign) {
            return true;
        }
        return false;
    }

    public BlockFace getDependentDirection() {
        if (!isDependent()) {
            return null;
        }
        if (MaterialTools.isPressurePlate(material)) {
            return BlockFace.DOWN;
        }
        BlockData blockData = getBlockData();
        if (blockData instanceof WallSign) {
            return ((WallSign) blockData).getFacing().getOppositeFace();
        }
        if (blockData instanceof Sign) {
            return BlockFace.DOWN;
        }

        return null;
    }

    public Vector getDependentPosition() {
        BlockFace blockFace = getDependentDirection();
        if (blockFace == null) {
            return null;
        }

        Vector direction = blockFace.getDirection();
        int directionX = direction.getBlockX();
        int directionY = direction.getBlockY();
        int directionZ = direction.getBlockZ();

        int resultX = relX + directionX;
        int resultY = relY + directionY;
        int resultZ = relZ + directionZ;

        return new Vector(resultX, resultY, resultZ);
    }

    public boolean samePosition(Vector pos) {
        return pos.getBlockX() == relX && pos.getBlockY() == relY && pos.getBlockZ() == relZ;
    }

    private Location absoluteLocation(Location origin, BlockFaceTransform blockFaceTransform) {
        Vector transformed = blockFaceTransform.getAffineTransform().apply(new Vector(relX, relY, relZ));
        return origin.clone().add(transformed);
    }

    public void place(Location origin, BlockFaceTransform blockFaceTransform) {
        Location toPlace = absoluteLocation(origin, blockFaceTransform);
        BlockData blockData = data == null ? material.createBlockData() : Bukkit.createBlockData(data);
        if (blockData instanceof Directional) {
            Directional directional = (Directional) blockData;
            directional.setFacing(blockFaceTransform.rotateBlockFace(directional.getFacing()));
        }
        toPlace.getBlock().setBlockData(blockData);
    }

    public void destroy(Location origin, BlockFaceTransform affineTransform) {
        Location toDestroy = absoluteLocation(origin, affineTransform);
        toDestroy.getBlock().setBlockData(Material.AIR.createBlockData());
    }

    public static StructureBlockBuilder builder(Material material) {
        return new StructureBlockBuilder(material);
    }

    public static class StructureBlockBuilder {

        private Material material;
        private int relX;
        private int relY;
        private int relZ;
        private String data;

        StructureBlockBuilder(Material material) {
            this.material = material;
        }

        public StructureBlockBuilder pos(int relX, int relY, int relZ) {
            this.relX = relX;
            this.relY = relY;
            this.relZ = relZ;
            return this;
        }

        public StructureBlockBuilder material(Material material) {
            this.material = material;
            return this;
        }

        public StructureBlockBuilder data(String data) {
            this.data = data;
            return this;
        }

        public StructureBlock build() {
            return new StructureBlock(this);
        }

    }

    public static class StructureBlockComparator implements Comparator<StructureBlock> {

        @Override
        public int compare(StructureBlock block1, StructureBlock block2) {
            double distance1 = block1.distanceToOriginSquared();
            double distance2 = block2.distanceToOriginSquared();

            Material type1 = block1.getMaterial();
            Material type2 = block2.getMaterial();

            if (block1.isDependent() && block2.isDependent()) {
                Vector dependentPos1 = block1.getDependentPosition();
                if (block2.samePosition(dependentPos1)) {
                    return 1;
                }

                Vector dependentPos2 = block2.getDependentPosition();
                if (block1.samePosition(dependentPos2)) {
                    return -1;
                }
            } else if (block1.isDependent() && !block2.isDependent()) {
                return 1;
            } else if (!block1.isDependent() && block2.isDependent()) {
                return -1;
            }

            if (type1 != type2) {
                if (type1.isSolid() && !type2.isSolid()) {
                    return -1;
                } else if (!type1.isSolid() && type2.isSolid()) {
                    return 1;
                }
            }

            return Double.compare(distance1, distance2);
        }
    }

}
