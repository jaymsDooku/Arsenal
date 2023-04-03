package dev.jayms.arsenal.artillery;

import org.bukkit.entity.Player;
import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.group.Group;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArtilleryPlayerState {

    private static final Map<UUID, ArtilleryPlayerState> playerStates = new HashMap<>();

    public static ArtilleryPlayerState getPlayerState(Player player) {
        ArtilleryPlayerState artilleryPlayerState = playerStates.get(player.getUniqueId());
        if (artilleryPlayerState == null) {
            artilleryPlayerState = new ArtilleryPlayerState(player);
            playerStates.put(player.getUniqueId(), artilleryPlayerState);
        }
        return artilleryPlayerState;
    }

    private final Player player;
    private int groupId = -1;

    private ArtilleryPlayerState(Player player) {
        this.player = player;

        String defGroupName = NameAPI.getGroupManager().getDefaultGroup(player.getUniqueId());
        Group group = GroupManager.getGroup(defGroupName);
        if (group != null) {
            groupId = group.getGroupId();
        }
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setGroup(Group group) {
        if (group == null) {
            this.groupId = -1;
            return;
        }
        this.groupId = group.getGroupId();
    }
}
