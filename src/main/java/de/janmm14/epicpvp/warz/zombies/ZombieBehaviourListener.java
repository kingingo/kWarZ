package de.janmm14.epicpvp.warz.zombies;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ZombieBehaviourListener implements Listener {

	private final ZombieModule module;

	@EventHandler(ignoreCancelled = true)
	public void onCombust(EntityCombustEvent event) { //stops zombies being on fire by sun
		if ( !( event instanceof EntityCombustByEntityEvent ) && !( event instanceof EntityCombustByBlockEvent ) //allow zombies being fired by entities and blocks
			&& event.getEntityType() == EntityType.ZOMBIE ) {
			event.setDuration( 0 );
		}
	}

	@EventHandler
	public void onDrop(EntityDeathEvent ev) {
		if ( ev.getEntityType() == EntityType.ZOMBIE || ev.getEntityType() == EntityType.SKELETON ) {
			ev.setDroppedExp( 0 );
			ev.getDrops().clear();
		}
	}
	//TODO implement more zombie behaviour
}
