package de.janmm14.epicpvp.warz.compass;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.kcore.Inventory.Inventory.InventoryCopy;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import lombok.RequiredArgsConstructor;

public class CompassTargetSwitchListener implements Listener {

	private final CompassTargetModule module;
	private InventoryCopy selectionInventory;
	
	public CompassTargetSwitchListener(CompassTargetModule module){
		this.module=module;
		
		this.selectionInventory=new InventoryCopy(InventorySize._9, "Set your Compass target");
		this.selectionInventory.addButton(1, new CompassButton(module, CompassTarget.ENEMY, 1, UtilItem.Item(new ItemStack(Material.SKULL_ITEM,1,(byte)SkullType.ZOMBIE.ordinal()), new String[]{}, "§cEnemies"), this.selectionInventory));
		this.selectionInventory.addButton(4, new CompassButton(module, CompassTarget.FRIEND, 4, UtilItem.Item(new ItemStack(Material.SKULL_ITEM,1,(byte)SkullType.PLAYER.ordinal()), new String[]{}, "§aFriends"), this.selectionInventory));
		this.selectionInventory.addButton(7, new CompassButton(module, CompassTarget.ZONE, 7, UtilItem.Item(new ItemStack(Material.FENCE), new String[]{}, "§6Zones"), this.selectionInventory));
		this.selectionInventory.fill(Material.STAINED_GLASS_PANE, 7);
		this.selectionInventory.setCreate_new_inv(true);
		UtilInv.getBase().addPage(this.selectionInventory);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.hasItem() && event.getItem().getType() == Material.COMPASS) {
			openSelectionInventory(event.getPlayer());
		}
	}

	private void openSelectionInventory(Player plr) {
		this.selectionInventory.open(plr, UtilInv.getBase());
	}
}
