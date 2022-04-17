package dev.jayms.arsenal.structure;

import org.bukkit.block.data.type.WallSign;
import org.bukkit.util.Vector;

public class Comparators {

    public int compareWallSigns(WallSign wallSign, StructureBlock block, int returnIfEqual) {
        Vector behindVec1 = wallSign.getFacing().getOppositeFace().getDirection();
        int behindX = behindVec1.getBlockX();
        int behindY = behindVec1.getBlockY();
        int behindZ = behindVec1.getBlockZ();

        if (behindX == block.getRelX() && behindY == block.getRelY() && behindZ == block.getRelZ()) {
            return returnIfEqual;
        }

        return 0;
    }

}
