package dev.jayms.arsenal.artillery.impl.field;

import dev.jayms.arsenal.artillery.ArtilleryMissileState;
import dev.jayms.arsenal.artillery.impl.MissileUtils;
import dev.jayms.arsenal.artillery.impl.trebuchet.TrebuchetMissile;
import dev.jayms.arsenal.util.AffineTransform;
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

public class FieldArtilleryMissileLaunchState implements ArtilleryMissileState<FieldArtilleryMissile> {

    @Override
    public ArtilleryMissileState<FieldArtilleryMissile> update(FieldArtilleryMissile missile) {
        FieldArtillery fieldArtillery = missile.getArtillery();

        Block recuperator = fieldArtillery.getRecuperator();
        Location recuperatorLoc = recuperator.getLocation();
        recuperatorLoc.getWorld().playSound(recuperatorLoc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.2f);
        recuperatorLoc.getWorld().spawnParticle(Particle.FLAME, recuperatorLoc, 15, 0.5f, 0.5f, 0.5f, 0, null, true);
        recuperatorLoc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, recuperatorLoc, 7, 0.5f, 0.5f, 0.5f, 0, null, true);
        recuperatorLoc.getWorld().spawnParticle(Particle.CLOUD, recuperatorLoc, 7, 0.5f, 0.5f, 0.5f, 0, null, true);

        Location loc = missile.getLocation();
        loc.getWorld().spawnParticle(Particle.FLAME, loc, 15, 0.5f, 0.5f, 0.5f, 0, null, true);
        loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 7, 0.5f, 0.5f, 0.5f, 0, null, true);
        loc.getWorld().spawnParticle(Particle.CLOUD, loc, 7, 0.5f, 0.5f, 0.5f, 0, null, true);

        Location target = missile.getTarget();
        if (target != null) {
            missile.setPath(MissileUtils.computePath(loc, target));
            missile.setIndex(0);
        } else {
            Vector dir = missile.getDirection();
            FallingBlock missileBlock = missile.getMissileBlock();
            missileBlock.setVelocity(dir.multiply(missile.getArtillery().getFiringPower()));
        }

        return FieldArtilleryMissile.PROGRESS;
    }

}
