package dev.jayms.arsenal.artillery;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class ArtilleryTypeManager {

    private Map<Short, ArtilleryType> typesById;
    private Map<String, ArtilleryType> typesByKey;

    public ArtilleryTypeManager() {
        typesById = new TreeMap<>();
        typesByKey = new TreeMap<>();
    }

    public Collection<ArtilleryType> getAllTypes() {
        return typesById.values();
    }

    public ArtilleryType getById(short id) {
        //System.out.println("TYPES: " + typesById);
        return typesById.get(id);
    }

    public ArtilleryType getByKey(String key) {
        return typesByKey.get(key);
    }

    public void register(ArtilleryType type) {
        typesById.put(type.getId(), type);
        typesByKey.put(type.getKey(), type);
    }

}
