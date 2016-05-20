package de.janmm14.epicpvp.warz.fishingrod;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FishingRodListener implements Listener {

	private final FishingRodModule module;
	private final Cache<UUID, Object> noFallDamage = CacheBuilder.newBuilder()
		.expireAfterWrite( 30, TimeUnit.SECONDS )  //if he did not got fall damage by the velocity, remove him after some time
		.initialCapacity( 32 )
		.concurrencyLevel( 1 ) //events are sync
		.build();
	private static final Object DUMMY = new Object();

	@EventHandler
	public void onFishingRod(PlayerFishEvent event) {
		if ( event.getState() == PlayerFishEvent.State.IN_GROUND ) {
			noFallDamage.put( event.getPlayer().getUniqueId(), DUMMY );

			Vector diffVector = event.getHook().getLocation().add( 0, 1.8, 0 ).toVector().subtract( event.getPlayer().getLocation().toVector() );
			event.getPlayer().setVelocity( diffVector.normalize().multiply( 2 ) );
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if ( event.getEntityType() == EntityType.PLAYER && event.getCause() == EntityDamageEvent.DamageCause.FALL ) {
			UUID uuid = event.getEntity().getUniqueId();
			if ( noFallDamage.getIfPresent( uuid ) != null ) {
				event.setCancelled( true );
				noFallDamage.invalidate( uuid );
			}
		}
	}
}