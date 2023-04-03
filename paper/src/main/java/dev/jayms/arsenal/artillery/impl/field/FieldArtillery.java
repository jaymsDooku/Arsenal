package dev.jayms.arsenal.artillery.impl.field;

import dev.jayms.arsenal.artillery.Artillery;
import dev.jayms.arsenal.artillery.ArtilleryCategory;
import dev.jayms.arsenal.artillery.ArtilleryCore;
import dev.jayms.arsenal.artillery.ArtilleryMissileRunner;
import dev.jayms.arsenal.structure.StructureBlueprint;
import dev.jayms.arsenal.structure.StructureInstance;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class FieldArtillery extends Artillery {

    private static final String MUZZLE = "muzzle";
    private static final String RECUPERATOR = "recuperator";
    private Block muzzle;
    private Block recuperator;

    public FieldArtillery(ArtilleryCore core, BlockFace blockFace, StructureBlueprint structureBlueprint, long creationTime, float health,
                          double artilleryDamageDefault, Map<String, Double> artilleryDamage, boolean isNew) {
        super(core, blockFace, structureBlueprint, creationTime, health, artilleryDamageDefault, artilleryDamage, isNew);
        initBlockPositions();
        initConfig();
    }

    public FieldArtillery(ArtilleryCore core, BlockFace blockFace, StructureInstance structureInstance, long creationTime, float health,
                          double artilleryDamageDefault, Map<String, Double> artilleryDamage) {
        super(core, blockFace, structureInstance, creationTime, health, artilleryDamageDefault, artilleryDamage);
        initBlockPositions();
        initConfig();
    }

    private void initBlockPositions() {
        muzzle = structureBlueprint.getNamedBlock(core.getBlock(), MUZZLE, directionTransform);
        recuperator = structureBlueprint.getNamedBlock(core.getBlock(), RECUPERATOR, directionTransform);
    }

    private void initConfig() {
        ConfigurationSection configurationSection = getType().getConfigurationSection();
//        ConfigurationSection impactSection = configurationSection.getConfigurationSection("impact");
//        impactRadius = impactSection.getInt("radius");
//        reinforcementDamage = impactSection.getInt("reinforcement-damage");
//        bastionDamage = impactSection.getInt("bastion-damage");
//        playerDamage = impactSection.getInt("player-damage");
//
//        ConfigurationSection knockbackSection = impactSection.getConfigurationSection("knockback");
//        horizontalKb = knockbackSection.getDouble("horizontal");
//        verticalKb = knockbackSection.getDouble("vertical");
    }

    public Block getMuzzle() {
        return muzzle;
    }

    public Block getRecuperator() {
        return recuperator;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.RED + "Field Artillery";
    }

    @Override
    public ArtilleryCategory getCategory() {
        return ArtilleryCategory.ARTILLERY;
    }

    @Override
    public ArtilleryMissileRunner getMissileRunner() {
        return FieldArtilleryMissileRunner.FIELD_ARTILLERY_MISSILE_RUNNER;
    }
}
