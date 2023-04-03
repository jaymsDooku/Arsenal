package dev.jayms.arsenal.artillery.impl.field;

import dev.jayms.arsenal.artillery.ArtilleryMissileRunner;

public class FieldArtilleryMissileRunner extends ArtilleryMissileRunner<FieldArtilleryMissile> {

    public static final FieldArtilleryMissileRunner FIELD_ARTILLERY_MISSILE_RUNNER = new FieldArtilleryMissileRunner();

    public FieldArtilleryMissileRunner() {
        super(FieldArtilleryMissile.class, FieldArtillery.class);
    }

}
