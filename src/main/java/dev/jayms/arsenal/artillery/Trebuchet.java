package dev.jayms.arsenal.artillery;

import dev.jayms.arsenal.structure.StructureBlueprint;
import dev.jayms.arsenal.structure.StructureInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Trebuchet extends Artillery {

    private String displayName;
    private Block middle;

    public Trebuchet(ArtilleryCore core, BlockFace blockFace, StructureBlueprint structureBlueprint, long creationTime, boolean isNew) {
        super(core, blockFace, structureBlueprint, creationTime, isNew);
    }

    public Trebuchet(ArtilleryCore core, BlockFace blockFace, StructureInstance structureInstance, long creationTime) {
        super(core, blockFace, structureInstance, creationTime);
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public ArtilleryCategory getCategory() {
        return ArtilleryCategory.TREBUCHET;
    }


}
