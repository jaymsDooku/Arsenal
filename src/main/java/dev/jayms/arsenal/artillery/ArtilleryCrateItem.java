package dev.jayms.arsenal.artillery;

import dev.jayms.arsenal.Arsenal;
import dev.jayms.arsenal.item.CustomItem;
import dev.jayms.arsenal.util.PlayerTools;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import vg.civcraft.mc.civmodcore.inventory.items.ItemBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Consumer;

public class ArtilleryCrateItem extends CustomItem {

    private ArtilleryType artilleryType;

    public ArtilleryCrateItem(int ID, ArtilleryType artilleryType) {
        super(ID);
        this.artilleryType = artilleryType;
    }

    public ArtilleryType getArtilleryType() {
        return artilleryType;
    }

    @Override
    protected ItemBuilder getItemStackBuilder(Map<String, Object> data) {
        return ItemBuilder.builder(Material.CHEST)
                .meta((Consumer<ItemMeta>) itemMeta -> {
                    itemMeta.displayName(Component.text(artilleryType.getTypeDisplayName()));
                });
    }

    @Override
    public boolean preventOnLeftClick() {
        return false;
    }

    @Override
    public boolean preventOnRightClick() {
        return false;
    }

    @Override
    public Runnable onBlockPlace(BlockPlaceEvent e) {
        return () -> {
            Player player = e.getPlayer();
            ArtilleryPlayerState playerState = ArtilleryPlayerState.getPlayerState(player);
            int groupId = playerState.getGroupId();
            if (groupId == -1) {
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Must have namelayer group selected");
                return;
            }
            Block block = e.getBlock();
            Location loc = e.getBlock().getLocation();

            BlockFace direction;
            if (block.getBlockData() instanceof Directional) {
                Directional directional = (Directional) block.getBlockData();
                direction = directional.getFacing().getOppositeFace();
            } else {
                direction = PlayerTools.yawToFace(player.getPlayer().getEyeLocation().getYaw(), false);
            }
            ArtilleryCore artilleryCore = new ArtilleryCore(artilleryType, groupId, 100, loc);
            try {
                ArtilleryManager artilleryManager = Arsenal.getInstance().getArtilleryManager();
                artilleryManager.putArtillery(artilleryType.instantiateNewArtillery(artilleryCore, direction));
                player.sendMessage("Created " + artilleryType.getTypeDisplayName() + " facing " + direction);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException ex) {
                player.sendMessage(ChatColor.RED + "Failed to create " + artilleryType.getTypeDisplayName());
                ex.printStackTrace();
            }
        };
    }
}
