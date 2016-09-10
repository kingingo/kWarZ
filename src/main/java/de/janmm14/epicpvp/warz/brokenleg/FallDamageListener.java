package de.janmm14.epicpvp.warz.brokenleg;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import eu.epicpvp.kcore.Translation.TranslationHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FallDamageListener implements Listener {

	private final BrokenLegModule module;

	@EventHandler(ignoreCancelled = true)
	public void onFallDamage(EntityDamageEvent event) {
		if ( event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getEntityType() == EntityType.PLAYER
			&& event.getDamage() > module.getRequiredHalfHeartsDamage() ) {
			Player plr = ( Player ) event.getEntity();
			int durationTicks = module.getDurationTicks();
			new PotionEffect( PotionEffectType.SLOW, durationTicks * 2, module.getSlownessAmplifier() )
				.apply( plr );

			String notificationMessage = module.getNotificationMessage();
			if ( !notificationMessage.trim().isEmpty() ) {
				plr.sendMessage( TranslationHandler.getText(plr,"PREFIX") + notificationMessage );
			}
		}
	}
}
