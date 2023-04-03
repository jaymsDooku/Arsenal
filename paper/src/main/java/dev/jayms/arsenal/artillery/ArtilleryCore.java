package dev.jayms.arsenal.artillery;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.PermissionType;

import java.util.Objects;

public class ArtilleryCore {

    private ArtilleryType type;
    private int groupId;
    private Location location;

    public ArtilleryCore(ArtilleryType type, int groupId, Location location) {
        this.type = type;
        this.groupId = groupId;
        this.location = location;
    }

    public ArtilleryType getType() {
        return type;
    }

    public int getGroupId() {
        return groupId;
    }

    public Location getLocation() {
        return location;
    }

    public Block getBlock() {
        return location.getBlock();
    }

    public Inventory getInventory() {
        Block block = getBlock();
        BlockState state = block.getState();
        Chest chestState = (Chest) state;
        Inventory chestInventory = chestState.getBlockInventory();
        return chestInventory;
    }

    public boolean hasAmmo() {
        Inventory inventory = getInventory();
        return inventory.containsAtLeast(type.getAmmoItemStack(), type.getAmmoItemStack().getAmount());
    }

    public void consumeAmmo() {
        if (!hasAmmo()) {
            return;
        }

        Inventory inventory = getInventory();
        inventory.removeItem(type.getAmmoItemStack());
    }

    public boolean canBreak(Player player) {
        Group group = GroupManager.getGroup(groupId);
        if (group == null) {
            return false;
        }
        PermissionType permission = PermissionType.getPermission(Permissions.ARTILLERY_LIST);
        return NameAPI.getGroupManager().hasAccess(group, player.getUniqueId(), permission);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtilleryCore that = (ArtilleryCore) o;
        return groupId == that.groupId && Objects.equals(type, that.type) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, groupId, location);
    }
}
