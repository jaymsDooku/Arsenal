package dev.jayms.arsenal.artillery;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ArtilleryTypeManager {

    private Map<Short, ArtilleryType> typesById;

    public ArtilleryTypeManager() {
        typesById = new TreeMap<>();
    }

    public Collection<ArtilleryType> getAllTypes() {
        return typesById.values();
    }

    public ArtilleryType getById(short id) {
        return typesById.get(id);
    }

    public void register(ArtilleryType type) {
        typesById.put(type.getId(), type);
    }

}
