package dev.jayms.arsenal.artillery;

import org.bukkit.Location;
import org.bukkit.block.Block;
import vg.civcraft.mc.civmodcore.world.locations.chunkmeta.api.BlockBasedChunkMetaView;
import vg.civcraft.mc.civmodcore.world.locations.chunkmeta.block.table.TableBasedDataObject;
import vg.civcraft.mc.civmodcore.world.locations.chunkmeta.block.table.TableStorageEngine;

public class ArtilleryManager {

    private BlockBasedChunkMetaView<ArtilleryChunkData, TableBasedDataObject, TableStorageEngine<Artillery>> chunkMetaData;

    public ArtilleryManager(BlockBasedChunkMetaView<ArtilleryChunkData, TableBasedDataObject, TableStorageEngine<Artillery>> chunkMetaData) {
        this.chunkMetaData = chunkMetaData;
    }

    public Artillery getArtillery(Location location) {
        return (Artillery) chunkMetaData.get(location);
    }

    public Artillery getArtillery(Block block) {
        return getArtillery(block.getLocation());
    }

    public void putArtillery(Artillery artillery) {
        chunkMetaData.put(artillery);
    }

    public void shutDown() {
        chunkMetaData.disable();
    }

}
