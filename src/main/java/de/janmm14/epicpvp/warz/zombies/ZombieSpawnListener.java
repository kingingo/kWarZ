package de.janmm14.epicpvp.warz.zombies;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ZombieSpawnListener implements Listener {

	private final ZombieModule module;

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntitySpawn(CreatureSpawnEvent event) {
		if ( event.getEntityType() == EntityType.ZOMBIE ) {
			if ( event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM ) {
				module.setupZombie( ( Zombie ) event.getEntity() );
			}
		} else {
			//TODO no other mobs?
			event.setCancelled( true );
		}
	}
}
