package dev.jayms.arsenal.artillery.impl.rocket;

import dev.jayms.arsenal.artillery.ArtilleryMissileState;
import dev.jayms.arsenal.artillery.impl.MissileUtils;
import dev.jayms.arsenal.artillery.impl.field.FieldArtillery;
import dev.jayms.arsenal.artillery.impl.field.FieldArtilleryMissile;
import dev.jayms.arsenal.util.ParticleTools;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

public class RocketArtilleryMissileLaunchState implements ArtilleryMissileState<RocketArtilleryMissile> {

    @Override
    public ArtilleryMissileState<RocketArtilleryMissile> update(RocketArtilleryMissile missile) {
        RocketArtillery rocketArtillery = missile.getArtillery();

        RocketArtillery.MissileBody missileBody = missile.getMissileBody();
        Block missileHead = missileBody.getMissileHead();
        Block missileTail = missileBody.getMissileTail();

        Location missileHeadLoc = missileHead.getLocation().add(0, 1, 0);
        Location missileTailLoc = missileTail.getLocation().add(0, 1, 0);

        ParticleTools.drawLine(missileHeadLoc, missileTailLoc, 15, (loc) -> {
            loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 3, 0.3f, 0.3f, 0.3f, 0, null, true);
            loc.getWorld().spawnParticle(Particle.FLAME, loc, 3, 0.3f, 0.3f, 0.3f, 0, null, true);
        });

        missileTailLoc.getWorld().spawnParticle(Particle.SMOKE_LARGE, missileTailLoc, 2, 0.5f, 0.5f, 0.5f, 0, null, true);
        missileTailLoc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, missileTailLoc, 4, 0.5f, 0.5f, 0.5f, 0, null, true);
        missileTailLoc.getWorld().spawnParticle(Particle.CLOUD, missileTailLoc, 8, 0.5f, 0.5f, 0.5f, 0, null, true);

        missileTailLoc.getWorld().playSound(missileTailLoc, Sound.ENTITY_CREEPER_PRIMED, 1f, 0.2f);
        missileTailLoc.getWorld().playSound(missileTailLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 0.2f);

        Location target = missile.getTarget();
        if (target != null) {
            missile.setPath(MissileUtils.computePath(missileHeadLoc, target));
            missile.setIndex(0);
        }

        return RocketArtilleryMissile.PROGRESS;
    }

}
