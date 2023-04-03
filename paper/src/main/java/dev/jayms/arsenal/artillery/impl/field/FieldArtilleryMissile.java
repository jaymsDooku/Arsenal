package dev.jayms.arsenal.artillery.impl.field;

import dev.jayms.arsenal.Arsenal;
import dev.jayms.arsenal.artillery.ArtilleryMissile;
import dev.jayms.arsenal.artillery.ArtilleryMissileState;
import dev.jayms.arsenal.artillery.impl.GenericMissile;
import dev.jayms.arsenal.artillery.impl.trebuchet.TrebuchetMissile;
import dev.jayms.arsenal.artillery.impl.trebuchet.TrebuchetMissileFinishState;
import dev.jayms.arsenal.artillery.impl.trebuchet.TrebuchetMissileLaunchState;
import dev.jayms.arsenal.artillery.impl.trebuchet.TrebuchetMissileProgressState;
import dev.jayms.arsenal.artillery.shooter.Shooter;
import dev.jayms.arsenal.util.AffineTransform;
import dev.jayms.arsenal.util.RotAxis;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class FieldArtilleryMissile extends GenericMissile<FieldArtillery> implements Listener {

    public static final ArtilleryMissileState<FieldArtilleryMissile> LAUNCH = new FieldArtilleryMissileLaunchState();
    public static final ArtilleryMissileState<FieldArtilleryMissile> PROGRESS = new FieldArtilleryMissileProgressState();
    public static final ArtilleryMissileState<FieldArtilleryMissile> FINISH = new FieldArtilleryMissileFinishState();

    private Vector direction;
    private FallingBlock missileBlock;

    public FieldArtilleryMissile(Shooter shooter, FieldArtillery artillery, Location target) {
        super(shooter, artillery, target);

        BlockFace directionFace = artillery.getDirection();
        RotAxis axis = RotAxis.getAxis(directionFace);

        Vector direction = directionFace.getDirection();
        direction = new AffineTransform().rotateY(artillery.gethAngle()).apply(direction);

        double vAngle = artillery.getvAngle();
        if (axis == RotAxis.X) {
            direction = new AffineTransform().rotateX(vAngle).apply(direction);
        } else if (axis == RotAxis.Z) {
            direction = new AffineTransform().rotateZ(vAngle).apply(direction);
        }
        this.direction = direction;

        Location location = artillery.getMuzzle().getLocation().clone().add(direction);

        setLocation(location);

        missileBlock = location.getWorld().spawnFallingBlock(location, Material.STONE, (byte) 1);
        missileBlock.setDropItem(false);

        setState(LAUNCH);
        Bukkit.getPluginManager().registerEvents(this, Arsenal.getInstance());
    }

    public Vector getDirection() {
        return direction;
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
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
