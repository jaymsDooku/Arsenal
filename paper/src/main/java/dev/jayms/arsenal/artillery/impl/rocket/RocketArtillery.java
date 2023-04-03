package dev.jayms.arsenal.artillery.impl.rocket;

import dev.jayms.arsenal.Arsenal;
import dev.jayms.arsenal.artillery.Artillery;
import dev.jayms.arsenal.artillery.ArtilleryCategory;
import dev.jayms.arsenal.artillery.ArtilleryCore;
import dev.jayms.arsenal.artillery.ArtilleryMissileRunner;
import dev.jayms.arsenal.artillery.impl.field.FieldArtilleryMissileRunner;
import dev.jayms.arsenal.artillery.shooter.Shooter;
import dev.jayms.arsenal.structure.StructureBlueprint;
import dev.jayms.arsenal.structure.StructureInstance;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RocketArtillery extends Artillery {

    private static final String MISSILE_HEAD = "missile_head";
    private static final String MISSILE_TAIL = "missile_tail";

    private List<MissileBody> missileBodies = new ArrayList<>();

    public RocketArtillery(ArtilleryCore core, BlockFace blockFace, StructureBlueprint structureBlueprint, long creationTime, float health,
                          double artilleryDamageDefault, Map<String, Double> artilleryDamage, boolean isNew) {
        super(core, blockFace, structureBlueprint, creationTime, health, artilleryDamageDefault, artilleryDamage, isNew);
        initBlockPositions();
    }

    public RocketArtillery(ArtilleryCore core, BlockFace blockFace, StructureInstance structureInstance, long creationTime, float health,
                          double artilleryDamageDefault, Map<String, Double> artilleryDamage) {
        super(core, blockFace, structureInstance, creationTime, health, artilleryDamageDefault, artilleryDamage);
        initBlockPositions();
    }

    private void initBlockPositions() {
        Map<String, Block> missileHeads = structureBlueprint.getNamedBlocks(core.getBlock(), MISSILE_HEAD, directionTransform);
        Map<String, Block> missileTails = structureBlueprint.getNamedBlocks(core.getBlock(), MISSILE_TAIL, directionTransform);

        for (Map.Entry<String, Block> missileHeadEntry : missileHeads.entrySet()) {
            String missileHeadKey = missileHeadEntry.getKey();
            Block missileHeadBlock = missileHeadEntry.getValue();
            String missileHeadNum = missileHeadKey.substring(missileHeadKey.length() - 1);
            Block missileTailBlock = missileTails.get(MISSILE_TAIL + missileHeadNum);
            missileBodies.add(new MissileBody(missileHeadBlock, missileTailBlock));
        }
    }

    private int missileBodyIndex = 0;

    public MissileBody getNextMissileBody() {
        if (missileBodyIndex >= missileBodies.size()) {
            missileBodyIndex = 0;
        }

        return missileBodies.get(missileBodyIndex++);
    }

    private boolean isFiring = false;
    private int maxShots = 8;
    private int currentShot = 0;

    @Override
    public boolean fire(Shooter shooter, Location target) {
        if (isFiring) {
            shooter.sendMessage(getDisplayName() + " is still firing.");
            return false;
        }

        isFiring = true;
        return fireMissile(shooter, target);
    }

    @Override
    protected boolean fireMissile(Shooter shooter, Location target) {
        if (currentShot >= maxShots) {
            currentShot = 0;
            isFiring = false;
        } else {
            new BukkitRunnable() {

                @Override
                public void run() {
                    fireMissile(shooter, target);
                }

            }.runTaskLater(Arsenal.getInstance(), 10L);
            currentShot++;
        }
        return super.fireMissile(shooter, target);
    }

    @Override
    public String getDisplayName() {
        return ChatColor.RED + "Rocket Artillery";
    }

    @Override
    public ArtilleryCategory getCategory() {
        return ArtilleryCategory.ARTILLERY;
    }

    @Override
    public ArtilleryMissileRunner getMissileRunner() {
        return RocketArtilleryMissileRunner.ROCKET_ARTILLERY_MISSILE_RUNNER;
    }

    public static class MissileBody {

        private Block missileHead;
        private Block missileTail;

        public MissileBody(Block missileHead, Block missileTail) {
            this.missileHead = missileHead;
            this.missileTail = missileTail;
        }

        public Block getMissileHead() {
            return missileHead;
        }

        public Block getMissileTail() {
            return missileTail;
        }
    }

}
