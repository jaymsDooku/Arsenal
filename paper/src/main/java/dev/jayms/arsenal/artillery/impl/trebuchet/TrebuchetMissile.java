package dev.jayms.arsenal.artillery.impl.trebuchet;

import dev.jayms.arsenal.Arsenal;
import dev.jayms.arsenal.artillery.ArtilleryMissile;
import dev.jayms.arsenal.artillery.ArtilleryMissileState;
import dev.jayms.arsenal.artillery.impl.GenericMissile;
import dev.jayms.arsenal.artillery.shooter.Shooter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.List;

public class TrebuchetMissile extends GenericMissile<Trebuchet> implements Listener {

    public static final ArtilleryMissileState<TrebuchetMissile> LAUNCH = new TrebuchetMissileLaunchState();
    public static final ArtilleryMissileState<TrebuchetMissile> PROGRESS = new TrebuchetMissileProgressState();
    public static final ArtilleryMissileState<TrebuchetMissile> FINISH = new TrebuchetMissileFinishState();

    private FallingBlock missileBlock;
    private Location lowStart;
    private Location prevLocation;
    private double rotation;

    public TrebuchetMissile(Shooter shooter, Trebuchet artillery, Location target) {
        super(shooter, artillery, target);

        this.lowStart = artillery.getMiddle().getLocation().clone().subtract(0, 12, 0);
        Location location = lowStart;

        switch (artillery.getDirection()) {
            case NORTH:
                location = location.add(0, 0, -7);
                break;
            case EAST:
                location = location.add(7, 0, 0);
                break;
            case SOUTH:
                location = location.add(0, 0, 7);
                break;
            case WEST:
                location = location.add(-7, 0, 0);
                break;
        }

        setLocation(location);

        missileBlock = location.getWorld().spawnFallingBlock(location, Material.STONE, (byte) 1);
        missileBlock.setDropItem(false);

        setState(LAUNCH);
        Bukkit.getPluginManager().registerEvents(this, Arsenal.getInstance());
    }

    public Location getLowStart() {
        return lowStart;
    }

    public void setLowStart(Location lowStart) {
        this.lowStart = lowStart;
    }

    public Location getPrevLocation() {
        return prevLocation;
    }

    public void setPrevLocation(Location prevLocation) {
        this.prevLocation = prevLocation;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public FallingBlock getMissileBlock() {
        return missileBlock;
    }

    public void setMissileBlock(FallingBlock missileBlock) {
        this.missileBlock = missileBlock;
    }

    @EventHandler
    public void onMissileLand(EntityChangeBlockEvent event) {
        if (getMissileBlock() == null) {
            return;
        }
        if (getMissileBlock().getUniqueId().equals(event.getEntity().getUniqueId())) {
            setMissileBlock(null);
            setState(FINISH);
            event.setCancelled(true);
        }
    }

}
