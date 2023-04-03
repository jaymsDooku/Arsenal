package dev.jayms.arsenal.artillery.impl;

import dev.jayms.arsenal.Arsenal;
import dev.jayms.arsenal.artillery.Artillery;
import dev.jayms.arsenal.artillery.ArtilleryManager;
import dev.jayms.arsenal.artillery.ArtilleryMissile;
import dev.jayms.arsenal.artillery.ArtilleryMissileState;
import dev.jayms.arsenal.artillery.event.MissileImpactEvent;
import dev.jayms.arsenal.artillery.impl.trebuchet.Trebuchet;
import dev.jayms.arsenal.artillery.impl.trebuchet.TrebuchetMissile;
import dev.jayms.arsenal.artillery.shooter.PlayerShooter;
import dev.jayms.arsenal.artillery.shooter.Shooter;
import dev.jayms.arsenal.util.LocationTools;
import isaac.bastion.Bastion;
import isaac.bastion.BastionBlock;
import isaac.bastion.manager.BastionBlockManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import vg.civcraft.mc.citadel.ReinforcementLogic;
import vg.civcraft.mc.citadel.model.Reinforcement;

import java.util.*;

public class GenericMissileFinishState<T extends GenericMissile> implements ArtilleryMissileState<T> {

    @Override
    public boolean isAcceptingState() {
        return true;
    }

    @Override
    public ArtilleryMissileState<T> update(GenericMissile missile) {
        Location loc = missile.getLocation();
        Shooter shooter = missile.getShooter();

        BastionBlockManager bastionBlockManager = Bastion.getBastionManager();
        Set<BastionBlock> bastions = bastionBlockManager.getBlockingBastions(loc);
        if (!bastions.isEmpty()) {
            if (shooter instanceof PlayerShooter) {
                bastionBlockManager.erodeFromPlace(((PlayerShooter) shooter).getPlayer(), bastions);
            } else {
                for (BastionBlock bastionBlock : bastions) {
                    bastionBlock.erode(bastionBlock.getErosionFromBlock());
                }
            }
        }

        Random random = new Random();
        Artillery artillery = missile.getArtillery();
        int impactRadius = artillery.getImpactRadius();
        List<Location> explodeLocs = LocationTools.getCircle(loc, impactRadius, impactRadius, false, true, 0);
        List<Block> explodeBlocks = new ArrayList<>();
        Map<Artillery, Float> artilleryDamages = new HashMap<>();
        for (Location explodeLoc : explodeLocs) {
            if (explodeLoc.getY() < -64) continue;

            Block explodeBlock = explodeLoc.getBlock();
            if (explodeBlock.getType() != Material.AIR && explodeBlock.getType() != Material.BEDROCK && explodeBlock.getType() != Material.BARRIER) {
                ArtilleryManager artilleryManager = Arsenal.getInstance().getArtilleryManager();
                Set<Artillery> victimArtilleries = artilleryManager.forLocation(explodeLoc);
                if (!victimArtilleries.isEmpty()) {
                    for (Artillery victimArtillery : victimArtilleries) {
                        artilleryDamages.merge(victimArtillery, artillery.getArtilleryDamage(artillery.getType().getKey()).floatValue(), Float::sum);
                    }
                    continue;
                }
                Reinforcement rein = ReinforcementLogic.getReinforcementProtecting(explodeBlock);
                if (rein != null) {
                    int reinforcementDamage = artillery.getReinforcementDamage();
                    for (int i = 0; i < reinforcementDamage; i++) {
                        if (rein.isBroken()) {
                            break;
                        }
                        float damage = ReinforcementLogic.getDamageApplied(rein);
                        if (shooter instanceof PlayerShooter) {
                            ReinforcementLogic.damageReinforcement(rein, damage, ((PlayerShooter) shooter).getPlayer());
                        } else {
                            ReinforcementLogic.damageReinforcement(rein, damage, null);
                        }
                    }
                } else {
                    Material type = Material.AIR;

                    if (explodeBlock.getRelative(BlockFace.DOWN, 1).getType().isSolid() && random.nextInt(3) == 0) {
                        type = Material.FIRE;
                    }

                    explodeBlock.setType(type);
                    explodeBlocks.add(explodeBlock);
                }
            }
        }

        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 8.0f, 0.5f);
        loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 5, 0f, 0f, 0f, 0, null, true);
        loc.getWorld().spawnParticle(Particle.FLAME, loc, 10, 0.2f, 0.2f, 0.2f, 0, null, true);
        loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 5, 0.3f, 0.3f, 0.3f, 0, null, true);

        for (Map.Entry<Artillery, Float> artilleryDamageEntry : artilleryDamages.entrySet()) {
            Artillery victimArtillery = artilleryDamageEntry.getKey();
            float damage = artilleryDamageEntry.getValue();
            victimArtillery.damage(damage);
            Bukkit.broadcastMessage("Artillery@" + artillery.getLocation().toVector() + " has dealt " + damage + " to Artillery@" + victimArtillery.getLocation().toVector());
        }

        Collection<LivingEntity> livingEntities = LocationTools.getNearbyLivingEntities(loc, impactRadius);
        for (LivingEntity le : livingEntities) {
            org.bukkit.util.Vector dir = loc.clone().subtract(le.getEyeLocation()).toVector().normalize().multiply(-1);
            dir = dir.add(new org.bukkit.util.Vector(0, artillery.getVerticalKb(), 0));
            dir = dir.multiply(new Vector(artillery.getHorizontalKb(), 0, artillery.getHorizontalKb()));
            le.setVelocity(dir);
            le.damage(artillery.getPlayerDamage());
        }

        MissileImpactEvent missileImpactEvent = new MissileImpactEvent(missile, explodeLocs, explodeBlocks);
        Bukkit.getPluginManager().callEvent(missileImpactEvent);
        return this;
    }
}
