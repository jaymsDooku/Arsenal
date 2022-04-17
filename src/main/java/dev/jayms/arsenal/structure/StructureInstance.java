package dev.jayms.arsenal.structure;

import dev.jayms.arsenal.util.AffineTransform;
import dev.jayms.arsenal.util.BlockFaceTransform;
import org.bukkit.Location;

public class StructureInstance {

    private final StructureBlueprint structureBlueprint;
    private final Location origin;
    private final BlockFaceTransform blockFaceTransform;
    private int blocksPlacedIndex;

    public StructureInstance(Location origin, StructureBlueprint structureBlueprint, BlockFaceTransform blockFaceTransform) {
        this(origin, structureBlueprint, 0, blockFaceTransform);
    }

    public StructureInstance(Location origin, StructureBlueprint structureBlueprint, int blocksPlacedIndex, BlockFaceTransform blockFaceTransform) {
        this.origin = origin;
        this.structureBlueprint = structureBlueprint;
        this.blocksPlacedIndex = blocksPlacedIndex;
        this.blockFaceTransform = blockFaceTransform;
    }

    public boolean hasNextBlock() {
        return structureBlueprint.getBlock(blocksPlacedIndex + 1) != null;
    }

    public void placeNextBlock() {
        StructureBlock block = structureBlueprint.getBlock(++blocksPlacedIndex);
        block.place(origin, blockFaceTransform);
    }

    public boolean hasPreviousBlock() {
        return structureBlueprint.getBlock(blocksPlacedIndex - 1) != null;
    }

    public void destroyPreviousBlock() {
        StructureBlock block = structureBlueprint.getBlock(blocksPlacedIndex--);
        block.destroy(origin, blockFaceTransform);
    }

    public StructureBlueprint getStructureBlueprint() {
        return structureBlueprint;
    }

    public int getBlocksPlacedIndex() {
        return blocksPlacedIndex;
    }

}
