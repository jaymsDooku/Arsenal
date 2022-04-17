package dev.jayms.arsenal.artillery;

import vg.civcraft.mc.civmodcore.world.locations.chunkmeta.block.table.TableBasedBlockChunkMeta;

public class ArtilleryChunkData extends TableBasedBlockChunkMeta<Artillery> {

    public ArtilleryChunkData(boolean isNew, ArtilleryDAO storage) {
        super(isNew, storage);
    }

}
