package dev.jayms.arsenal.structure;

import dev.jayms.arsenal.item.CustomItem;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import vg.civcraft.mc.civmodcore.inventory.items.ItemBuilder;

import java.util.Map;
import java.util.function.Consumer;

public class StructureRegionTool extends CustomItem {

    public static final int ID = 100;

    public StructureRegionTool(int id) {
        super(id);
    }

    @Override
    protected ItemBuilder getItemStackBuilder(Map<String, Object> data) {
        return ItemBuilder.builder(Material.WOODEN_SHOVEL).meta((Consumer<ItemMeta>) meta -> {
            meta.displayName(Component.text(ChatColor.DARK_PURPLE + "Structure Region Tool"));
        });
    }

    @Override
    public boolean preventOnLeftClick() {
        return true;
    }

    @Override
    public boolean preventOnRightClick() {
        return true;
    }

    @Override
    public Runnable getLeftClick(PlayerInteractEvent e) {
        return () -> {
            Block clicked = e.getClickedBlock();
            if (clicked == null) return;

            StructureRegionClipboard structureRegionClipboard = StructureRegionClipboard.getRegionClipboard(e.getPlayer());
            Location point1 = clicked.getLocation().clone();
            structureRegionClipboard.setPoint1(point1);
            e.getPlayer().sendMessage("Set structure point 1: " + point1);
        };
    }

    @Override
    public Runnable getRightClick(PlayerInteractEvent e) {
        return () -> {
            Block clicked = e.getClickedBlock();
            if (clicked == null) return;

            StructureRegionClipboard structureRegionClipboard = StructureRegionClipboard.getRegionClipboard(e.getPlayer());
            Location point2 = clicked.getLocation().clone();
            structureRegionClipboard.setPoint2(point2);
            e.getPlayer().sendMessage("Set structure point 2: " + point2);
        };
    }

}
