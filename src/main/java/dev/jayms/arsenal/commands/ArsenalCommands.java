package dev.jayms.arsenal.commands;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import dev.jayms.arsenal.Arsenal;
import dev.jayms.arsenal.ArsenalConfig;
import dev.jayms.arsenal.artillery.ArtilleryCrateItem;
import dev.jayms.arsenal.artillery.ArtilleryPlayerState;
import dev.jayms.arsenal.artillery.ArtilleryType;
import dev.jayms.arsenal.artillery.ArtilleryTypeManager;
import dev.jayms.arsenal.item.CustomItemManager;
import dev.jayms.arsenal.structure.*;
import dev.jayms.arsenal.util.AffineTransform;
import dev.jayms.arsenal.util.BlockFaceTransform;
import dev.jayms.arsenal.util.PlayerTools;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;
import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.group.Group;

import java.io.IOException;
import java.util.*;

@CommandAlias("arsenal")
public class ArsenalCommands extends BaseCommand {

    @Subcommand("test")
    public void onTest(Player player, int period) {
        player.sendMessage("Executing Arsenal test...");

        List<StructureBlock> blocks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            blocks.add(StructureBlock.builder(Material.OBSIDIAN).pos(0, i, 0).build());
        }
        BlockFace direction = PlayerTools.yawToFace(player.getEyeLocation().getYaw(), false);
        StructureBlueprint structureBlueprint = new StructureBlueprint("test", blocks, direction);

        StructurePlacementTask structurePlacementTask = new StructurePlacementTask(player, structureBlueprint, new BlockFaceTransform(direction, direction));
        structurePlacementTask.runTaskTimer(Arsenal.getInstance(), 0L, period);

        player.sendMessage("Executed Arsenal test!");
    }

    @Subcommand("structure blueprints")
    public void onStructureBlueprintList(Player player) {
        List<StructureBlueprint> structureBlueprints = StructureBlueprint.getStructureBlueprints();
        for (int i = 0; i < structureBlueprints.size(); i++) {
            StructureBlueprint structureBlueprint = structureBlueprints.get(i);
            player.sendMessage((i + 1) + ". " + structureBlueprint.getName());
        }
    }

    @Subcommand("structure blueprint")
    public void onStructureBlueprintView(Player player, String blueprintName) {
        StructureBlueprint structureBlueprint = StructureBlueprint.getStructureBlueprint(blueprintName);
        player.sendMessage("Name: " + structureBlueprint.getName());
        player.sendMessage("Blocks: " + structureBlueprint.getBlocks().size());
        player.sendMessage("Named Blocks: " + structureBlueprint.getNamedBlockPositions());
    }

    @Subcommand("structure blueprint wand")
    public void onStructureBlueprintGiveWand(Player player) {
        StructureRegionTool structureRegionTool = CustomItemManager.getCustomItemManager().getCustomItem(StructureRegionTool.ID, StructureRegionTool.class);
        ItemStack itemStack = structureRegionTool.getItemStack();
        player.getInventory().addItem(itemStack);
        player.sendMessage("Here is your wand!");
    }

    @Subcommand("structure blueprint nameblock")
    public void onStructureBlueprintNameBlock(Player player, String blockName) {
        Block targetBlock = player.getTargetBlock(5);
        if (targetBlock == null) {
            player.sendMessage(ChatColor.RED + "No block found");
            return;
        }
        StructureRegionClipboard structureRegionClipboard = StructureRegionClipboard.getRegionClipboard(player);
        if (!structureRegionClipboard.inside(targetBlock.getLocation())) {
            player.sendMessage(ChatColor.RED + "Target block outside of clipboard selection");
            return;
        }
        StructureBlueprintRecording structureBlueprintRecording = StructureBlueprintRecording.getBlueprintRecording(player);
        structureBlueprintRecording.nameBlock(blockName, targetBlock);
        player.sendMessage(targetBlock + " is now called " + blockName);
    }

    @Subcommand("structure blueprint namedblocks")
    public void onStructureBlueprintListNamedBlocks(Player player) {
        StructureBlueprintRecording structureBlueprintRecording = StructureBlueprintRecording.getBlueprintRecording(player);
        Set<Map.Entry<String, Block>> namedBlocks = structureBlueprintRecording.getNamedBlocks();
        for (Map.Entry<String, Block> namedBlockEntry : namedBlocks) {
            player.sendMessage(namedBlockEntry.getKey() + ": " + namedBlockEntry.getValue());
        }
    }

    @Subcommand("structure blueprint origin")
    public void onStructureBlueprintOrigin(Player player) {
        Block targetBlock = player.getTargetBlock(5);
        if (targetBlock == null) {
            player.sendMessage(ChatColor.RED + "No block found");
            return;
        }
        StructureRegionClipboard structureRegionClipboard = StructureRegionClipboard.getRegionClipboard(player);
        if (!structureRegionClipboard.inside(targetBlock.getLocation())) {
            player.sendMessage(ChatColor.RED + "Target block outside of clipboard selection");
            return;
        }
        StructureBlueprintRecording structureBlueprintRecording = StructureBlueprintRecording.getBlueprintRecording(player);
        structureBlueprintRecording.setOrigin(targetBlock);
        player.sendMessage("Set blueprint origin to " + targetBlock);
    }

    @Subcommand("structure blueprint copy")
    public void onStructureBlueprintCopy(Player player) {
        StructureRegionClipboard clipboard = StructureRegionClipboard.getRegionClipboard(player);
        StructureBlueprintRecording structureBlueprintRecording = StructureBlueprintRecording.getBlueprintRecording(player);
        List<Block> blocks = clipboard.getAllNonAirBlocks();
        structureBlueprintRecording.setBlocks(blocks);
        player.sendMessage("Copied " + blocks.size() + " blocks");
    }

    @Subcommand("structure blueprint paste")
    public void onStructureBlueprintPaste(Player player, int period) {
        StructureBlueprintRecording structureBlueprintRecording = StructureBlueprintRecording.getBlueprintRecording(player);
        BlockFace direction = PlayerTools.yawToFace(player.getEyeLocation().getYaw(), false);
        StructureBlueprint structureBlueprint = structureBlueprintRecording.toBlueprint(player.getName(), direction);
        List<StructureBlock> structureBlocks = structureBlueprint.getBlocks();
        for (int i = 0; i < structureBlocks.size(); i++) {
            StructureBlock structureBlock = structureBlocks.get(i);
            player.sendMessage(i + ". " + structureBlock.getMaterial());
        }
        StructurePlacementTask structurePlacementTask = new StructurePlacementTask(player, structureBlueprint, new BlockFaceTransform(direction, direction));
        structurePlacementTask.runTaskTimer(Arsenal.getInstance(), 0L, period);
        player.sendMessage("Pasting blueprint...");
    }

    @Subcommand("structure blueprint create")
    public void onStructureBlueprintCreate(Player player, String blueprintName) {
        StructureBlueprintRecording structureBlueprintRecording = StructureBlueprintRecording.getBlueprintRecording(player);
        StructureBlueprint structureBlueprint = structureBlueprintRecording.toBlueprint(blueprintName, PlayerTools.yawToFace(player.getEyeLocation().getYaw(), false));

        player.sendMessage("Created blueprint: " + blueprintName);
    }

    @Subcommand("structure blueprint load")
    public void onStructureBlueprintLoad(Player player, String blueprintName) {
        try {
            StructureBlueprint structureBlueprint = StructureBlueprint.loadStructureBlueprint(blueprintName);
            player.sendMessage("Loaded blueprint " + structureBlueprint.getName() + " from " + structureBlueprint.getSaveFile().getName());
        } catch (IOException ex) {
            player.sendMessage(ChatColor.RED + "Failed to load " + blueprintName);
            ex.printStackTrace();
        }
    }

    @Subcommand("structure blueprint save")
    public void onStructureBlueprintSave(Player player, String blueprintName) {
        StructureBlueprint structureBlueprint = StructureBlueprint.getStructureBlueprint(blueprintName);
        if (structureBlueprint == null) {
            player.sendMessage(ChatColor.RED + "Blueprint not found");
            return;
        }
        try {
            structureBlueprint.save();
            player.sendMessage("Successfully saved " + blueprintName);
        } catch (IOException ex) {
            ex.printStackTrace();
            player.sendMessage(ChatColor.RED + "Failed to save " + blueprintName);
        }
    }

    @Subcommand("structure blueprint clear")
    public void onStructureBlueprintClear(Player player) {

    }

    @Subcommand("structure tasks")
    public void onStructureTasks(Player player) {
        Set<Map.Entry<Integer, StructurePlacementTask>> tasks = StructurePlacementTask.getTasks();
        for (Map.Entry<Integer, StructurePlacementTask> taskEntry : tasks) {
            StructurePlacementTask structurePlacementTask = taskEntry.getValue();
            player.sendMessage(taskEntry.getKey() + ": forward=" + structurePlacementTask.isForward() +
                    ", paused=" + structurePlacementTask.isPaused() +
                    ", finished=" + structurePlacementTask.isFinished());
        }
    }

    @Subcommand("structure task forward")
    public void onStructureTaskForward(Player player, int id) {
        StructurePlacementTask task = StructurePlacementTask.getTask(id);
        if (task == null) {
            player.sendMessage(ChatColor.RED + "Task not found");
            return;
        }
        task.setForward(true);
    }

    @Subcommand("structure task backward")
    public void onStructureTaskBackward(Player player, int id) {
        StructurePlacementTask task = StructurePlacementTask.getTask(id);
        if (task == null) {
            player.sendMessage(ChatColor.RED + "Task not found");
            return;
        }
        task.setForward(false);
    }

    @Subcommand("artillery type list")
    public void onArtilleryTypeList(Player player) {
        ArtilleryTypeManager artilleryTypeManager = Arsenal.getInstance().getArtilleryTypeManager();
        Collection<ArtilleryType> artilleryTypes = artilleryTypeManager.getAllTypes();
        if (artilleryTypes.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No artillery types.");
            return;
        }
        for (ArtilleryType type : artilleryTypes) {
            player.sendMessage(type.getId() + ". " + type.getTypeDisplayName());
        }
    }

    @Subcommand("artillery type give")
    public void onArtilleryTypeGive(Player player, short artilleryTypeId) {
        ArtilleryTypeManager artilleryTypeManager = Arsenal.getInstance().getArtilleryTypeManager();
        ArtilleryType artilleryType = artilleryTypeManager.getById(artilleryTypeId);
        if (artilleryType == null) {
            player.sendMessage(ChatColor.RED + "Artillery type doesn't exist.");
            return;
        }
        ArtilleryCrateItem artilleryCrateItem = artilleryType.getArtilleryCrateItem();
        if (artilleryCrateItem == null) {
            player.sendMessage(ChatColor.RED + "No artillery crate item.");
            return;
        }
        ItemStack itemStack = artilleryCrateItem.getItemStack();
        player.getInventory().addItem(itemStack);
        player.sendMessage("Given " + artilleryType.getTypeDisplayName() + " crate");
    }

    @Subcommand("artillery type reload")
    public void onArtilleryTypeReload(Player player) {
        Arsenal.getInstance().reloadConfig();
        ArsenalConfig arsenalConfig = Arsenal.getInstance().getArsenalConfig();
        if (!arsenalConfig.parse()) {
            player.sendMessage(ChatColor.RED + "Config failed to parse");
            return;
        }
        ArtilleryTypeManager artilleryTypeManager = Arsenal.getInstance().getArtilleryTypeManager();
        arsenalConfig.getArtilleryTypes().forEach(t -> artilleryTypeManager.register(t));
    }

    @Subcommand("group")
    public void onNamelayerGroup(Player player, @Optional String groupNameParam) {
        String groupName = groupNameParam;
        if (groupName == null) {
            groupName = NameAPI.getGroupManager().getDefaultGroup(player.getUniqueId());
        }

        Group group = GroupManager.getGroup(groupName);
        if (group == null) {
            player.sendMessage(ChatColor.RED + "The group " + groupName + " does not exist.");
            return;
        }

        ArtilleryPlayerState playerState = ArtilleryPlayerState.getPlayerState(player);
        playerState.setGroup(group);
        player.sendMessage(group.getName() + " group set for artillery.");
    }

}
