package dev.jayms.arsenal.artillery.impl;

import dev.jayms.arsenal.artillery.Artillery;
import dev.jayms.arsenal.artillery.ArtilleryMissile;
import dev.jayms.arsenal.artillery.impl.trebuchet.Trebuchet;
import dev.jayms.arsenal.artillery.shooter.Shooter;
import org.bukkit.Location;

import java.util.List;

public class GenericMissile<T extends Artillery> extends ArtilleryMissile<T> {

    private Location location;

    private List<Location> path;
    private int index;

    protected GenericMissile(Shooter shooter, T artillery, Location target) {
        super(shooter, artillery, target);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Location> getPath() {
        return path;
    }

    public void setPath(List<Location> path) {
        this.path = path;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
