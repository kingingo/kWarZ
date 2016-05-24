package de.janmm14.epicpvp.warz.healitems;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HealUseListener implements Listener {

	private final HealItemsModule module;
	private final TObjectLongMap<UUID> nextHealUses = new TObjectLongHashMap<>( 100, .7F );

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if ( event.hasItem() && ( event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR ) ) {
			ItemStack item = event.getItem();
			if ( item.getType() == Material.AIR ) {
				return;
			}
			HealItemValues healItemValues = module.getItemConfig( item );
			if ( healItemValues == null ) {
				return;
			}
			Player plr = event.getPlayer();
			double newHealth = plr.getHealth() + healItemValues.getHealAmount();
			if ( newHealth < plr.getMaxHealth() ) {
				plr.setHealth( newHealth );
			} else if ( plr.getHealth() < plr.getMaxHealth() ) {
				plr.setHealth( plr.getMaxHealth() );
			} else { //player is already at full health
				return;
			}
			long currentMillis = System.currentTimeMillis();
			Long nextHealUse = nextHealUses.get( plr.getUniqueId() ); //default value is 0
			if ( nextHealUse > currentMillis ) { //cooldown not over
				//TODO message?
				return;
			}
			nextHealUses.put( plr.getUniqueId(), currentMillis + healItemValues.getMsDelay() );
			ItemStack handItem = plr.getItemInHand();
			if ( handItem.getAmount() > 1 ) {
				handItem.setAmount( handItem.getAmount() - 1 );
				plr.setItemInHand( handItem );
			} else {
				plr.setItemInHand( null );
			}
			plr.updateInventory();
			event.setCancelled( true );
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		nextHealUses.remove( event.getPlayer().getUniqueId() );
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onKick(PlayerKickEvent event) {
		nextHealUses.remove( event.getPlayer().getUniqueId() );
	}
}
