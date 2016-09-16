package de.janmm14.epicpvp.warz.thirst;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import dev.wolveringer.dataserver.gamestats.GameType;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsCreateEvent;
import eu.epicpvp.kcore.Util.UtilPlayer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ThirstListener implements Listener {

	private final ThirstModule module;

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if ( event.hasItem() && ( event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR ) ) {
			ItemStack item = event.getItem();
			if ( item.getType() == Material.AIR ) {
				return;
			}
			Double thirstFillAmount = module.getThirstFillAmount( item );

			if ( thirstFillAmount == null ) {
				return;
			}

			Player plr = event.getPlayer();
			if ( plr.getExp() >= 1 ) {
				return;
			}

			float newThirstPercentage = ( float ) ( plr.getExp() + thirstFillAmount );
			plr.setExp( newThirstPercentage >= 1 ? 1 : newThirstPercentage );
			checkExpAbilities( plr );

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

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		Player plr = event.getPlayer();
		if ( plr.getExp() <= 0.0 ) {
			return;
		}
		double distance = event.getFrom().toVector().distance( event.getTo().toVector() );
		float newExp = ( float ) ( plr.getExp() - module.getDistanceMultiplier() * distance );
		plr.setExp( newExp <= 0.0 ? 0 : newExp );
		checkExpAbilities( plr );
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onExperienceChange(PlayerExpChangeEvent event) {
		event.setAmount( 0 );
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerNew(PlayerStatsCreateEvent event) {
		if ( event.getManager().getType() == GameType.WARZ ) {
			Player plr = UtilPlayer.searchExact( event.getPlayerId() );
			if ( plr != null ) {
				plr.setExp( 1 );
				checkExpAbilities( plr );
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player plr = event.getPlayer();
		checkExpAbilities( plr );
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled( true );
	}

	private void checkExpAbilities(Player plr) {
		float exp = plr.getExp();
		if ( module.getLowThirstThreshold() >= exp ) {
			plr.setFoodLevel( 6 );
			plr.setSaturation( Float.MAX_VALUE );
		} else {
			plr.setFoodLevel( 20 );
			plr.setSaturation( Float.MAX_VALUE );
		}
	}
}
