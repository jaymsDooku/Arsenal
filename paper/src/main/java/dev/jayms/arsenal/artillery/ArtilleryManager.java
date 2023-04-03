package dev.jayms.arsenal.artillery;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import vg.civcraft.mc.civmodcore.world.locations.SparseQuadTree;
import vg.civcraft.mc.civmodcore.world.locations.chunkmeta.api.BlockBasedChunkMetaView;
import vg.civcraft.mc.civmodcore.world.locations.chunkmeta.block.table.TableBasedDataObject;
import vg.civcraft.mc.civmodcore.world.locations.chunkmeta.block.table.TableStorageEngine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ArtilleryManager {

    private Map<World, SparseQuadTree<Artillery>> artilleries = new HashMap<>();
    private Map<World, SparseQuadTree<ArtilleryDefenceRange>> artilleryDefences = new HashMap<>();
    private BlockBasedChunkMetaView<ArtilleryChunkData, TableBasedDataObject, TableStorageEngine<Artillery>> chunkMetaData;

    public ArtilleryManager(BlockBasedChunkMetaView<ArtilleryChunkData, TableBasedDataObject, TableStorageEngine<Artillery>> chunkMetaData) {
        this.chunkMetaData = chunkMetaData;
    }

    public Set<Artillery> forLocation(Location loc) {
        SparseQuadTree<Artillery> sparseQuadTree = artilleries.get(loc.getWorld());
        if (sparseQuadTree == null) {
            return new HashSet<>();
        }
        Set<Artillery> found = sparseQuadTree.find(loc.getBlockX(), loc.getBlockZ());
        Set<Artillery> withinYLevel = found.stream().filter(a -> a.withinYLevel(loc))
                .collect(Collectors.toSet());
        return withinYLevel;
    }

    public Set<Artillery> defencesForLocation(Location loc) {
        SparseQuadTree<ArtilleryDefenceRange> sparseQuadTree = artilleryDefences.get(loc.getWorld());
        if (sparseQuadTree == null) {
            return new HashSet<>();
        }
        return sparseQuadTree.find(loc.getBlockX(), loc.getBlockZ())
                .stream().map(ad -> ad.getArtillery())
                .collect(Collectors.toSet());
    }

    public void putArtilleryDefence(Artillery artillery) {
        SparseQuadTree<ArtilleryDefenceRange> artilleryDefenceRangeSparseQuadTree = artilleryDefences.get(artillery.getWorld());
        if (artilleryDefenceRangeSparseQuadTree == null) {
            artilleryDefenceRangeSparseQuadTree = new SparseQuadTree<>();
        }
        artilleryDefenceRangeSparseQuadTree.add(artillery.getDefenceRange());
        artilleryDefences.put(artillery.getWorld(), artilleryDefenceRangeSparseQuadTree);
    }

    public void removeArtilleryDefence(Artillery artillery) {
        SparseQuadTree<ArtilleryDefenceRange> artilleryDefenceRangeSparseQuadTree = artilleryDefences.get(artillery.getWorld());
        artilleryDefenceRangeSparseQuadTree.remove(artillery.getDefenceRange());
    }

    public Artillery getArtillery(Location location) {
        if (location.getY() < location.getWorld().getMinHeight()) {
            return null;
        }
        return (Artillery) chunkMetaData.get(location);
    }

    public Artillery getArtillery(Block block) {
        return getArtillery(block.getLocation());
    }

    public void putArtillery(Artillery artillery) {
        chunkMetaData.put(artillery);
        trackArtillery(artillery);
    }

    public void destroyArtillery(Artillery artillery) {
        if (artillery.isDefenceOn()) {
            artillery.toggleDefence();
        }
        chunkMetaData.remove(artillery);
        untrackArtillery(artillery);
    }

    public void trackArtillery(Artillery artillery) {
        SparseQuadTree<Artillery> sparseQuadTree = artilleries.get(artillery.getWorld());
        if (sparseQuadTree == null) {
            sparseQuadTree = new SparseQuadTree<>();
        }
        sparseQuadTree.add(artillery);
        artilleries.put(artillery.getWorld(), sparseQuadTree);
    }

    public void untrackArtillery(Artillery artillery) {
        if (!artilleries.containsKey(artillery.getWorld())) {
            return;
        }
        SparseQuadTree<Artillery> sparseQuadTree = artilleries.get(artillery.getWorld());
        sparseQuadTree.remove(artillery);
        artilleries.put(artillery.getWorld(), sparseQuadTree);
    }

    public void shutDown() {
        chunkMetaData.disable();
    }

}
