package de.janmm14.epicpvp.warz.gilde;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.BlockVector;

import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Permission.Events.PlayerLoadPermissionEvent;
import eu.epicpvp.kcore.Util.UtilPlayer;

public class GildeChestListener extends kListener {

	private GildeModule module;

	public GildeChestListener(GildeModule module) {
		super(module.getPlugin(),"GildeChestListener");
		this.module = module;
	}

	@EventHandler
	public void join(PlayerLoadPermissionEvent ev) {
		module.loadInventory( ev.getPermissionPlayer().getPlayerId() );
	}

	@EventHandler
	public void quit(PlayerQuitEvent ev) {
		module.saveInventory( UtilPlayer.getPlayerId( ev.getPlayer() ) );
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInventoryOpen(PlayerInteractEvent event) {
		Player plr = event.getPlayer();
		if ( plr.isOp() && event.getAction() == Action.LEFT_CLICK_BLOCK ) {
			return;
		}
		BlockVector chestLoc = module.getChest();
		if ( chestLoc == null ) {
			return;
		}

		if ( !module.getHandler().hasGilde( plr ) ) {
			return;
		}

		Block block = event.getClickedBlock();
		if ( event.hasBlock() && ( block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST ) ) {
			if ( chestLoc.equals( block.getLocation().toVector().toBlockVector() ) ) {
				event.setCancelled( true );
				module.openInventory( plr );
			}
		}
	}
}
