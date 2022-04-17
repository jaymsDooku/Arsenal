package dev.jayms.arsenal.artillery;

import dev.jayms.arsenal.Arsenal;
import dev.jayms.arsenal.structure.StructureInstance;
import dev.jayms.arsenal.util.AffineTransform;
import dev.jayms.arsenal.util.BlockFaceTransform;
import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import vg.civcraft.mc.civmodcore.CivModCorePlugin;
import vg.civcraft.mc.civmodcore.dao.ManagedDatasource;
import vg.civcraft.mc.civmodcore.world.locations.chunkmeta.XZWCoord;
import vg.civcraft.mc.civmodcore.world.locations.chunkmeta.block.BlockBasedChunkMeta;
import vg.civcraft.mc.civmodcore.world.locations.chunkmeta.block.table.TableBasedBlockChunkMeta;
import vg.civcraft.mc.civmodcore.world.locations.chunkmeta.block.table.TableBasedDataObject;
import vg.civcraft.mc.civmodcore.world.locations.chunkmeta.block.table.TableStorageEngine;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArtilleryDAO extends TableStorageEngine<Artillery> {

    public ArtilleryDAO(Logger logger, ManagedDatasource db) {
        super(logger, db);

    }

    @Override
    public void registerMigrations() {
        db.registerMigration(1, false,
                "CREATE TABLE IF NOT EXISTS artillery (chunk_x int not null, chunk_z int not null, world_id smallint unsigned not null, " +
                        "x_offset tinyint unsigned not null, y smallint not null, z_offset tinyint unsigned not null, " +
                        "type_id smallint unsigned not null, health float not null, group_id int not null, direction tinyint unsigned not null, " +
                        "blocks_placed_index int not null, " +
                        "creation_time timestamp not null default now(), index artilleryChunkLookUp (chunk_x, chunk_z, world_id), primary key " +
                        "(chunk_x, chunk_z, world_id, x_offset, y, z_offset))");
    }

    @Override
    public void insert(Artillery artillery, XZWCoord coord) {
        try (Connection insertConn = db.getConnection();
             PreparedStatement insertArtillery = insertConn.prepareStatement(
                     "insert into artillery (chunk_x, chunk_z, world_id, x_offset, y, z_offset, type_id, " +
                             "health, group_id, direction, blocks_placed_index) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
             )) {
            setInsertArtilleryStatement(insertArtillery, artillery, coord);
            insertArtillery.execute();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to insert artillery into db: ", e);
        }
    }

    private static void setInsertArtilleryStatement(PreparedStatement insertArtillery, Artillery artillery, XZWCoord coord) throws SQLException {
        insertArtillery.setInt(1, coord.getX());
        insertArtillery.setInt(2, coord.getZ());
        insertArtillery.setShort(3, coord.getWorldID());
        insertArtillery.setByte(4, (byte) BlockBasedChunkMeta.modulo(artillery.getLocation().getBlockX()));
        insertArtillery.setShort(5, (short) artillery.getLocation().getBlockY());
        insertArtillery.setByte(6, (byte) BlockBasedChunkMeta.modulo(artillery.getLocation().getBlockZ()));
        insertArtillery.setShort(7, artillery.getType().getId());
        insertArtillery.setFloat(8, artillery.getHealth());
        insertArtillery.setInt(9, artillery.getGroupId());
        insertArtillery.setInt(10, artillery.getDirection().ordinal());
        insertArtillery.setInt(11, artillery.getStructureInstance() != null ? artillery.getStructureInstance().getBlocksPlacedIndex() : 0);
    }

    @Override
    public void update(Artillery artillery, XZWCoord coord) {
        try (Connection insertConn = db.getConnection();
             PreparedStatement updateArtillery = insertConn.prepareStatement(
                     "update artillery set type_id = ?, health = ?, group_id = ?, direction = ?, blocks_placed_index = ? where "
                             + "chunk_x = ? and chunk_z = ? and world_id = ? and x_offset = ? and y = ? and z_offset = ?;");) {
            setUpdateArtilleryStatement(updateArtillery, artillery, coord);
            updateArtillery.execute();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to update reinforcement in db: ", e);
        }
    }

    private static void setUpdateArtilleryStatement(PreparedStatement updateArtillery, Artillery artillery, XZWCoord coord) throws SQLException {
        updateArtillery.setShort(1, artillery.getType().getId());
        updateArtillery.setFloat(2, artillery.getHealth());
        updateArtillery.setInt(3, artillery.getGroupId());
        updateArtillery.setInt(4, artillery.getDirection().ordinal());
        StructureInstance structureInstance = artillery.getStructureInstance();
        if (structureInstance != null) {
            updateArtillery.setInt(5, structureInstance.getBlocksPlacedIndex());
        } else {
            updateArtillery.setInt(5, -1);
        }
        updateArtillery.setInt(6, coord.getX());
        updateArtillery.setInt(7, coord.getZ());
        updateArtillery.setShort(8, coord.getWorldID());
        updateArtillery.setByte(9, (byte) BlockBasedChunkMeta.modulo(artillery.getLocation().getBlockX()));
        updateArtillery.setShort(10, (short) artillery.getLocation().getBlockY());
        updateArtillery.setByte(11, (byte) BlockBasedChunkMeta.modulo(artillery.getLocation().getBlockZ()));
    }

    @Override
    public void delete(Artillery artillery, XZWCoord coord) {
        try (Connection deleteConn = db.getConnection();
                PreparedStatement deleteArtillery = deleteConn.prepareStatement(
                        "delete from artillery where chunk_x = ? and chunk_z = ? and world_id = ? " +
                                "and x_offset = ? and y = ? and z_offset = ?")) {
                setDeleteArtilleryStatement(deleteArtillery, artillery, coord);
                deleteArtillery.execute();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to delete artillery from db: ", e);
        }
    }

    private static void setDeleteArtilleryStatement(PreparedStatement deleteArtillery, Artillery artillery, XZWCoord coord) throws SQLException {
        deleteArtillery.setInt(1, coord.getX());
        deleteArtillery.setInt(2, coord.getZ());
        deleteArtillery.setShort(3, coord.getWorldID());
        deleteArtillery.setByte(4, (byte) BlockBasedChunkMeta.modulo(artillery.getLocation().getBlockX()));
        deleteArtillery.setShort(5, (short) artillery.getLocation().getBlockY());
        deleteArtillery.setByte(6, (byte) BlockBasedChunkMeta.modulo(artillery.getLocation().getBlockZ()));
    }

    @Override
    public void fill(TableBasedBlockChunkMeta<Artillery> chunkData, Consumer<Artillery> insertFunction) {
        int preMultipliedX = chunkData.getChunkCoord().getX() * 16;
        int preMultipliedZ = chunkData.getChunkCoord().getZ() * 16;
        ArtilleryTypeManager typeMan = Arsenal.getInstance().getArtilleryTypeManager();
        World world = chunkData.getChunkCoord().getWorld();
        try (Connection insertConn = db.getConnection();
             PreparedStatement selectArtillery = insertConn.prepareStatement(
                     "select x_offset, y, z_offset, type_id, group_id, creation_time, health, direction, blocks_placed_index "
                             + "from artillery where chunk_x = ? and chunk_z = ? and world_id = ?;");) {
            selectArtillery.setInt(1, chunkData.getChunkCoord().getX());
            selectArtillery.setInt(2, chunkData.getChunkCoord().getZ());
            selectArtillery.setShort(3, chunkData.getChunkCoord().getWorldID());
            try (ResultSet rs = selectArtillery.executeQuery()) {
                while (rs.next()) {
                    int xOffset = rs.getByte(1);
                    int x = xOffset + preMultipliedX;
                    int y = rs.getShort(2);
                    int zOffset = rs.getByte(3);
                    int z = zOffset + preMultipliedZ;
                    Location loc = new Location(world, x, y, z);
                    short typeID = rs.getShort(4);
                    ArtilleryType type = typeMan.getById(typeID);
                    if (type == null) {
                        logger.log(Level.SEVERE, "Failed to load artillery with type id " + typeID);
                        continue;
                    }
                    int groupID = rs.getInt(5);
                    long creationTime = rs.getTimestamp(6).getTime();
                    float health = rs.getFloat(7);
                    ArtilleryCore core = new ArtilleryCore(type, groupID, health, loc);
                    int direction = rs.getInt(8);
                    BlockFace directionBf = BlockFace.values()[direction];
                    int blocksPlacedIndex = rs.getInt(9);
                    StructureInstance structureInstance = null;
                    if (blocksPlacedIndex != -1) {
                        structureInstance = new StructureInstance(loc, type.getStructureBlueprint(), blocksPlacedIndex,
                                new BlockFaceTransform(type.getStructureBlueprint().getInitialDirection(), directionBf));
                    }
                    Artillery artillery = type.instantiateExistingArtillery(core, directionBf, structureInstance, creationTime);
                    insertFunction.accept(artillery);
                }
            }
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            logger.log(Level.SEVERE, "Failed to load artillery from db: ", e);
        }
    }

    @Override
    public Artillery getForLocation(int x, int y, int z, short worldID, short pluginID) {
        int chunkX = BlockBasedChunkMeta.toChunkCoord(x);
        int chunkZ = BlockBasedChunkMeta.toChunkCoord(z);
        ArtilleryTypeManager typeMan = Arsenal.getInstance().getArtilleryTypeManager();
        try (Connection insertConn = db.getConnection();
             PreparedStatement selectArtillery = insertConn
                     .prepareStatement("select type_id, health, group_id, direction, blocks_placed_index, creation_time "
                             + "from artillery where chunk_x = ? and chunk_z = ? and world_id = ? and x_offset = ? and y = ? and z_offset = ?;");) {
            selectArtillery.setInt(1, chunkX);
            selectArtillery.setInt(2, chunkZ);
            selectArtillery.setShort(3, worldID);
            selectArtillery.setByte(4, (byte) BlockBasedChunkMeta.modulo(x));
            selectArtillery.setShort(5, (short) y);
            selectArtillery.setByte(6, (byte) BlockBasedChunkMeta.modulo(z));
            try (ResultSet rs = selectArtillery.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                short typeID = rs.getShort(1);
                ArtilleryType type = typeMan.getById(typeID);
                if (type == null) {
                    logger.log(Level.SEVERE, "Failed to load artillery with type id " + typeID);
                    return null;
                }
                float health = rs.getFloat(2);
                int groupID = rs.getInt(3);
                int direction = rs.getInt(4);
                BlockFace directionBf = BlockFace.values()[direction];
                int blocksPlacedIndex = rs.getInt(5);
                World world = CivModCorePlugin.getInstance().getWorldIdManager().getWorldByInternalID(worldID);
                Location loc = new Location(world, x, y, z);
                ArtilleryCore core = new ArtilleryCore(type, groupID, health, loc);
                StructureInstance structureInstance = null;
                if (blocksPlacedIndex != -1) {
                    structureInstance = new StructureInstance(loc, type.getStructureBlueprint(), blocksPlacedIndex,
                            new BlockFaceTransform(type.getStructureBlueprint().getInitialDirection(), directionBf));
                }
                long creationTime = rs.getLong(6);
                return type.instantiateExistingArtillery(core, directionBf, structureInstance, creationTime);
            }
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            logger.log(Level.SEVERE, "Failed to load artillery from db: ", e);
            return null;
        }
    }

    @Override
    public Collection<XZWCoord> getAllDataChunks() {
        List<XZWCoord> result = new ArrayList<>();
        try (Connection insertConn = db.getConnection();
             PreparedStatement selectChunks = insertConn.prepareStatement(
                     "select chunk_x, chunk_z, world_id from artillery group by chunk_x, chunk_z, world_id");
             ResultSet rs = selectChunks.executeQuery()) {
            while (rs.next()) {
                int chunkX = rs.getInt(1);
                int chunkZ = rs.getInt(2);
                short worldID = rs.getShort(3);
                result.add(new XZWCoord(chunkX, chunkZ, worldID));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to select populated chunks from db: ", e);
        }
        return result;
    }

    @Override
    public boolean stayLoaded() {
        return false;
    }
}
