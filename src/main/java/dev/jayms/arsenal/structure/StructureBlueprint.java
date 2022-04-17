package dev.jayms.arsenal.structure;

import com.google.common.collect.Lists;
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
            int x = (int) vectorMap.get("x");
            int y = (int) vectorMap.get("y");
            int z = (int) vectorMap.get("z");
            Vector vector = new Vector(x, y, z);
            namedBlockPositions.put(namedBlockEntry.getKey(), vector);
        }

        reader.close();

        StructureBlueprint structureBlueprint = new StructureBlueprint(name, blocks, initialDirection, namedBlockPositions);
        return structureBlueprint;
    }

    public static File getSaveFile(String blueprintName) {
        File structureFolder = Arsenal.getInstance().getStructureFolder();
        File structureFile = new File(structureFolder, blueprintName + ".json");
        return structureFile;
    }

    private String name;
    private List<StructureBlock> blocks = new ArrayList<>();
    private Map<String, Vector> namedBlockPositions;

    private int minX;
    private int minY;
    private int minZ;

    private int maxX;
    private int maxY;
    private int maxZ;

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
        int relX = structureBlock.getRelX();
        int relY = structureBlock.getRelY();
        int relZ = structureBlock.getRelZ();

        minX = Math.min(minX, relX);
        minY = Math.min(minY, relY);
        minZ = Math.min(minZ, relZ);

        maxX = Math.max(maxX, relX);
        maxY = Math.max(maxY, relY);
        maxZ = Math.max(maxZ, relZ);

        blocks.add(structureBlock);
    }

    public Map<String, Vector> getNamedBlockPositions() {
        return namedBlockPositions;
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
