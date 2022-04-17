package dev.jayms.arsenal.artillery;

import dev.jayms.arsenal.item.CustomItemManager;
import dev.jayms.arsenal.structure.StructureBlueprint;
import dev.jayms.arsenal.structure.StructureInstance;
import org.bukkit.block.BlockFace;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ArtilleryType {

    private static final int ARTILLERY_CRATE_BASE_ID = 1000;

    private short id;
    private String typeDisplayName;
    private StructureBlueprint structureBlueprint;
    private Class<? extends Artillery> artilleryClazz;

    public ArtilleryType(short id, String typeDisplayName, StructureBlueprint structureBlueprint, Class<? extends Artillery> artilleryClazz) {
        this.id = id;
        this.typeDisplayName = typeDisplayName;
        this.structureBlueprint = structureBlueprint;
        this.artilleryClazz = artilleryClazz;
    }

    public short getId() {
        return id;
    }

    public String getTypeDisplayName() {
        return typeDisplayName;
    }

    public StructureBlueprint getStructureBlueprint() {
        return structureBlueprint;
    }

    public Class<? extends Artillery> getArtilleryClazz() {
        return artilleryClazz;
    }

    private ArtilleryCrateItem artilleryCrateItem;

    public ArtilleryCrateItem getArtilleryCrateItem() {
        if (artilleryCrateItem != null) {
            return artilleryCrateItem;
        }
        artilleryCrateItem = CustomItemManager.getCustomItemManager().createCustomItem(ARTILLERY_CRATE_BASE_ID + id, ArtilleryCrateItem.class, new Class[]{ArtilleryType.class}, this);
        return artilleryCrateItem;
    }

    public Artillery instantiateNewArtillery(ArtilleryCore core, BlockFace direction) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Constructor<? extends Artillery> artilleryConstructor = artilleryClazz.getConstructor(ArtilleryCore.class, BlockFace.class, StructureBlueprint.class, long.class, boolean.class);
        return artilleryConstructor.newInstance(core, direction, structureBlueprint, System.currentTimeMillis(), true);
    }

    public Artillery instantiateExistingArtillery(ArtilleryCore core, BlockFace direction, StructureInstance structureInstance, long creationTime) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<? extends Artillery> artilleryConstructor = artilleryClazz.getConstructor(ArtilleryCore.class, BlockFace.class, StructureInstance.class, long.class);
        return artilleryConstructor.newInstance(core, direction, structureInstance, creationTime);
    }

    @Override
    public String toString() {
        return "ArtilleryType{" +
                "id=" + id +
                ", typeDisplayName='" + typeDisplayName + '\'' +
                ", structureBlueprint=" + structureBlueprint +
                ", artilleryClazz=" + artilleryClazz +
                ", artilleryCrateItem=" + artilleryCrateItem +
                '}';
    }
}
