package de.janmm14.epicpvp.warz.compass;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompassTargetSwitchListener implements Listener {

	private final CompassTargetModule module;

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.hasItem() && event.getItem().getType() == Material.COMPASS) {
			openSelectionInventory(event.getPlayer());
		}
	}

	private void openSelectionInventory(Player plr) {
		CompassTarget target = module.getCompassTarget( plr );
		//TODO implement
	}
}
