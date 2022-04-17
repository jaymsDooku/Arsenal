package dev.jayms.arsenal.util;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class MaterialTools {

    public static boolean isPressurePlate(Material material) {
        return Arrays.asList(Material.BIRCH_PRESSURE_PLATE, Material.ACACIA_PRESSURE_PLATE,
                Material.CRIMSON_PRESSURE_PLATE, Material.OAK_PRESSURE_PLATE, Material.DARK_OAK_PRESSURE_PLATE,
                Material.HEAVY_WEIGHTED_PRESSURE_PLATE, Material.POLISHED_BLACKSTONE_PRESSURE_PLATE,
                Material.JUNGLE_PRESSURE_PLATE, Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.SPRUCE_PRESSURE_PLATE,
                Material.STONE_PRESSURE_PLATE, Material.WARPED_PRESSURE_PLATE).contains(material);
    }

}
