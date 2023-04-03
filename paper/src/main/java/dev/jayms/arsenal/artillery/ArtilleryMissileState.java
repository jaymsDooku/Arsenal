package dev.jayms.arsenal.artillery;

public interface ArtilleryMissileState<T extends ArtilleryMissile> {

    default boolean isAcceptingState() {
        return false;
    }

    ArtilleryMissileState<T> update(T missile);

}
