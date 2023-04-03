package dev.jayms.arsenal.artillery;

import dev.jayms.arsenal.Arsenal;
import dev.jayms.arsenal.artillery.event.MissileImpactEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

public class ArtilleryDefenceListener implements Listener {

    @EventHandler
    public void onMissileImpact(MissileImpactEvent event) {
        ArtilleryMissile missile = event.getMissile();

        ArtilleryManager artilleryManager = Arsenal.getInstance().getArtilleryManager();
        for (Location loc : event.getExplodeLocations()) {
            Set<Artillery> defendingArtillery = artilleryManager.defencesForLocation(loc);
            if (defendingArtillery.isEmpty()) {
                continue;
            }
            for (Artillery artillery : defendingArtillery) {
                artillery.respondToMissile(missile);
            }
        }
    }

}
