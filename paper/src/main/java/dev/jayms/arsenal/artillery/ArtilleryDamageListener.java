package dev.jayms.arsenal.artillery;

import dev.jayms.arsenal.Arsenal;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ArtilleryDamageListener implements Listener {

    private boolean damageArtilleryAtLocation(Location loc) {
        ArtilleryManager artilleryManager = Arsenal.getInstance().getArtilleryManager();
        Set<Artillery> artilleries = artilleryManager.forLocation(loc);
        if (!artilleries.isEmpty()) {
            for (Artillery artill : artilleries) {
                artill.damage(1);
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ArtilleryManager artilleryManager = Arsenal.getInstance().getArtilleryManager();
        Artillery artillery = artilleryManager.getArtillery(block);
        if (artillery != null) {
            if (artillery.getCore().canBreak(player)) {
                artillery.startDisassembly(player);
            } else {
                event.setCancelled(true);
                artillery.damage(1);
            }
            return;
        }

        event.setCancelled(damageArtilleryAtLocation(block.getLocation()));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        event.setCancelled(damageArtilleryAtLocation(block.getLocation()));
    }

    private void protectFromExplosion(Iterator<Block> iterator) {
        ArtilleryManager artilleryManager = Arsenal.getInstance().getArtilleryManager();
        while (iterator.hasNext()) {
            Block block = iterator.next();
            Set<Artillery> artilleries = artilleryManager.forLocation(block.getLocation());
            if (!artilleries.isEmpty()) {
                iterator.remove();
            }
        }
    }

    private boolean affectsArtillery(List<Block> blocks) {
        ArtilleryManager artilleryManager = Arsenal.getInstance().getArtilleryManager();
        for (Block block : blocks) {
            if (artilleryManager.forLocation(block.getLocation()).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Iterator<Block> iterator = event.blockList().iterator();
        protectFromExplosion(iterator);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        Iterator<Block> iterator = event.blockList().iterator();
        protectFromExplosion(iterator);
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        event.setCancelled(affectsArtillery(event.getBlocks()));
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        event.setCancelled(affectsArtillery(event.getBlocks()));
    }
}
