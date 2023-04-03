package dev.jayms.arsenal.util;

import org.bukkit.block.BlockFace;

public enum RotAxis {

    X, Z;

    public static RotAxis getAxis(BlockFace face) {
        switch (face) {
            case NORTH:
            case SOUTH:
                return RotAxis.X;
            case WEST:
            case EAST:
            default:
                return RotAxis.Z;
        }
    }

}
