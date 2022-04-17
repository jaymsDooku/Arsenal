package dev.jayms.arsenal.structure;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.*;

public class StructureBlueprintRecording {

    private static final Map<UUID, StructureBlueprintRecording> blueprintRecordings = new HashMap<>();

    public static StructureBlueprintRecording getBlueprintRecording(Player player) {
        StructureBlueprintRecording blueprintRecording = blueprintRecordings.get(player.getUniqueId());
        if (blueprintRecording == null) {
            blueprintRecording = new StructureBlueprintRecording();
            blueprintRecordings.put(player.getUniqueId(), blueprintRecording);
        }
        return blueprintRecording;
    }

    public static void loadBlueprintRecording(Player player, String recordingName) {

    }

    private Block origin;
    private List<StructureBlock> structureBlocks = new ArrayList<>();
    private Map<String, Block> namedBlocks = new HashMap<>();

    public void setOrigin(Block block) {
        this.origin = block;
    }

    public void setBlocks(List<Block> blocks) {
        structureBlocks.clear();
        for (Block block : blocks) {
            addStructureBlock(block);
        }
    }

    public void nameBlock(String name, Block block) {
        namedBlocks.put(name, block);
    }

    public Set<Map.Entry<String, Block>> getNamedBlocks() {
        return namedBlocks.entrySet();
    }

    public void addStructureBlock(Block block) {
        int origX = origin.getX();
        int origY = origin.getY();
        int origZ = origin.getZ();

        int blockX = block.getX();
        int blockY = block.getY();
        int blockZ = block.getZ();

        int relX = blockX - origX;
        int relY = blockY - origY;
        int relZ = blockZ - origZ;

        Material material = block.getType();
        String data = block.getBlockData().getAsString();
        StructureBlock structureBlock = StructureBlock.builder(material)
                .pos(relX, relY, relZ)
                .data(data)
                .build();
        structureBlocks.add(structureBlock);
    }

    public List<StructureBlock> getStructureBlocks() {
        return structureBlocks;
    }

    public StructureBlueprint toBlueprint(String name, BlockFace initialDirection) {
        return new StructureBlueprint(name, structureBlocks, initialDirection);
    }

}
