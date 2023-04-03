package dev.jayms.arsenal.structure;

import dev.jayms.arsenal.artillery.Artillery;
import dev.jayms.arsenal.artillery.ArtilleryCore;
import dev.jayms.arsenal.util.BlockFaceTransform;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class StructurePlacementTask extends BukkitRunnable {

    private static int ID = 1;
    private static Map<Integer, StructurePlacementTask> tasks = new HashMap<>();

    public static Set<Map.Entry<Integer, StructurePlacementTask>> getTasks() {
        return tasks.entrySet();
    }

    public static StructurePlacementTask getTask(int id) {
        return tasks.get(id);
    }

    private Player placer;
    private StructureInstance instance;
    private boolean forward = true;
    private boolean paused = false;
    private boolean running = false;
    private boolean finished = false;
    private Consumer<StructureInstance> onFinish;

    public StructurePlacementTask(Player placer, Artillery artillery, BlockFaceTransform blockFaceTransform, Consumer<StructureInstance> onFinish) {
        ArtilleryCore core = artillery.getCore();
        StructureBlueprint structureBlueprint = artillery.getStructureBlueprint();

        this.placer = placer;
        this.instance = new StructureInstance(core.getBlock().getLocation().clone(), structureBlueprint, blockFaceTransform);
        this.onFinish = onFinish;
        tasks.put(ID++, this);
    }

    public StructurePlacementTask(Player placer, StructureBlueprint structureBlueprint, BlockFaceTransform blockFaceTransform) {
        this.placer = placer;
        this.instance = new StructureInstance(placer.getEyeLocation().clone().add(placer.getEyeLocation().getDirection()), structureBlueprint, blockFaceTransform);

        tasks.put(ID++, this);
    }

    public StructurePlacementTask(Player placer, StructureInstance instance, boolean forward, Consumer<StructureInstance> onFinish) {
        this.placer = placer;
        this.instance = instance;
        this.forward = forward;
        this.onFinish = onFinish;
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

    public void finish() {
        if (placer != null) {
            placer.sendMessage("Finished placement task");
        }
        running = false;
        finished = true;
        if (onFinish != null) {
            onFinish.accept(instance);
        }
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
