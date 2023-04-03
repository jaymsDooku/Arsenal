package dev.jayms.arsenal.artillery;

import dev.jayms.arsenal.artillery.shooter.Shooter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class ArtilleryMissile<T extends Artillery> {

    private UUID id;
    protected Shooter shooter;
    protected ArtilleryMissileState state;
    protected T artillery;
    protected Location target;

    protected ArtilleryMissile(Shooter shooter, T artillery, Location target) {
        this.id = UUID.randomUUID();
        this.shooter = shooter;
        this.artillery = artillery;
        this.target = target;
    }

    public UUID getId() {
        return id;
    }

    public Shooter getShooter() {
        return shooter;
    }

    public T getArtillery() {
        return artillery;
    }

    public Location getTarget() {
        return target;
    }

    public boolean update() {
        this.state = state.update(this);

        if (state.isAcceptingState()) {
            state.update(this);
            return true;
        }
        return false;
    }

    public void setState(ArtilleryMissileState state) {
        this.state = state;
    }

    public ArtilleryMissileState getState() {
        return state;
    }
}
