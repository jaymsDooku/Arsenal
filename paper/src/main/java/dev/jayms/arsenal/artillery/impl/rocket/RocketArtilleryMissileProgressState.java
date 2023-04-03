package dev.jayms.arsenal.artillery.impl.rocket;

import dev.jayms.arsenal.artillery.ArtilleryMissileState;
import dev.jayms.arsenal.artillery.impl.field.FieldArtilleryMissile;
import dev.jayms.arsenal.util.ParticleTools;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import java.util.List;

public class RocketArtilleryMissileProgressState implements ArtilleryMissileState<RocketArtilleryMissile> {

    @Override
    public ArtilleryMissileState<RocketArtilleryMissile> update(RocketArtilleryMissile missile) {
        Location location = missile.getLocation();
        Block block = location.getBlock();

        if (!block.isPassable()) {
            return RocketArtilleryMissile.FINISH;
        }

        location.getWorld().playSound(location, Sound.ENTITY_CREEPER_PRIMED, 1f, 0.2f);
        location.getWorld().playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 0.2f);

        List<Location> path = missile.getPath();
        if (path != null && !path.isEmpty()) {
            int index = missile.getIndex();
            if (index < path.size()) {
                Location pathLoc = path.get(index);
                Vector dir = pathLoc.clone().subtract(location).toVector().normalize();
                Vector oppDir = dir.clone().multiply(-4);
                Location tailLoc = pathLoc.clone().add(oppDir);

                ParticleTools.drawLine(pathLoc, tailLoc, 15, (loc) -> {
                    loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 3, 0.3f, 0.3f, 0.3f, 0, null, true);
                    loc.getWorld().spawnParticle(Particle.FLAME, loc, 3, 0.3f, 0.3f, 0.3f, 0, null, true);
                });

                tailLoc.getWorld().spawnParticle(Particle.SMOKE_LARGE, tailLoc, 2, 0.5f, 0.5f, 0.5f, 0, null, true);
                tailLoc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, tailLoc, 4, 0.5f, 0.5f, 0.5f, 0, null, true);
                tailLoc.getWorld().spawnParticle(Particle.CLOUD, tailLoc, 8, 0.5f, 0.5f, 0.5f, 0, null, true);

                missile.setLocation(pathLoc);
                missile.setIndex(index + 1);
            } else {
                return RocketArtilleryMissile.FINISH;
            }
        } else {
            Vector direction = missile.getDirection();
            Location newLocation = location.clone().add(direction);
            Vector oppDir = direction.clone().multiply(-4);
            Location tailLoc = newLocation.clone().add(oppDir);

            ParticleTools.drawLine(newLocation, tailLoc, 10, (loc) -> {
                loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 3, 0.3f, 0.3f, 0.3f, 0, null, true);
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 3, 0.3f, 0.3f, 0.3f, 0, null, true);
            });

            tailLoc.getWorld().spawnParticle(Particle.SMOKE_LARGE, tailLoc, 2, 0.5f, 0.5f, 0.5f, 0, null, true);
            tailLoc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, tailLoc, 4, 0.5f, 0.5f, 0.5f, 0, null, true);
            tailLoc.getWorld().spawnParticle(Particle.CLOUD, tailLoc, 8, 0.5f, 0.5f, 0.5f, 0, null, true);

            missile.setLocation(newLocation);
            missile.setDirection(direction.clone().subtract(new Vector(0, 0.01, 0)).normalize());
        }
        return this;
    }

}
