package dev.jayms.arsenal.artillery;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ArtilleryCore {

    private ArtilleryType type;
    private int groupId;
    private float health;
    private Location location;

    public ArtilleryCore(ArtilleryType type, int groupId, float health, Location location) {
        this.type = type;
        this.groupId = groupId;
        this.health = health;
        this.location = location;
    }

    public ArtilleryType getType() {
        return type;
    }

    public int getGroupId() {
        return groupId;
    }

    public float getHealth() {
        return health;
    }

    public Block getBlock() {
        return location.getBlock();
    }

}
