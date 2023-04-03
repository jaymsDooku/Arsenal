package dev.jayms.arsenal.artillery.shooter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import vg.civcraft.mc.citadel.events.ReinforcementDestructionEvent;
import vg.civcraft.mc.citadel.model.Reinforcement;

public class AutoShooter implements Shooter {

    @Override
    public String getName() {
        return "AutoShooter";
    }

    @Override
    public void sendMessage(String message) {
    }

    public static void damageReinforcement(Reinforcement rein, float damage, Entity source) {
        float futureHealth = rein.getHealth() - damage;
        if (futureHealth <= 0) {
            ReinforcementDestructionEvent event = new ReinforcementDestructionEvent(rein, damage, source);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
        }
        futureHealth = Math.min(futureHealth, rein.getType().getHealth());
        rein.setHealth(futureHealth);
        if (rein.isBroken()) {
            if (rein.getType().getDestructionEffect() != null) {
                rein.getType().getDestructionEffect().playEffect(rein);
            }
        } else {
            if (rein.getType().getDamageEffect() != null) {
                rein.getType().getDamageEffect().playEffect(rein);
            }
        }
    }
}
