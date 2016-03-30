package de.janmm14.epicpvp.warz.cobwebs;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CobWebDigListener implements Listener {

	private final CobWebModule module;

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if ( event.getBlock().getType() == Material.WEB ) {
			event.setCancelled( true );
		}
	}
}
