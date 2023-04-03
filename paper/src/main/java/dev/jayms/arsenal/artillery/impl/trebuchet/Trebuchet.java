package dev.jayms.arsenal.artillery.impl.trebuchet;

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
import org.bukkit.util.Vector;

import java.io.ObjectInputFilter;
import java.util.Map;

public class Trebuchet extends Artillery {

    private static final String MIDDLE = "middle";

    private Block middle;

    private double firingAngleThreshold = 150;

    public Trebuchet(ArtilleryCore core, BlockFace blockFace, StructureBlueprint structureBlueprint, long creationTime, float health,
                     double artilleryDamageDefault, Map<String, Double> artilleryDamage, boolean isNew) {
        super(core, blockFace, structureBlueprint, creationTime, health, artilleryDamageDefault, artilleryDamage, isNew);
        initBlockPositions();
        initConfig();
    }

    public Trebuchet(ArtilleryCore core, BlockFace blockFace, StructureInstance structureInstance, long creationTime, float health,
                    double artilleryDamageDefault, Map<String, Double> artilleryDamage) {
        super(core, blockFace, structureInstance, creationTime, health, artilleryDamageDefault, artilleryDamage);
        initBlockPositions();
        initConfig();
    }

    private void initBlockPositions() {
        middle = structureBlueprint.getNamedBlock(core.getBlock(), MIDDLE, directionTransform);
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

    public double getFiringAngleThreshold() {
        return firingAngleThreshold;
    }

    public Block getMiddle() {
        return middle;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.RED + "Trebuchet";
    }

    @Override
    public ArtilleryCategory getCategory() {
        return ArtilleryCategory.TREBUCHET;
    }

    @Override
    public ArtilleryMissileRunner getMissileRunner() {
        return TrebuchetMissileRunner.TREBUCHET_MISSILE_RUNNER;
    }
}
