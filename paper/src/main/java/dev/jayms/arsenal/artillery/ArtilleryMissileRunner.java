package dev.jayms.arsenal.artillery;

import com.google.common.collect.Sets;
import dev.jayms.arsenal.artillery.shooter.Shooter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class ArtilleryMissileRunner<T extends ArtilleryMissile> {

    private final Class<T> clazz;
    private final Class<? extends Artillery> artilleryClazz;
    private Set<T> missiles = Sets.newConcurrentHashSet();

    public ArtilleryMissileRunner(Class<T> clazz, Class<? extends Artillery> artilleryClazz) {
        this.clazz = clazz;
        this.artilleryClazz = artilleryClazz;
    }

    public Set<T> getMissiles() {
        return missiles;
    }

    public void haltMissile(T missile) {
        missiles.remove(missile);
    }

    public void update() {
        if (missiles.isEmpty()) return;

        Set<T> toRemove = new HashSet<>();
        for (T missile : missiles) {
            if (update(missile)) {
                toRemove.add(missile);
            }
        }

        if (!toRemove.isEmpty()) {
            missiles.removeAll(toRemove);
        }
    }

    public void fireMissile(Shooter shooter, Artillery artillery, Location target) {
        try {
            Constructor<T> clazzConstructor = clazz.getConstructor(Shooter.class, artilleryClazz, Location.class);
            T missile = clazzConstructor.newInstance(shooter, artillery, target);
            missiles.add(missile);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean update(T missile) {
        return missile.update();
    }

}
