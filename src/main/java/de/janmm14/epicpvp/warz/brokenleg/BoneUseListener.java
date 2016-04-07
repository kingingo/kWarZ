package de.janmm14.epicpvp.warz.brokenleg;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BoneUseListener implements Listener {

	private final BrokenLegModule module;

	@EventHandler
	public void onBoneUse(PlayerInteractEvent event) {
		if ( !event.hasItem() ) {
			return;
		}
		Action action = event.getAction();
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if ( ( action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK )
			&& item.getType() == Material.BONE && player.hasPotionEffect( PotionEffectType.SLOW ) ) {
			player.removePotionEffect( PotionEffectType.SLOW );
			if ( item.getAmount() == 1 ) {
				item.setAmount( 0 );
				player.setItemInHand( null );
			} else {
				item.setAmount( item.getAmount() - 1 );
				player.setItemInHand( item );
			}
			player.updateInventory();
		}
	}
}
