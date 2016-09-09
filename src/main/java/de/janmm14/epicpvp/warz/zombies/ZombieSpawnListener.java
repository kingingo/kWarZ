package de.janmm14.epicpvp.warz.zombies;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import de.janmm14.epicpvp.warz.WarZ;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ZombieSpawnListener implements Listener {

	private final ZombieModule module;

	@EventHandler(ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		if ( WarZ.DEBUG ) {
			Bukkit.broadcastMessage( "PlayerDeathEvent - " + event.getEntity().getName() );
		}
		Player plr = event.getEntity();
		Location loc = plr.getLocation();
		module.setupZombie( loc.getWorld().spawn( loc, Zombie.class ) );
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntitySpawn(CreatureSpawnEvent event) {
		switch ( event.getEntityType() ) {
			case WITHER_SKULL:
				break;
			case ZOMBIE:
				if ( event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM ) {
					module.setupZombie( ( Zombie ) event.getEntity() );
				}
				break;
			case SHEEP:
				event.setCancelled( true );
				Location loc = event.getLocation();
				Zombie zombie = loc.getWorld().spawn( loc, Zombie.class );
				module.setupZombie( zombie );
				break;
			default:
				event.setCancelled( true );
				break;
		}
	}
}
