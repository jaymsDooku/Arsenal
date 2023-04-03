package dev.jayms.arsenal.artillery.impl.rocket;

import dev.jayms.arsenal.artillery.Artillery;
import dev.jayms.arsenal.artillery.ArtilleryMissileRunner;
import dev.jayms.arsenal.artillery.impl.field.FieldArtillery;
import dev.jayms.arsenal.artillery.impl.field.FieldArtilleryMissile;
import dev.jayms.arsenal.artillery.impl.field.FieldArtilleryMissileRunner;
import dev.jayms.arsenal.artillery.shooter.Shooter;
import org.bukkit.Location;

public class RocketArtilleryMissileRunner extends ArtilleryMissileRunner<RocketArtilleryMissile> {

    public static final RocketArtilleryMissileRunner ROCKET_ARTILLERY_MISSILE_RUNNER = new RocketArtilleryMissileRunner();

    public RocketArtilleryMissileRunner() {
        super(RocketArtilleryMissile.class, RocketArtillery.class);
    }

}
