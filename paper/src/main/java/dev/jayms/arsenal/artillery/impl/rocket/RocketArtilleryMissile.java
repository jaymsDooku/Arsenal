package dev.jayms.arsenal.artillery.impl.rocket;

import dev.jayms.arsenal.Arsenal;
import dev.jayms.arsenal.artillery.ArtilleryMissileState;
import dev.jayms.arsenal.artillery.impl.GenericMissile;
import dev.jayms.arsenal.artillery.impl.field.*;
import dev.jayms.arsenal.artillery.shooter.Shooter;
import dev.jayms.arsenal.util.AffineTransform;
import dev.jayms.arsenal.util.RotAxis;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class RocketArtilleryMissile extends GenericMissile<RocketArtillery> {

    public static final ArtilleryMissileState<RocketArtilleryMissile> LAUNCH = new RocketArtilleryMissileLaunchState();
    public static final ArtilleryMissileState<RocketArtilleryMissile> PROGRESS = new RocketArtilleryMissileProgressState();
    public static final ArtilleryMissileState<RocketArtilleryMissile> FINISH = new RocketArtilleryMissileFinishState();

    private RocketArtillery.MissileBody missileBody;
    private Vector direction;

    public RocketArtilleryMissile(Shooter shooter, RocketArtillery artillery, Location target) {
        super(shooter, artillery, target);

        this.missileBody = artillery.getNextMissileBody();

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
        this.direction = direction.normalize();

        Location location = missileBody.getMissileHead().getLocation().clone().add(0, 1, 0).add(direction);
        setLocation(location);

        setState(LAUNCH);
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    public Vector getDirection() {
        return direction;
    }

    public RocketArtillery.MissileBody getMissileBody() {
        return missileBody;
    }
}
