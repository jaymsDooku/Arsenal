package dev.jayms.arsenal.structure;

import dev.jayms.arsenal.artillery.ArtilleryCore;
import dev.jayms.arsenal.util.AffineTransform;
import dev.jayms.arsenal.util.BlockFaceTransform;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Structure;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StructurePlacementTask extends BukkitRunnable {

    private static int ID = 1;
    private static Map<Integer, StructurePlacementTask> tasks = new HashMap<>();

    public static Set<Map.Entry<Integer, StructurePlacementTask>> getTasks() {
        return tasks.entrySet();
    }

    public static StructurePlacementTask getTask(int id) {
        return tasks.get(id);
    }

    private final Player placer;
    private StructureInstance instance;
    private boolean forward = true;
    private boolean paused = false;
    private boolean running = false;
    private boolean finished = false;

    public StructurePlacementTask(Player placer, ArtilleryCore core, StructureBlueprint structureBlueprint, BlockFaceTransform blockFaceTransform) {
        this.placer = placer;
        this.instance = new StructureInstance(core.getBlock().getLocation().clone(), structureBlueprint, blockFaceTransform);
        tasks.put(ID++, this);
    }

    public StructurePlacementTask(Player placer, StructureBlueprint structureBlueprint, BlockFaceTransform blockFaceTransform) {
        this.placer = placer;
        this.instance = new StructureInstance(placer.getEyeLocation().clone().add(placer.getEyeLocation().getDirection()), structureBlueprint, blockFaceTransform);

        tasks.put(ID++, this);
    }

    public StructureInstance getInstance() {
        return instance;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public boolean isForward() {
        return forward;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isFinished() {
        return finished;
    }

    private void finish() {
        placer.sendMessage("Finished placement task");
        running = false;
        finished = true;
        cancel();
    }

    @Override
    public void run() {
        if (paused) {
            running = false;
            return;
        }

        running = true;
        if (forward) {
            if (instance.hasNextBlock()) {
                instance.placeNextBlock();
            } else {
                finish();
            }
        } else {
            if (instance.hasPreviousBlock()) {
                instance.destroyPreviousBlock();
            } else {
                finish();
            }
        }
    }

}
