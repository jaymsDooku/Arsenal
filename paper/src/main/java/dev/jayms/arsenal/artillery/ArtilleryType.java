package dev.jayms.arsenal.artillery;

import dev.jayms.arsenal.item.CustomItemManager;
import dev.jayms.arsenal.structure.StructureBlueprint;
import dev.jayms.arsenal.structure.StructureInstance;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.utilities.cooldowns.ICoolDownHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ArtilleryType {

    private static final int ARTILLERY_CRATE_BASE_ID = 1000;

    private short id;
    private String key;
    private String typeDisplayName;
    private StructureBlueprint structureBlueprint;
    private Class<? extends Artillery> artilleryClazz;

    private Material ammoType;
    private int ammoConsumptionAmount;

    private float maxHealth;
    private long cooldown;
    private int defenceRange;

    private double artilleryDamageDefault;
    private Map<String, Double> artilleryDamage;

    private ConfigurationSection configurationSection;

    public ArtilleryType(short id, String key, String typeDisplayName, StructureBlueprint structureBlueprint,
                         Class<? extends Artillery> artilleryClazz, Material ammoType, int ammoConsumptionAmount,
                         float maxHealth, long cooldown, int defenceRange, double artilleryDamageDefault,
                         Map<String, Double> artilleryDamage, ConfigurationSection configurationSection) {
        this.id = id;
        this.key = key;
        this.typeDisplayName = typeDisplayName;
        this.structureBlueprint = structureBlueprint;
        this.artilleryClazz = artilleryClazz;

        this.ammoType = ammoType;
        this.ammoConsumptionAmount = ammoConsumptionAmount;

        this.maxHealth = maxHealth;
        this.cooldown = cooldown;
        this.defenceRange = defenceRange;

        this.artilleryDamageDefault = artilleryDamageDefault;
        this.artilleryDamage = artilleryDamage;

        this.configurationSection = configurationSection;
    }

    public short getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getTypeDisplayName() {
        return typeDisplayName;
    }

    public StructureBlueprint getStructureBlueprint() {
        return structureBlueprint;
    }

    public Class<? extends Artillery> getArtilleryClazz() {
        return artilleryClazz;
    }

    public ItemStack getAmmoItemStack() {
        return new ItemStack(ammoType, ammoConsumptionAmount);
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public long getCooldown() {
        return cooldown;
    }

    public int getDefenceRange() {
        return defenceRange;
    }

    public double getArtilleryDamageDefault() {
        return artilleryDamageDefault;
    }

    public Map<String, Double> getArtilleryDamage() {
        return artilleryDamage;
    }

    public ConfigurationSection getConfigurationSection() {
        return configurationSection;
    }

    private ArtilleryCrateItem artilleryCrateItem;

    public ArtilleryCrateItem getArtilleryCrateItem() {
        if (artilleryCrateItem != null) {
            return artilleryCrateItem;
        }
        artilleryCrateItem = CustomItemManager.getCustomItemManager().createCustomItem(ARTILLERY_CRATE_BASE_ID + id, ArtilleryCrateItem.class, new Class[]{ArtilleryType.class}, this);
        return artilleryCrateItem;
    }

    public Artillery instantiateNewArtillery(ArtilleryCore core, BlockFace direction, float health) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Constructor<? extends Artillery> artilleryConstructor = artilleryClazz.getConstructor(ArtilleryCore.class, BlockFace.class, StructureBlueprint.class, long.class, float.class, double.class, Map.class, boolean.class);
        return artilleryConstructor.newInstance(core, direction, structureBlueprint, System.currentTimeMillis(), health, artilleryDamageDefault, artilleryDamage, true);
    }

    public Artillery instantiateExistingArtillery(ArtilleryCore core, BlockFace direction, StructureInstance structureInstance, long creationTime, float health) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<? extends Artillery> artilleryConstructor = artilleryClazz.getConstructor(ArtilleryCore.class, BlockFace.class, StructureInstance.class, long.class, float.class, double.class, Map.class);
        return artilleryConstructor.newInstance(core, direction, structureInstance, creationTime, health, artilleryDamageDefault, artilleryDamage);
    }

    @Override
    public String toString() {
        return "ArtilleryType{" +
                "id=" + id +
                ", typeDisplayName='" + typeDisplayName + '\'' +
                ", structureBlueprint=" + structureBlueprint +
                ", artilleryClazz=" + artilleryClazz +
                ", artilleryCrateItem=" + artilleryCrateItem +
                '}';
    }
}
