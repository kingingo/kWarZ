package de.janmm14.epicpvp.warz.zonechest;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.BlockVector;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChestOpenListener implements Listener {

	private final ZoneAndChestsModule module;

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if ( event.hasBlock() && ( event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType() == Material.TRAPPED_CHEST ) ) {
			event.setCancelled( true );

			Player plr = event.getPlayer();

			BlockVector blockVector = plr.getLocation().toVector().toBlockVector();
			CustomChestInventoryHolder owner = new CustomChestInventoryHolder( blockVector );
			Inventory inv = Bukkit.createInventory( owner, InventoryType.CHEST );
			module.getChestContentManager().getInventory( plr.getWorld(), blockVector, inv );
			owner.setInventory( inv );
		}
	}
}
