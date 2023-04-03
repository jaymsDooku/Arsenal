package dev.jayms.arsenal.artillery;

import vg.civcraft.mc.civmodcore.world.locations.QTBox;

public class ArtilleryDefenceRange implements QTBox {

    private Artillery artillery;

    public ArtilleryDefenceRange(Artillery artillery) {
        this.artillery = artillery;
    }

    public Artillery getArtillery() {
        return artillery;
    }

    @Override
    public int qtXMin() {
        return artillery.qtXMin() - artillery.getType().getDefenceRange();
    }

    @Override
    public int qtXMid() {
        return artillery.qtXMid();
    }

    @Override
    public int qtXMax() {
        return artillery.qtXMax() + artillery.getType().getDefenceRange();
    }

    @Override
    public int qtZMin() {
        return artillery.qtZMin() - artillery.getType().getDefenceRange();
    }

    @Override
    public int qtZMid() {
        return artillery.qtZMid();
    }

    @Override
    public int qtZMax() {
        return artillery.qtZMax() + artillery.getType().getDefenceRange();
    }
}
