package dev.jayms.arsenal.artillery.event;

import dev.jayms.arsenal.artillery.ArtilleryMissile;
import dev.jayms.arsenal.artillery.shooter.Shooter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class MissileImpactEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final ArtilleryMissile missile;
    private Shooter shooter;
    private List<Location> explodeLocations;
    private List<Block> explodeBlocks;

    public MissileImpactEvent(ArtilleryMissile missile, Shooter shooter, List<Location> explodeLocations, List<Block> explodeBlocks) {
        this.missile = missile;
        this.shooter = shooter;
        this.explodeLocations = explodeLocations;
        this.explodeBlocks = explodeBlocks;
    }

    public ArtilleryMissile getMissile() {
        return missile;
    }

    public Shooter getShooter() {
        return shooter;
    }

    public List<Location> getExplodeLocations() {
        return explodeLocations;
    }

    public List<Block> getExplodeBlocks() {
        return explodeBlocks;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
