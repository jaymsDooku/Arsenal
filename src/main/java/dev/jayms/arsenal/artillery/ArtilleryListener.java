package dev.jayms.arsenal.artillery;

import dev.jayms.arsenal.Arsenal;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ArtilleryListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        if (itemStack == null) {
            return;
        }
        if (itemStack.getType() != Material.STICK) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            Block clickedBlock = event.getClickedBlock();

            if (!player.isSneaking()) {
                return;
            }

            ArtilleryManager artilleryManager = Arsenal.getInstance().getArtilleryManager();
            Artillery artillery = artilleryManager.getArtillery(clickedBlock);
            artillery.startAssembly(player);
        }
    }

}
