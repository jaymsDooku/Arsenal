package dev.jayms.arsenal.structure;

import com.google.common.collect.Lists;
import dev.jayms.arsenal.util.BlockFaceTransform;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.jayms.arsenal.Arsenal;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class StructureBlueprint {

    private static Map<String, StructureBlueprint> structureBlueprints = new HashMap<>();

    public static List<StructureBlueprint> getStructureBlueprints() {
        return Lists.newArrayList(structureBlueprints.values());
    }

    public static StructureBlueprint getStructureBlueprint(String name) {
        return structureBlueprints.get(name);
    }

    public static StructureBlueprint loadStructureBlueprint(String blueprintName) throws IOException {
        if (structureBlueprints.containsKey(blueprintName)) {
            return structureBlueprints.get(blueprintName);
        }
        File structureFile = getSaveFile(blueprintName);
        if (!structureFile.exists()) {
            Bukkit.getLogger().info(structureFile + " not exist");
            return null;
        }
        Reader reader = Files.newBufferedReader(structureFile.toPath());
        Gson gson = new Gson();
        Map<String, Object> saveMap = gson.fromJson(reader, Map.class);

        if (!saveMap.containsKey("name")) {
            Bukkit.getLogger().info("name not exist");
            return null;
        }
        String name = (String) saveMap.get("name");

        if (!saveMap.containsKey("blocks")) {
            Bukkit.getLogger().info("blocks not exist");
            return null;
        }
        List<StructureBlock> blocks = new ArrayList<>();
        List<Map<String, Object>> blocksList = (List<Map<String, Object>>) saveMap.get("blocks");
        for (Map<String, Object> blockMap : blocksList) {
            blocks.add(StructureBlock.fromMap(blockMap));
        }

        BlockFace initialDirection = BlockFace.SOUTH;
        if (saveMap.containsKey("initial_direction")) {
            initialDirection = BlockFace.valueOf(((String) saveMap.get("initial_direction")).toUpperCase());
        }

        if (!saveMap.containsKey("named_blocks")) {
            Bukkit.getLogger().info("named_blocks not exist");
            return null;
        }
        Map<String, Vector> namedBlockPositions = new HashMap<>();
        Map<String, Map<String, Object>> namedBlocks = (Map<String, Map<String, Object>>) saveMap.get("named_blocks");
        for (Map.Entry<String, Map<String, Object>> namedBlockEntry : namedBlocks.entrySet()) {
            Map<String, Object> vectorMap = namedBlockEntry.getValue();
            int x = ((Double) vectorMap.get("x")).intValue();
            int y = ((Double) vectorMap.get("y")).intValue();
            int z = ((Double) vectorMap.get("z")).intValue();
            Vector vector = new Vector(x, y, z);
            namedBlockPositions.put(namedBlockEntry.getKey(), vector);
        }

        reader.close();

        StructureBlueprint structureBlueprint = new StructureBlueprint(name, blocks, initialDirection, namedBlockPositions);
        return structureBlueprint;
    }

    public static File getSaveFile(String fileName) {
        File structureFolder = Arsenal.getInstance().getStructureFolder();
        File structureFile = new File(structureFolder, fileName + ".json");
        return structureFile;
    }

    private String name;
    private List<StructureBlock> blocks = new ArrayList<>();
    private Map<String, Vector> namedBlockPositions;

    private BlockFace initialDirection;

    public String getName() {
        return name;
    }

    public StructureBlueprint(String name, List<StructureBlock> blocks, BlockFace initialDirection) {
        this(name, blocks, initialDirection, new HashMap<>());
    }

    public StructureBlueprint(String name, List<StructureBlock> blocks, BlockFace initialDirection, Map<String, Vector> namedBlockPositions) {
        this.name = name;
        this.namedBlockPositions = namedBlockPositions;
        this.initialDirection = initialDirection;

        for (StructureBlock block : blocks) {
            putBlock(block);
        }
        Collections.sort(this.blocks, new StructureBlock.StructureBlockComparator());

        structureBlueprints.put(name, this);
    }

    public BlockFace getInitialDirection() {
        return initialDirection;
    }

    private void putBlock(StructureBlock.StructureBlockBuilder structureBlockBuilder) {
        putBlock(structureBlockBuilder.build());
    }

    private void putBlock(StructureBlock structureBlock) {
        blocks.add(structureBlock);
    }

    private Map<BlockFace, Vector> mins = new HashMap<>();
    private Map<BlockFace, Vector> maxs = new HashMap<>();

    public void computeMinMaxes(BlockFaceTransform transform) {
        if (!mins.containsKey(transform.getTo())) {
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int minZ = Integer.MAX_VALUE;
            for (StructureBlock block : blocks) {
                Vector transformed = transform.getAffineTransform().apply(new Vector(block.getRelX(), block.getRelY(), block.getRelZ()));
                minX = Math.min(minX, transformed.getBlockX());
                minY = Math.min(minY, transformed.getBlockY());
                minZ = Math.min(minZ, transformed.getBlockZ());
            }
            mins.put(transform.getTo(), new Vector(minX, minY, minZ));
        }

        if (!maxs.containsKey(transform.getTo())) {
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            int maxZ = Integer.MIN_VALUE;
            for (StructureBlock block : blocks) {
                Vector transformed = transform.getAffineTransform().apply(new Vector(block.getRelX(), block.getRelY(), block.getRelZ()));
                maxX = Math.max(maxX, transformed.getBlockX());
                maxY = Math.max(maxY, transformed.getBlockY());
                maxZ = Math.max(maxZ, transformed.getBlockZ());
            }
            maxs.put(transform.getTo(), new Vector(maxX, maxY, maxZ));
        }
    }

    public int getMinX(BlockFaceTransform transform) {
        computeMinMaxes(transform);
        return mins.get(transform.getTo()).getBlockX();
    }

    public int getMinY(BlockFaceTransform transform) {
        computeMinMaxes(transform);
        return mins.get(transform.getTo()).getBlockY();
    }

    public int getMinZ(BlockFaceTransform transform) {
        computeMinMaxes(transform);
        return mins.get(transform.getTo()).getBlockZ();
    }

    public int getMaxX(BlockFaceTransform transform) {
        computeMinMaxes(transform);
        return maxs.get(transform.getTo()).getBlockX();
    }

    public int getMaxY(BlockFaceTransform transform) {
        computeMinMaxes(transform);
        return maxs.get(transform.getTo()).getBlockY();
    }

    public int getMaxZ(BlockFaceTransform transform) {
        computeMinMaxes(transform);
        return maxs.get(transform.getTo()).getBlockZ();
    }

//    public int getMinX() {
//        return minX;
//    }
//
//    public int getMinY() {
//        return minY;
//    }
//
//    public int getMinZ() {
//        return minZ;
//    }
//
//    public int getMaxX() {
//        return maxX;
//    }
//
//    public int getMaxY() {
//        return maxY;
//    }
//
//    public int getMaxZ() {
//        return maxZ;
//    }

    public Map<String, Vector> getNamedBlockPositions() {
        return namedBlockPositions;
    }

    public StructureBlueprintRecording toRecording() {
        return new StructureBlueprintRecording(blocks, namedBlockPositions);
    }

    public Block getNamedBlock(Block origin, String name, BlockFaceTransform transform) {
        if (!namedBlockPositions.containsKey(name)) {
            return null;
        }
        Vector namedPos = namedBlockPositions.get(name);
        Vector rotatedNamedPos = transform.getAffineTransform().apply(namedPos);
        return origin.getRelative(rotatedNamedPos.getBlockX(), rotatedNamedPos.getBlockY(), rotatedNamedPos.getBlockZ());
    }

    public Map<String, Block> getNamedBlocks(Block origin, String namePart, BlockFaceTransform transform) {
        Map<String, Block> namedBlocks = new HashMap<>();

        for (Map.Entry<String, Vector> namedBlockEntry : namedBlockPositions.entrySet()) {
            if (namedBlockEntry.getKey().contains(namePart)) {
                Vector namedPos = namedBlockEntry.getValue();
                Vector rotatedNamedPos = transform.getAffineTransform().apply(namedPos);
                Block block = origin.getRelative(rotatedNamedPos.getBlockX(), rotatedNamedPos.getBlockY(), rotatedNamedPos.getBlockZ());
                namedBlocks.put(namedBlockEntry.getKey(), block);
            }
        }

        return namedBlocks;
    }

    public List<StructureBlock> getBlocks() {
        return blocks;
    }

    public StructureBlock getBlock(int i) {
        if (i < 0 || i >= blocks.size()) {
            return null;
        }

        return blocks.get(i);
    }

    public File getSaveFile() {
        return getSaveFile(name);
    }

    public void save() throws IOException {
        File structureFile = getSaveFile();
        if (!structureFile.exists()) {
            if (structureFile.createNewFile()) {
                Arsenal.getInstance().getLogger().info("Created " + structureFile.getName());
            }
        }
        Writer writer = new FileWriter(structureFile);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, Object> saveMap = new HashMap<>();
        saveMap.put("name", name);

        List<Map<String, Object>> blocksList = new ArrayList<>();
        for (StructureBlock structureBlock : blocks) {
            Map<String, Object> blockMap = structureBlock.toMap();
            blocksList.add(blockMap);
        }
        saveMap.put("blocks", blocksList);

        saveMap.put("initial_direction", initialDirection.toString());

        Map<String, Map<String, Object>> namedBlockMap = new HashMap<>();
        for (Map.Entry<String, Vector> namedBlockEntry : namedBlockPositions.entrySet()) {
            Map<String, Object> vectorMap = new HashMap<>();
            Vector vec = namedBlockEntry.getValue();
            vectorMap.put("x", vec.getBlockX());
            vectorMap.put("y", vec.getBlockY());
            vectorMap.put("z", vec.getBlockZ());
            namedBlockMap.put(namedBlockEntry.getKey(), vectorMap);
        }
        saveMap.put("named_blocks", namedBlockMap);

        gson.toJson(saveMap, writer);

        writer.close();
    }

}
