package dev.jayms.arsenal.artillery;

import dev.jayms.arsenal.Arsenal;
import dev.jayms.arsenal.artillery.shooter.AutoShooter;
import dev.jayms.arsenal.artillery.shooter.PlayerShooter;
import dev.jayms.arsenal.artillery.shooter.Shooter;
import dev.jayms.arsenal.structure.StructureBlueprint;
import dev.jayms.arsenal.structure.StructureInstance;
import dev.jayms.arsenal.structure.StructurePlacementTask;
import dev.jayms.arsenal.util.BlockFaceTransform;
import dev.jayms.arsenal.util.RotAxis;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import vg.civcraft.mc.civmodcore.world.locations.QTBox;
import vg.civcraft.mc.civmodcore.world.locations.chunkmeta.block.table.TableBasedDataObject;

import java.text.DecimalFormat;
import java.util.*;

public abstract class Artillery extends TableBasedDataObject implements QTBox {

    protected StructureBlueprint structureBlueprint;
    protected long creationTime;

    protected ArtilleryCore core;
    protected BlockFace direction;
    protected BlockFaceTransform directionTransform;

    protected StructurePlacementTask structurePlacementTask;
    protected BukkitTask structurePlacementBukkitTask;
    protected StructureInstance structureInstance;

    protected double artilleryDamageDefault;
    protected Map<String, Double> artilleryDamage;

    private int impactRadius;
    private int reinforcementDamage;
    private int bastionDamage;
    private int playerDamage;

    private double horizontalKb;
    private double verticalKb;

    private ArtilleryDefenceRange artilleryDefenceRange;
    private Set<UUID> respondedToMissiles = new HashSet<>();

    private float health;
    private boolean firing;
    private long lastFiredTime;
    protected double firingPower = 2;
    protected double hAngle;
    protected double vAngle;

    private static final double MAX_HANGLE = 30;
    private static final double MAX_VANGLE = 30;

    protected Artillery(ArtilleryCore core, BlockFace direction, StructureBlueprint structureBlueprint, long creationTime, float health,
                        double artilleryDamageDefault, Map<String, Double> artilleryDamage, boolean isNew) {
        super(core.getBlock().getLocation(), isNew);
        this.core = core;
        this.creationTime = creationTime;
        this.health = health;
        this.artilleryDamageDefault = artilleryDamageDefault;
        this.artilleryDamage = artilleryDamage;
        this.direction = direction;
        this.structureBlueprint = structureBlueprint;
        this.directionTransform = new BlockFaceTransform(structureBlueprint.getInitialDirection(), direction);
        initConfig();
    }

    protected Artillery(ArtilleryCore core, BlockFace direction, StructureInstance instance, long creationTime, float health,
                        double artilleryDamageDefault, Map<String, Double> artilleryDamage) {
        this(core, direction, instance.getStructureBlueprint(), creationTime, health, artilleryDamageDefault, artilleryDamage, false);
        this.structureInstance = instance;
    }

    private void initConfig() {
        ConfigurationSection configurationSection = getType().getConfigurationSection();
        ConfigurationSection impactSection = configurationSection.getConfigurationSection("impact");
        impactRadius = impactSection.getInt("radius");
        reinforcementDamage = impactSection.getInt("reinforcement-damage");
        bastionDamage = impactSection.getInt("bastion-damage");
        playerDamage = impactSection.getInt("player-damage");

        ConfigurationSection knockbackSection = impactSection.getConfigurationSection("knockback");
        horizontalKb = knockbackSection.getDouble("horizontal");
        verticalKb = knockbackSection.getDouble("vertical");
    }

    private boolean defenceOn;

    public boolean isDefenceOn() {
        return defenceOn;
    }

    public void toggleDefence() {
        defenceOn = !defenceOn;
        ArtilleryManager artilleryManager = Arsenal.getInstance().getArtilleryManager();
        if (defenceOn) {
            artilleryManager.putArtilleryDefence(this);
        } else {
            artilleryManager.removeArtilleryDefence(this);
        }
    }

    public ArtilleryDefenceRange getDefenceRange() {
        if (artilleryDefenceRange == null) {
            artilleryDefenceRange = new ArtilleryDefenceRange(this);
        }
        return artilleryDefenceRange;
    }

    public void respondToMissile(ArtilleryMissile missile) {
        UUID missileId = missile.getId();
        if (respondedToMissiles.contains(missileId)) {
            return;
        }
        respondedToMissiles.add(missileId);

        Artillery attackingArtillery = missile.getArtillery();
        fire(new AutoShooter(), attackingArtillery.getLocation());
    }

    private DecimalFormat df = new DecimalFormat("#.#");

    public boolean fire(Player player, Location target) {
        return fire(new PlayerShooter(player), target);
    }

    public boolean fire(Shooter shooter, Location target) {
        if (!(shooter instanceof AutoShooter)) {
            if (System.currentTimeMillis() < lastFiredTime + getType().getCooldown()) {
                long timeLeft = (lastFiredTime + getType().getCooldown()) - System.currentTimeMillis();
                double secLeft = timeLeft / 1000L;
                shooter.sendMessage(getDisplayName() + " is still cooling down. Time left: " + df.format(secLeft) + "s");
                return false;
            }
        }

        return fireMissile(shooter, target);
    }

    protected boolean fireMissile(Shooter shooter, Location target) {
        if (!core.hasAmmo()) {
            shooter.sendMessage(getDisplayName() + " is out of ammo.");
            return false;
        }

        if (target != null) {
            Location coreLoc = core.getBlock().getLocation();
            Vector artilleryDir = direction.getDirection();
            Vector targetDir = target.toVector().subtract(coreLoc.toVector());
            double angle = artilleryDir.angle(targetDir);
            double angleDegrees = Math.toDegrees(angle);
            if (angle > MAX_HANGLE || angleDegrees > MAX_HANGLE) {
                shooter.sendMessage("Target is out of angle range.");
                return false;
            }
        }

        setFiring(true);

        core.consumeAmmo();

        getMissileRunner().fireMissile(shooter, this, target);
        shooter.sendMessage("You have fired the " + getDisplayName());
        lastFiredTime = System.currentTimeMillis();
        return true;
    }

    public boolean isBuilding() {
        return structurePlacementTask != null && !structurePlacementTask.isPaused() && structurePlacementTask.isRunning() && !structurePlacementTask.isFinished();
    }

    public void startAssembly(Player placer) {
        if (structurePlacementTask != null) {
            structurePlacementTask.setForward(true);
            structurePlacementTask.setPaused(false);
            return;
        }

        placer.sendMessage("Starting assembly...");
        structurePlacementTask = new StructurePlacementTask(placer, this,
                directionTransform,
                (instance) -> setStructureInstance(instance));
        structurePlacementBukkitTask = structurePlacementTask.runTaskTimer(Arsenal.getInstance(), 0L , 1L);
    }

    public void pauseAssembly() {
        if (structurePlacementTask == null) return;

        structurePlacementTask.setPaused(true);
    }

    public boolean isAssembling() {
        return isBuilding() && structurePlacementTask.isForward();
    }

    public void startDisassembly(Player disassembler) {
        if (structureInstance == null) return;

        if (isBuilding()) {
            if (!isAssembling()) {
                return;
            }

            structurePlacementTask.finish();
        }

        structurePlacementTask = new StructurePlacementTask(disassembler, structureInstance,
                false, (instance) -> getOwningCache().remove(core.getBlock()));
        structurePlacementTask.setForward(false);
        structurePlacementBukkitTask = structurePlacementTask.runTaskTimer(Arsenal.getInstance(), 0L, 1L);
    }

    public void pauseDisassembly() {
        structurePlacementTask.setPaused(true);
    }

    public boolean isDisassembling() {
        return isBuilding() && !structurePlacementTask.isForward();
    }

    public boolean isAssembled() {
        return structureInstance != null;
    }

    public BlockFace getDirection() {
        return direction;
    }

    public BlockFaceTransform getDirectionTransform() {
        return directionTransform;
    }

    public World getWorld() {
        return core.getLocation().getWorld();
    }

    public abstract String getDisplayName();

    public abstract ArtilleryCategory getCategory();

    public abstract ArtilleryMissileRunner getMissileRunner();

    public int getGroupId() {
        return core.getGroupId();
    }

    public float getHealth() {
        return health;
    }

    public int getImpactRadius() {
        return impactRadius;
    }

    public int getReinforcementDamage() {
        return reinforcementDamage;
    }

    public int getBastionDamage() {
        return bastionDamage;
    }

    public int getPlayerDamage() {
        return playerDamage;
    }

    public double getHorizontalKb() {
        return horizontalKb;
    }

    public double getVerticalKb() {
        return verticalKb;
    }

    public boolean damage(float damage) {
        if (damage < 0) {
            return false;
        }
        health -= damage;
        if (health < 0) {
            health = 0;
            destroy();
            return true;
        }
        return false;
    }

    public void destroy() {
        if (isAssembled()) {
            startDisassembly(null);
        }
        ArtilleryManager artilleryManager = Arsenal.getInstance().getArtilleryManager();
        artilleryManager.destroyArtillery(this);
        Bukkit.broadcastMessage("Artillery@" + getLocation().toVector() + " has been destroyed!");
    }

    public ArtilleryType getType() {
        return core.getType();
    }

    public ArtilleryCore getCore() {
        return core;
    }

    public Double getArtilleryDamage(String key) {
        if (!artilleryDamage.containsKey(key)) {
            return artilleryDamageDefault;
        }
        return artilleryDamage.get(key);
    }

    public StructureBlueprint getStructureBlueprint() {
        return structureBlueprint;
    }

    public StructureInstance getStructureInstance() {
        return structureInstance;
    }

    public void setStructureInstance(StructureInstance structureInstance) {
        this.structureInstance = structureInstance;
    }

    public boolean isFiring() {
        return firing;
    }

    public void setFiring(boolean firing) {
        this.firing = firing;
    }

    public double getFiringPower() {
        return firingPower;
    }

    public void setFiringPower(double firingPower) {
        this.firingPower = firingPower;
    }

    public void increaseFiringPower(Player player) {
        firingPower += 0.5;
        if (firingPower > 10) {
            firingPower = 10;
        }
        player.sendMessage("Firing power: " + firingPower);
    }

    public void decreaseFiringPower(Player player) {
        firingPower -= 0.5;
        if (firingPower < 1) {
            firingPower = 1;
        }
        player.sendMessage("Firing power: " + firingPower);
    }

    public double gethAngle() {
        return hAngle;
    }

    public void sethAngle(double hAngle) {
        this.hAngle = hAngle;
    }

    public void increasehAngle(Player player) {
        hAngle += 1;
        if (hAngle > MAX_HANGLE) {
            hAngle = 30;
        }
        player.sendMessage("Horizontal Angle: " + hAngle);
    }

    public void decreasehAngle(Player player) {
        hAngle -= 1;
        if (hAngle < -MAX_HANGLE) {
            hAngle = -30;
        }
        player.sendMessage("Horizontal Angle: " + hAngle);
    }

    public double getvAngle() {
        return vAngle;
    }

    public void setvAngle(double vAngle) {
        this.vAngle = vAngle;
    }

    public void increasevAngle(Player player) {
        vAngle += 1;
        if (vAngle > MAX_VANGLE) {
            vAngle = 30;
        }
        player.sendMessage("Vertical Angle: " + vAngle);
    }

    public void decreasevAngle(Player player) {
        vAngle -= 1;
        if (vAngle < -MAX_VANGLE) {
            vAngle = -30;
        }
        player.sendMessage("Vertical Angle: " + vAngle);
    }

//    public Vector getStructureMin() {
//        Vector structureMin = new Vector(structureBlueprint.getMinX(), 0, structureBlueprint.getMinZ());
//        Vector rotatedStructureMin = directionTransform.getAffineTransform().apply(structureMin);
//        return rotatedStructureMin;
//    }
//
//    public Vector getStructureMax() {
//        Vector structureMax = new Vector(structureBlueprint.getMaxX(), structureBlueprint.getMaxY(), structureBlueprint.getMaxZ());
//        Vector rotatedStructureMax = directionTransform.getAffineTransform().apply(structureMax);
//        return rotatedStructureMax;
//    }

    @Override
    public int qtXMin() {
//        RotAxis axis = RotAxis.getAxis(direction);
//        if (axis == RotAxis.X) {
//            return core.getLocation().getBlockX() + structureBlueprint.getMinX();
//        } else {
//            return core.getLocation().getBlockX() + structureBlueprint.getMinZ();
//        }
        // return core.getLocation().getBlockX() + structureInstance.getMinX();
        return core.getLocation().getBlockX() + structureBlueprint.getMinX(directionTransform);
    }

    @Override
    public int qtXMid() {
        return core.getLocation().getBlockX();
    }

    @Override
    public int qtXMax() {
//        RotAxis axis = RotAxis.getAxis(direction);
//        if (axis == RotAxis.X) {
//            return core.getLocation().getBlockX() + structureBlueprint.getMaxX();
//        } else {
//            return core.getLocation().getBlockX() + structureBlueprint.getMaxZ();
//        }

        // return core.getLocation().getBlockX() + structureInstance.getMaxX();
        return core.getLocation().getBlockX() + structureBlueprint.getMaxX(directionTransform);
    }

    @Override
    public int qtZMin() {
//        RotAxis axis = RotAxis.getAxis(direction);
//        if (axis == RotAxis.X) {
//            return core.getLocation().getBlockZ() + structureBlueprint.getMinZ();
//        } else {
//            return core.getLocation().getBlockZ() + structureBlueprint.getMinX();
//        }
        // return core.getLocation().getBlockX() + structureInstance.getMinZ();
        return core.getLocation().getBlockZ() + structureBlueprint.getMinZ(directionTransform);
    }

    @Override
    public int qtZMid() {
        return core.getLocation().getBlockZ();
    }

    @Override
    public int qtZMax() {
//        RotAxis axis = RotAxis.getAxis(direction);
//        if (axis == RotAxis.X) {
//            return core.getLocation().getBlockZ() + structureBlueprint.getMaxZ();
//        } else {
//            return core.getLocation().getBlockZ() + structureBlueprint.getMaxX();
//        }
        // return core.getLocation().getBlockX() + structureInstance.getMaxZ();
        return core.getLocation().getBlockZ() + structureBlueprint.getMaxZ(directionTransform);
    }

    public int getMinY() {
        // return core.getLocation().getBlockY() + structureInstance.getMinY();
        return core.getLocation().getBlockY() + structureBlueprint.getMinY(directionTransform);
    }

    public int getMaxY() {
        // return core.getLocation().getBlockY() + structureInstance.getMaxY();
        return core.getLocation().getBlockY() + structureBlueprint.getMaxY(directionTransform);
    }

    public boolean withinYLevel(Location loc) {
        return loc.getY() >= getMinY() && loc.getY() <= getMaxY();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artillery artillery = (Artillery) o;
        return Objects.equals(core, artillery.core);
    }

    @Override
    public int hashCode() {
        return Objects.hash(core);
    }
}
