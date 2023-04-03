package dev.jayms.arsenal;

import co.aikar.commands.PaperCommandManager;
import dev.jayms.arsenal.artillery.*;
import dev.jayms.arsenal.commands.ArsenalCommands;
import dev.jayms.arsenal.item.CustomItemManager;
import dev.jayms.arsenal.structure.StructureRegionTool;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.world.locations.chunkmeta.api.BlockBasedChunkMetaView;
import vg.civcraft.mc.civmodcore.world.locations.chunkmeta.api.ChunkMetaAPI;
import vg.civcraft.mc.civmodcore.world.locations.chunkmeta.block.table.TableBasedDataObject;
import vg.civcraft.mc.civmodcore.world.locations.chunkmeta.block.table.TableStorageEngine;
import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.permission.PermissionType;

import java.io.File;
import java.util.LinkedList;

public class Arsenal extends ACivMod {

    private static Arsenal instance;
    private static PaperCommandManager commandManager;

    public static Arsenal getInstance() {
        return instance;
    }

    private ArtilleryTypeManager artilleryTypeManager;

    public ArtilleryTypeManager getArtilleryTypeManager() {
        return artilleryTypeManager;
    }

    private ArsenalConfig arsenalConfig;

    public ArsenalConfig getArsenalConfig() {
        return arsenalConfig;
    }

    private ArtilleryDAO dao;

    public ArtilleryDAO getDao() {
        return dao;
    }

    private ArtilleryManager artilleryManager;

    public ArtilleryManager getArtilleryManager() {
        return artilleryManager;
    }

    private ArtilleryListener artilleryListener;
    private ArtilleryDamageListener artilleryDamageListener;
    private ArtilleryDefenceListener artilleryDefenceListener;

    private File structureFolder;

    public File getStructureFolder() {
        return structureFolder;
    }

    @Override
    public void onEnable() {
        instance = this;
        if (!Bukkit.getPluginManager().isPluginEnabled("NameLayer")) {
            getLogger().info("Citadel is shutting down because it could not find NameLayer");
            Bukkit.shutdown();
            return;
        }
        this.structureFolder = new File(getDataFolder(), "structures");
        if (!this.structureFolder.exists()) {
            this.structureFolder.mkdir();
        }

        arsenalConfig = new ArsenalConfig(this);
        if (!arsenalConfig.parse()) {
            getLogger().severe("Errors in config file, shutting down");
            Bukkit.shutdown();
            return;
        }

        artilleryTypeManager = new ArtilleryTypeManager();
        arsenalConfig.getArtilleryTypes().forEach(t -> artilleryTypeManager.register(t));

        dao = new ArtilleryDAO(getLogger(), arsenalConfig.getDatabase());
        if (!dao.updateDatabase()) {
            getLogger().severe("Errors setting up database, shutting down");
            Bukkit.shutdown();
            return;
        }
        BlockBasedChunkMetaView<ArtilleryChunkData, TableBasedDataObject, TableStorageEngine<Artillery>> chunkMetaData =
                ChunkMetaAPI.registerBlockBasedPlugin(this, () -> new ArtilleryChunkData(false, dao), dao, true);
        if (chunkMetaData == null) {
            getLogger().severe("Errors setting up chunk metadata API, shutting down");
            Bukkit.shutdown();
            return;
        }
        artilleryManager = new ArtilleryManager(chunkMetaData);

        artilleryListener = new ArtilleryListener();
        artilleryDamageListener = new ArtilleryDamageListener();
        artilleryDefenceListener = new ArtilleryDefenceListener();
        Bukkit.getPluginManager().registerEvents(artilleryListener, this);
        Bukkit.getPluginManager().registerEvents(artilleryDamageListener, this);
        Bukkit.getPluginManager().registerEvents(artilleryDefenceListener, this);

        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new ArsenalCommands());

        CustomItemManager.getCustomItemManager().createCustomItem(StructureRegionTool.ID, StructureRegionTool.class);

        new ArtilleryMissileRunnable().runTaskTimer(Arsenal.getInstance(), 0L, 1L);
    }

    @Override
    public void onDisable() {
        if (artilleryManager != null) {
            artilleryManager.shutDown();
        }
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    private void registerNameLayerPermissions() {
        LinkedList <GroupManager.PlayerType> memberAndAbove = new LinkedList<>();
        memberAndAbove.add(GroupManager.PlayerType.MEMBERS);
        memberAndAbove.add(GroupManager.PlayerType.MODS);
        memberAndAbove.add(GroupManager.PlayerType.ADMINS);
        memberAndAbove.add(GroupManager.PlayerType.OWNER);

        LinkedList <GroupManager.PlayerType> modAndAbove = new LinkedList<>();
        modAndAbove.add(GroupManager.PlayerType.MODS);
        modAndAbove.add(GroupManager.PlayerType.ADMINS);
        modAndAbove.add(GroupManager.PlayerType.OWNER);

        LinkedList <GroupManager.PlayerType> adminAndAbove = new LinkedList<>();
        adminAndAbove.add(GroupManager.PlayerType.ADMINS);
        adminAndAbove.add(GroupManager.PlayerType.OWNER);

        PermissionType.registerPermission(Permissions.ARTILLERY_LIST, modAndAbove, "Allows a player to see all artillery under this group.");
    }

}
