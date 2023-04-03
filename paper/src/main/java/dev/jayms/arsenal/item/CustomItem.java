package dev.jayms.arsenal.item;

import net.minecraft.nbt.CompoundTag;

import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.inventory.items.ItemBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class CustomItem {
	
	public static final String NBT_CUSTOM_ITEM_ID = "custom-item-id";
	private static final Random rng = new Random();

	private final int ID;
	
	public int getID() {
		return ID;
	}
	
	protected CustomItem(int ID) {
		this.ID = ID;
	}
	
	public void populateNBT(CompoundTag compound, Map<String, Object> data) {
	}

	protected abstract ItemBuilder getItemStackBuilder(Map<String, Object> data);
	
	public ItemStack getItemStack() {
		return getItemStack(new HashMap<>());
	}
	
	public ItemStack getItemStack(Map<String, Object> data) {
		ItemStack it = getItemStackBuilder(data).build();
        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(it);
		CompoundTag compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new CompoundTag();
        compound.putInt(NBT_CUSTOM_ITEM_ID, ID);
		populateNBT(compound, data);
		nmsStack.setTag(compound);
		return CraftItemStack.asBukkitCopy(nmsStack);
	}
	
	public abstract boolean preventOnLeftClick();
	
	public abstract boolean preventOnRightClick();
	
	public boolean preventOnBlockPlace(BlockPlaceEvent e) {
		return false;
	}
	
	public Runnable getLeftClick(PlayerInteractEvent e) {
		return null;
	}
	
	public Runnable getRightClick(PlayerInteractEvent e) {
		return null;
	}
	
	public Runnable onSwitchSlot(PlayerItemHeldEvent e) {
		return null;
	}
	
	public Runnable onBlockPlace(BlockPlaceEvent e) {
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CustomItem)) {
			return false;
		}
		
		CustomItem customIt = (CustomItem) obj;
		return customIt.ID == this.ID;
	}
	
	@Override
	public int hashCode() {
		return ID;
	}
	
	public static int getCustomItemID(ItemStack it) {
        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(it);
		if (!nmsStack.hasTag()) return -1;
		
		CompoundTag compound = nmsStack.getTag();
		return compound.getInt(NBT_CUSTOM_ITEM_ID);
	}
	
	public static CompoundTag getNBTCompound(ItemStack it) {
        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(it);
		if (!nmsStack.hasTag()) return null;
		
		return nmsStack.getTag();
	}
}
