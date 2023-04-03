package dev.jayms.arsenal.artillery.impl.field;

import dev.jayms.arsenal.artillery.ArtilleryMissileState;
import dev.jayms.arsenal.artillery.impl.trebuchet.TrebuchetMissile;
import dev.jayms.arsenal.util.AffineTransform;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class FieldArtilleryMissileProgressState implements ArtilleryMissileState<FieldArtilleryMissile> {

    @Override
    public ArtilleryMissileState<FieldArtilleryMissile> update(FieldArtilleryMissile missile) {
        FallingBlock missileBlock = missile.getMissileBlock();

        if (missileBlock == null) {
            return FieldArtilleryMissile.FINISH;
        }

        Location blockLocation = missileBlock.getLocation();
        Location loc = missile.getLocation();

        if (Math.abs(loc.getX() - blockLocation.getX()) < 0.00001
                && Math.abs(loc.getZ() - blockLocation.getZ()) < 0.00001) {
            missileBlock.remove();
            missile.setMissileBlock(null);
            return FieldArtilleryMissile.FINISH;
        }

        List<Location> path = missile.getPath();
        if (path != null && !path.isEmpty()) {
            int index = missile.getIndex();
            if (index < path.size()) {
                Location pathLoc = path.get(index);
                Vector dir = pathLoc.clone().subtract(blockLocation).toVector();
                missileBlock.setVelocity(dir);
                missile.setIndex(index + 1);
            }
        }

        blockLocation.getWorld().playSound(blockLocation, Sound.ENTITY_ARROW_SHOOT, 0.75f, 0.2f);
        missileBlock.getWorld().spawnParticle(Particle.FLAME, missileBlock.getLocation(), 15, 0.5f, 0.5f, 0.5f, 0, null, true);
        missileBlock.getWorld().spawnParticle(Particle.SMOKE_NORMAL, missileBlock.getLocation(), 7, 0.5f, 0.5f, 0.5f, 0, null, true);
        missileBlock.getWorld().spawnParticle(Particle.SMOKE_LARGE, missileBlock.getLocation(), 2, 0.5f, 0.5f, 0.5f, 0, null, true);
        missile.setLocation(blockLocation);
        return this;
    }

}
