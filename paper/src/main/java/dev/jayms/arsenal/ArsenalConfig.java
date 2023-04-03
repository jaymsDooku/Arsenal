package dev.jayms.arsenal;

import dev.jayms.arsenal.artillery.Artillery;
import dev.jayms.arsenal.artillery.ArtilleryType;
import dev.jayms.arsenal.structure.StructureBlueprint;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import vg.civcraft.mc.civmodcore.config.ConfigParser;
import vg.civcraft.mc.civmodcore.dao.DatabaseCredentials;
import vg.civcraft.mc.civmodcore.dao.ManagedDatasource;

import java.io.IOException;
import java.util.*;

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

    private ArtilleryType parseArtilleryType(String key, ConfigurationSection configurationSection) {
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

        ConfigurationSection ammoSection = configurationSection.getConfigurationSection("ammo");
        if (ammoSection == null) {
            logger.warning("Artillery type at " + configurationSection.getCurrentPath() + " failed to load ammo section");
            return null;
        }

        String ammoTypeStr = ammoSection.getString("type");
        if (ammoTypeStr == null) {
            logger.warning("Artillery type at " + configurationSection.getCurrentPath() + " had no ammo type.");
            return null;
        }
        Material ammoType = Material.valueOf(ammoTypeStr.toUpperCase());
        if (!ammoSection.contains("consumption-amount")) {
            logger.warning("Artillery type at " + configurationSection.getCurrentPath() + " had no ammo consumption amount.");
            return null;
        }
        int ammoConsumptionAmount = ammoSection.getInt("consumption-amount");

        if (!configurationSection.contains("cooldown")) {
            logger.warning("Artillery type at " + configurationSection.getCurrentPath() + " had no cooldown duration.");
            return null;
        }
        long cooldown = configurationSection.getLong("cooldown");

        if (!configurationSection.contains("max-health")) {
            logger.warning("Artillery type at " + configurationSection.getCurrentPath() + " had no max health.");
            return null;
        }
        float maxHealth = ((Double) configurationSection.getDouble("max-health")).floatValue();

        if (!configurationSection.contains("defence-range")) {
            logger.warning("Artillery type at " + configurationSection.getCurrentPath() + " had no defence range.");
            return null;
        }
        int defenceRange = configurationSection.getInt("defence-range");

        if (!configurationSection.contains("artillery-damage-default")) {
            logger.warning("Artillery type at " + configurationSection.getCurrentPath() + " had no default artillery damage.");
            return null;
        }
        double artilleryDamageDefault = configurationSection.getDouble("artillery-damage-default");

        Map<String, Double> artilleryDamage = new HashMap<>();
        ConfigurationSection artilleryDamageSection = configurationSection.getConfigurationSection("artillery-damage");
        Set<String> artilleryKeys = artilleryDamageSection.getKeys(false);
        for (String artilleryKey : artilleryKeys) {
            artilleryDamage.put(artilleryKey, artilleryDamageSection.getDouble(artilleryKey));
        }

        ConfigurationSection configSection = configurationSection.getConfigurationSection("config");

        return new ArtilleryType(id, key, displayName, structureBlueprint, artilleryClazz, ammoType, ammoConsumptionAmount, maxHealth,
                cooldown, defenceRange, artilleryDamageDefault, artilleryDamage, configSection);
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
            ArtilleryType type = parseArtilleryType(key, configurationSection.getConfigurationSection(key));
            if (type != null) {
                logger.info("Loaded artillery type: " + type);
                artilleryTypes.add(type);
            }
        }
    }

}
