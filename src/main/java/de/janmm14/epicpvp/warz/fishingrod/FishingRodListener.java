package de.janmm14.epicpvp.warz.fishingrod;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FishingRodListener implements Listener {

	private final FishingRodModule module;
	private final Cache<UUID, Object> noFallDamage = CacheBuilder.newBuilder()
		.expireAfterWrite( 10, TimeUnit.SECONDS )  //if he did not got fall damage by the velocity, remove him after some time
		.initialCapacity( 32 )
		.concurrencyLevel( 1 ) //events are sync
		.build();
	private static final Object DUMMY = new Object();

	@EventHandler
	public void onFishingRod(PlayerFishEvent event) {
		if ( event.getState() == PlayerFishEvent.State.IN_GROUND || event.getState() == PlayerFishEvent.State.FAILED_ATTEMPT ) {
			Location location = event.getHook().getLocation();
			boolean cancel = true;
			for ( BlockFace face : BlockFace.values() ) {
				if ( location.getBlock().getRelative( face ).getType() != Material.AIR ) {
					cancel = false;
					break;
				}
			}
			if (cancel) {
				return;
			}

			Vector diffVector = location.toVector().subtract( event.getPlayer().getLocation().toVector() );
			if (diffVector.lengthSquared() < 3 * 3) {
				return;
			}
			noFallDamage.put( event.getPlayer().getUniqueId(), DUMMY );
			double y = diffVector.getY();
			if (y < .5) {
				y = .5;
			}
			if ( y > 2 ) {
				y = 2;
			}
			Vector normalize = diffVector.setY( 0 ).normalize();
			event.getPlayer().setVelocity( normalize.setY( y / 5 ).multiply( 5 ) );
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
