package dev.jayms.arsenal.item;

import dev.jayms.arsenal.Arsenal;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class CustomItemManager implements Listener {

	private static final CustomItemManager customItemManager = new CustomItemManager();
	
	public static CustomItemManager getCustomItemManager() {
		return customItemManager;
	}
	
	private CustomItemManager() {
	}
	
	private Map<Integer, CustomItem> customItems = new ConcurrentHashMap<>();
	
	private boolean registered = false;

    public void registerListener() {
        if (!registered) {
            Bukkit.getPluginManager().registerEvents(this, Arsenal.getInstance());
            registered = true;
        }
    }

    public <T extends CustomItem> T createCustomItem(int id, Class<T> clazz) {
        return createCustomItem(id, clazz, new Class[]{});
    }

    public <T extends CustomItem> T createCustomItem(int id, Class<T> clazz, Class<?>[] argTypes, Object... args) {
        T customItem = null;
        try {
            Class<?>[] allArgTypes = new Class<?>[argTypes.length + 1];
            allArgTypes[0] = int.class;
            for (int i = 0; i < argTypes.length; i++) {
                allArgTypes[i + 1] = argTypes[i];
            }
            System.out.println(Arrays.toString(allArgTypes));
            Constructor<T> constructor = clazz.getConstructor(allArgTypes);
            Object[] allArgs = new Object[args.length + 1];
            allArgs[0] = id;
            for (int i = 0; i < args.length; i++) {
                allArgs[i + 1] = args[i];
            }
            if (args.length > 0) {
                customItem = constructor.newInstance(allArgs);
            } else {
                customItem = constructor.newInstance(id);
            }
            System.out.println(customItem);
            customItems.put(customItem.getID(), customItem);

            registerListener();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return customItem;
    }

	/*public CustomItem createCustomItem(int id, Class<? extends CustomItem> clazz) {
		CustomItem customItem = null;
		try {
			Constructor<? extends CustomItem> constructor = clazz.getConstructor(int.class);
			customItem = constructor.newInstance(id);
			customItems.put(customItem.getID(), customItem);
				
			if (!registered) {
				Bukkit.getPluginManager().registerEvents(this, Arsenal.getInstance());
				registered = true;
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return customItem;
	}*/
	
	public <T extends CustomItem> T getCustomItem(int id, Class<T> clazz) {
		T customItem = null;
		for (Entry<Integer, CustomItem> ciEn : customItems.entrySet()) {
			if (ciEn.getValue().getClass().equals(clazz)) {
				customItem = (T) ciEn.getValue();
			}
		}
		
		if (customItem == null) {
			customItem = createCustomItem(id, clazz);
		}
		
		return customItem;
	}
	
	public CustomItem getCustomItem(ItemStack itemStack) {
		int id = CustomItem.getCustomItemID(itemStack);
		if (id == -1) {
			return null;
		}
		return customItems.get(id);
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (e.getHand() != EquipmentSlot.HAND) {
			return;
		}
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		
		if (item == null || item.getType() == Material.AIR) return;
		
		int customItemID = CustomItem.getCustomItemID(item);
		if (customItemID == -1) return;
		if (!(customItems.containsKey(customItemID))) return;
		
		CustomItem customItem = customItems.get(customItemID);
		if (customItem == null) return;

		Runnable runnable = null;
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			runnable = customItem.getRightClick(e);
			e.setCancelled(customItem.preventOnRightClick());
		}
		if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			runnable = customItem.getLeftClick(e);
			e.setCancelled(customItem.preventOnLeftClick());
		}
		
		if (runnable == null) return;
		
		Bukkit.getScheduler().runTask(Arsenal.getInstance(), runnable);
	}
	
	@EventHandler
	public void onSwitchSlot(PlayerItemHeldEvent e) {
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItem(e.getNewSlot());
		
		if (item == null || item.getType() == Material.AIR) return;
		
		int customItemID = CustomItem.getCustomItemID(item);
		if (customItemID == -1) return;
		if (!(customItems.containsKey(customItemID))) return;
		
		CustomItem customItem = customItems.get(customItemID);
		if (customItem == null) return;
		Runnable runnable = customItem.onSwitchSlot(e);
		
		if (runnable == null) return;
		
		Bukkit.getScheduler().runTask(Arsenal.getInstance(), runnable);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (e.getHand() != EquipmentSlot.HAND) {
			return;
		}
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		
		if (item == null || item.getType() == Material.AIR) return;
		
		int customItemID = CustomItem.getCustomItemID(item);
		if (customItemID == -1) return;
		if (!(customItems.containsKey(customItemID))) return;
		
		CustomItem customItem = customItems.get(customItemID);
		if (customItem == null) return;
		
		if (customItem.preventOnBlockPlace(e)) {
			e.setCancelled(true);
			return;
		}
		
		Runnable runnable = customItem.onBlockPlace(e);
		
		if (runnable == null) return;
		
		Bukkit.getScheduler().runTask(Arsenal.getInstance(), runnable);
	}

}
