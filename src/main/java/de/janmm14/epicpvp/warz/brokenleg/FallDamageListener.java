package de.janmm14.epicpvp.warz.brokenleg;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FallDamageListener implements Listener {

	private final BrokenLegModule module;

	@EventHandler(ignoreCancelled = true)
	public void onFallDamage(EntityDamageEvent event) {
		if ( event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getEntityType() == EntityType.PLAYER
			&& event.getFinalDamage() > module.getRequiredHalfHeartsDamage() ) {
			( ( Player ) event.getEntity() ).addPotionEffect( PotionEffectType.SLOW.createEffect( module.getDurationTicks(), module.getSlownessAmplifier() - 1 ) );
		}
	}
}
