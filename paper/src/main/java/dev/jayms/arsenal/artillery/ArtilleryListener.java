package dev.jayms.arsenal.artillery;

import dev.jayms.arsenal.Arsenal;
import dev.jayms.arsenal.artillery.shooter.PlayerShooter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ArtilleryListener implements Listener {

    public static final Map<Player, Artillery> SELECTED_ARTILLERY = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        if (itemStack == null) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            Block clickedBlock = event.getClickedBlock();

            ArtilleryManager artilleryManager = Arsenal.getInstance().getArtilleryManager();
            Artillery artillery = artilleryManager.getArtillery(clickedBlock);

            if (artillery != null) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (!player.isSneaking()) {
                        return;
                    }
                    if (itemStack.getType() == Material.STICK) {
                        if (artillery.isAssembled()) {
                            artillery.fire(player, null);
                        } else {
                            artillery.startAssembly(player);
                        }
                    } else if (itemStack.getType() == Material.WOODEN_HOE) {
                        artillery.increasehAngle(player);
                    } else if (itemStack.getType() == Material.WOODEN_SHOVEL) {
                        artillery.increasevAngle(player);
                    } else if (itemStack.getType() == Material.WOODEN_AXE) {
                        artillery.increaseFiringPower(player);
                    } else if (itemStack.getType() == Material.GOLDEN_HOE) {
                        artillery.toggleDefence();
                        player.sendMessage("Defence: " + (artillery.isDefenceOn() ? "On" : "Off"));
                    } else if (itemStack.getType() == Material.WOODEN_PICKAXE) {
                        player.sendMessage("Health: " + artillery.getHealth());
                    }
                } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if (itemStack.getType() == Material.WOODEN_AXE) {
                        artillery.decreaseFiringPower(player);
                    } else if (itemStack.getType() == Material.WOODEN_HOE) {
                        artillery.decreasehAngle(player);
                    } else if (itemStack.getType() == Material.WOODEN_SHOVEL) {
                        artillery.decreasevAngle(player);
                    }
                }
            }

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && itemStack.getType() == Material.WOODEN_SWORD) {
                Artillery selectedArtillery = SELECTED_ARTILLERY.get(player);
                if (selectedArtillery == null) {
                    player.sendMessage("Artillery not selected.");
                    return;
                }
                selectedArtillery.fire(new PlayerShooter(player), clickedBlock.getLocation());
            }
        }
    }

}
