package de.janmm14.epicpvp.warz.cobwebs;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class CobWebModule extends Module<CobWebModule> implements Listener {

	public CobWebModule(WarZ plugin) {
		super( plugin, (module) -> module );
	}

	@Override
	public void reloadConfig() {
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if ( event.hasBlock() && event.hasItem() ) {
			ItemStack item = event.getItem();
			Block clickedBlock = event.getClickedBlock();
			if ( item.getType() == Material.SHEARS && clickedBlock.getType() == Material.WEB ) {
				clickedBlock.setType( Material.AIR, false );
				if ( item.getDurability() > 1 ) {
					item.setDurability( ( short ) ( item.getDurability() - 1 ) );
					event.getPlayer().setItemInHand( item );
				} else {
					event.getPlayer().setItemInHand( null );
				}
				event.getPlayer().updateInventory();
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if ( event.getBlock().getType() == Material.WEB ) {
			event.setCancelled( true );
		}
	}
}
