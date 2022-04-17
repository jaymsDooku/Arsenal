package dev.jayms.arsenal;

import dev.jayms.arsenal.artillery.Artillery;
import dev.jayms.arsenal.artillery.ArtilleryType;
import dev.jayms.arsenal.structure.StructureBlueprint;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import vg.civcraft.mc.civmodcore.config.ConfigParser;
import vg.civcraft.mc.civmodcore.dao.DatabaseCredentials;
import vg.civcraft.mc.civmodcore.dao.ManagedDatasource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArsenalConfig extends ConfigParser {

    private ManagedDatasource database;
    private List<ArtilleryType> artilleryTypes;

    public ArsenalConfig(Plugin plugin) {
        super(plugin);
    }

    public ManagedDatasource getDatabase() {
        return database;
    }

    public List<ArtilleryType> getArtilleryTypes() {
        return artilleryTypes;
    }

    @Override
    protected boolean parseInternal(ConfigurationSection configurationSection) {
        database = ManagedDatasource.construct(Arsenal.getInstance(), (DatabaseCredentials) configurationSection.get("database"));
        parseArtilleryTypes(configurationSection.getConfigurationSection("artillery").getConfigurationSection("types"));
        return true;
    }

    private ArtilleryType parseArtilleryType(ConfigurationSection configurationSection) {
        short id = (short) configurationSection.getInt("id", -1);
        String displayName = configurationSection.getString("display-name");
        String blueprintName = configurationSection.getString("blueprint-name");
        String clazzName = configurationSection.getString("clazz-name");
        if (clazzName == null) {
            logger.warning("Artillery type at " + configurationSection.getCurrentPath() + " had no clazz name, it was ignored");
            return null;
        }
        Class<? extends Artillery> artilleryClazz = null;
        try {
            Class<?> clazz = Class.forName(clazzName);
            if (Artillery.class.isAssignableFrom(clazz)) {
                artilleryClazz = (Class<? extends Artillery>) clazz;
            }
        } catch (ClassNotFoundException e) {
            logger.warning("Artillery type at " + configurationSection.getCurrentPath() + " had invalid clazz name");
            return null;
        }
        if (id == -1) {
            logger.warning("Artillery type at " + configurationSection.getCurrentPath() + " had no id, it was ignored");
            return null;
        }
        if (artilleryClazz == null) {
            logger.warning("Artillery type at " + configurationSection.getCurrentPath() + " had invalid clazz name");
            return null;
        }
        StructureBlueprint structureBlueprint;
        try {
            structureBlueprint = StructureBlueprint.loadStructureBlueprint(blueprintName);
        } catch (IOException e) {
            logger.warning("Artillery type at " + configurationSection.getCurrentPath() + " failed to load blueprint structure " + blueprintName);
            return null;
        }
        if (structureBlueprint == null) {
            logger.warning("Artillery type at " + configurationSection.getCurrentPath() + " couldn't locate blueprint structure");
            return null;
        }
        return new ArtilleryType(id, displayName, structureBlueprint, artilleryClazz);
    }

    private void parseArtilleryTypes(ConfigurationSection configurationSection) {
        artilleryTypes = new ArrayList<>();
        if (configurationSection == null) {
            logger.info("No artillery types found in config");
            return;
        }
        for (String key : configurationSection.getKeys(false)) {
            if (!configurationSection.isConfigurationSection(key)) {
                logger.warning("Ignoring invalid entry " + key + " at " + configurationSection.getCurrentPath());
                continue;
            }
            ArtilleryType type = parseArtilleryType(configurationSection.getConfigurationSection(key));
            if (type != null) {
                logger.info("Loaded artillery type: " + type);
                artilleryTypes.add(type);
            }
        }
    }

}
