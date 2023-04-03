package dev.jayms.arsenal.artillery.impl.trebuchet;

import dev.jayms.arsenal.artillery.ArtilleryMissileRunner;

public class TrebuchetMissileRunner extends ArtilleryMissileRunner<TrebuchetMissile> {

    public static final TrebuchetMissileRunner TREBUCHET_MISSILE_RUNNER = new TrebuchetMissileRunner();

    public TrebuchetMissileRunner() {
        super(TrebuchetMissile.class, Trebuchet.class);
    }

}
