package dev.jayms.arsenal.artillery;

import dev.jayms.arsenal.artillery.impl.field.FieldArtilleryMissileRunner;
import dev.jayms.arsenal.artillery.impl.rocket.RocketArtilleryMissileRunner;
import dev.jayms.arsenal.artillery.impl.trebuchet.TrebuchetMissileRunner;
import org.bukkit.scheduler.BukkitRunnable;

public class ArtilleryMissileRunnable extends BukkitRunnable {

    @Override
    public void run() {
        FieldArtilleryMissileRunner.FIELD_ARTILLERY_MISSILE_RUNNER.update();
        TrebuchetMissileRunner.TREBUCHET_MISSILE_RUNNER.update();
        RocketArtilleryMissileRunner.ROCKET_ARTILLERY_MISSILE_RUNNER.update();
    }

}
