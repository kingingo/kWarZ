package de.janmm14.epicpvp.warz.fishingrod;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import eu.epicpvp.kcore.Util.UtilWorldGuard;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FishingRodListener implements Listener {

	private static final int USAGE_DELAY_SECONDS = 3;
	private final FishingRodModule module;
	private final Cache<UUID, Object> noFallDamage = CacheBuilder.newBuilder()
		.expireAfterWrite( 10, TimeUnit.SECONDS ) // if he did not got fall damage by the velocity, remove him after some time
		.initialCapacity( 32 )
		.concurrencyLevel( 1 ) // events are sync
		.build();
	private final Cache<UUID, Long> delay = CacheBuilder.newBuilder()
		.expireAfterWrite( USAGE_DELAY_SECONDS, TimeUnit.SECONDS )
		.initialCapacity( 32 )
		.concurrencyLevel( 1 ) // events are sync
		.build();
	private static final Object DUMMY = new Object();

	@EventHandler
	public void onFishingRod(PlayerFishEvent event) {
		if ( event.getState() == PlayerFishEvent.State.FISHING ) {
			UUID uuid = event.getPlayer().getUniqueId();
			Long lastUsage = delay.getIfPresent( uuid );
			if ( lastUsage != null && lastUsage >= System.currentTimeMillis() - USAGE_DELAY_SECONDS * 1000 ) {
				event.setCancelled( true );
				long sec = USAGE_DELAY_SECONDS - Math.max( 1, ( System.currentTimeMillis() - lastUsage ) / 1000 );
				if ( sec == 1 ) {
					event.getPlayer().sendMessage( "§cDu musst noch §6eine §cSekunde warten." );
				} else {
					event.getPlayer().sendMessage( "§cDu musst noch §6" + sec + "§c Sekunden warten." );
				}
				return;
			}
		} else if ( ( event.getState() == PlayerFishEvent.State.IN_GROUND || event.getState() == PlayerFishEvent.State.FAILED_ATTEMPT )
			&& UtilWorldGuard.RegionFlag( event.getPlayer(), DefaultFlag.PVP ) ) {

			UUID uuid = event.getPlayer().getUniqueId();
			Long lastUsage = delay.getIfPresent( uuid );
			if ( lastUsage == null || lastUsage < System.currentTimeMillis() - USAGE_DELAY_SECONDS * 1000 ) {
				delay.put( uuid, System.currentTimeMillis() );
			} else {
				event.setCancelled( true );
				long sec = USAGE_DELAY_SECONDS - Math.max( 1, ( System.currentTimeMillis() - lastUsage ) / 1000 );
				if ( sec == 1 ) {
					event.getPlayer().sendMessage( "§cDu musst noch §6eine §cSekunde warten." );
				} else {
					event.getPlayer().sendMessage( "§cDu musst noch §6" + sec + "§c Sekunden warten." );
				}
				return;
			}

			Location location = event.getHook().getLocation();
			boolean cancel = true;
			for ( BlockFace face : BlockFace.values() ) {
				if ( location.getBlock().getRelative( face ).getType() != Material.AIR ) {
					cancel = false;
					break;
				}
			}
			if ( cancel ) {
				return;
			}

			Vector diffVector = location.toVector().subtract( event.getPlayer().getLocation().toVector() );
			if ( diffVector.lengthSquared() < 3 * 3 ) {
				return;
			}
			noFallDamage.put( event.getPlayer().getUniqueId(), DUMMY );
			double y = diffVector.getY();
			if ( y < .5 ) {
				y = .5;
			}
			if ( y > 2 ) {
				y = 2;
			}
			Vector normalize = diffVector.setY( 0 ).normalize();
			event.getPlayer().setVelocity( normalize.setY( y / 5 ).multiply( 3 ) );
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
