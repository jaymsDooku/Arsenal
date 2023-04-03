package dev.jayms.arsenal.artillery.impl.trebuchet;

import dev.jayms.arsenal.artillery.ArtilleryMissileState;
import dev.jayms.arsenal.artillery.impl.MissileUtils;
import dev.jayms.arsenal.util.AffineTransform;
import dev.jayms.arsenal.util.MathUtils;
import dev.jayms.arsenal.util.ParticleTools;
import dev.jayms.arsenal.util.RotAxis;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class TrebuchetMissileLaunchState implements ArtilleryMissileState<TrebuchetMissile> {

    @Override
    public ArtilleryMissileState<TrebuchetMissile> update(TrebuchetMissile missile) {
        Trebuchet trebuchet = missile.getArtillery();
        BlockFace direction = trebuchet.getDirection();
        RotAxis axis = RotAxis.getAxis(direction);
        boolean negative = direction == BlockFace.NORTH || direction == BlockFace.EAST;

        ArtilleryMissileState<TrebuchetMissile> result = this;
        Block middle = trebuchet.getMiddle();
        Location middleLoc = middle.getLocation();

        Location loc = missile.getLocation();
        double rotation = missile.getRotation();
        missile.setPrevLocation(loc);

        // rotate start
        Vector vDir = loc.clone().toVector().subtract(middleLoc.toVector());

        double dRotate = 6;
        double rotate = negative ? -dRotate : dRotate;

        if (axis == RotAxis.X) {
            vDir = new AffineTransform().rotateX(rotate).apply(vDir);
        } else if (axis == RotAxis.Z) {
            vDir = new AffineTransform().rotateZ(rotate).apply(vDir);
        }

        rotation = negative ? rotation - dRotate : rotation + dRotate;
        missile.setRotation(rotation);

        loc = middleLoc.clone().add(vDir.normalize().multiply(10));
        // rotate end

        missile.setLocation(loc);

        FallingBlock missileBlock = missile.getMissileBlock();
        Vector velocity = loc.clone().toVector().subtract(missileBlock.getLocation().toVector());
        missileBlock.setVelocity(velocity);
        loc.getWorld().playSound(loc, Sound.ENTITY_ARROW_SHOOT, 0.75f, 0.2f);
        missileBlock.getWorld().spawnParticle(Particle.FLAME, missileBlock.getLocation(), 15, 0.5f, 0.5f, 0.5f, 0, null, true);
        missileBlock.getWorld().spawnParticle(Particle.SMOKE_NORMAL, missileBlock.getLocation(), 7, 0.5f, 0.5f, 0.5f, 0, null, true);
        missileBlock.getWorld().spawnParticle(Particle.SMOKE_LARGE, missileBlock.getLocation(), 2, 0.5f, 0.5f, 0.5f, 0, null, true);

        ParticleTools.drawLine(middle.getLocation(), missileBlock.getLocation(), 20, (l) -> {
            ParticleTools.displayColoredParticle(l, "#855F08");
        });

        if (negative) {
            if (rotation < -trebuchet.getFiringAngleThreshold()) {
                result = TrebuchetMissile.PROGRESS;
            }
        } else {
            if (rotation > trebuchet.getFiringAngleThreshold()) {
                result = TrebuchetMissile.PROGRESS;
            }
        }

        if (result == TrebuchetMissile.PROGRESS) {
            Location target = missile.getTarget();
            if (target != null) {
                missile.setPath(MissileUtils.computePath(loc, target));
                missile.setIndex(0);
            } else {
                Vector dir = missile.getLocation().getDirection();
                Location prevLoc = missile.getPrevLocation();
                if (prevLoc != null) {
                    dir = missile.getLocation().clone().toVector().subtract(missile.getPrevLocation().toVector());
                }
                dir = new AffineTransform().rotateY(missile.getArtillery().gethAngle()).apply(dir);
                missileBlock.setVelocity(dir.multiply(missile.getArtillery().getFiringPower()));
            }
        }

        return result;
    }

}

