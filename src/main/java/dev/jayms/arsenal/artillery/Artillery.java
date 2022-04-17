package dev.jayms.arsenal.artillery;

import dev.jayms.arsenal.Arsenal;
import dev.jayms.arsenal.structure.StructureBlueprint;
import dev.jayms.arsenal.structure.StructureInstance;
import dev.jayms.arsenal.structure.StructurePlacementTask;
import dev.jayms.arsenal.util.AffineTransform;
import dev.jayms.arsenal.util.BlockFaceTransform;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import vg.civcraft.mc.civmodcore.world.locations.chunkmeta.block.table.TableBasedDataObject;

public abstract class Artillery extends TableBasedDataObject {

    protected StructureBlueprint structureBlueprint;
    protected long creationTime;

    protected ArtilleryCore core;
    protected BlockFace direction;

    protected StructurePlacementTask structurePlacementTask;
    protected BukkitTask structurePlacementBukkitTask;
    protected StructureInstance structureInstance;

    protected double firingPower;
    protected double hAngle;
    protected double vAngle;

    protected Artillery(ArtilleryCore core, BlockFace direction, StructureBlueprint structureBlueprint, long creationTime, boolean isNew) {
        super(core.getBlock().getLocation(), isNew);
        this.core = core;
        this.creationTime = creationTime;
        this.direction = direction;
        this.structureBlueprint = structureBlueprint;
    }

    protected Artillery(ArtilleryCore core, BlockFace direction, StructureInstance instance, long creationTime) {
        this(core, direction, instance.getStructureBlueprint(), creationTime, false);
        this.structureInstance = instance;
    }

    public boolean isBuilding() {
        return structurePlacementTask != null && !structurePlacementTask.isPaused();
    }

    public void startAssembly(Player placer) {
        if (structurePlacementTask != null) {
            structurePlacementTask.setForward(true);
            structurePlacementTask.setPaused(false);
            return;
        }

        placer.sendMessage("Starting assembly...");
        structurePlacementTask = new StructurePlacementTask(placer, core, structureBlueprint,
                new BlockFaceTransform(structureBlueprint.getInitialDirection(), direction));
        structurePlacementBukkitTask = structurePlacementTask.runTaskTimer(Arsenal.getInstance(), 0L , 1L);
    }

    public void pauseAssembly() {
        if (structurePlacementTask == null) return;

        structurePlacementTask.setPaused(true);
    }

    public boolean isAssembling() {
        return isBuilding() && structurePlacementTask.isForward();
    }

    public void startDisassembly() {
        if (structurePlacementTask == null) return;

        structurePlacementTask.setForward(false);
        structurePlacementTask.setPaused(true);
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

    public abstract String getDisplayName();

    public abstract ArtilleryCategory getCategory();

    public int getGroupId() {
        return core.getGroupId();
    }

    public float getHealth() {
        return core.getHealth();
    }

    public ArtilleryType getType() {
        return core.getType();
    }

    public StructureInstance getStructureInstance() {
        return structureInstance;
    }
}
