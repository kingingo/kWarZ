package de.janmm14.epicpvp.warz.zombies;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ZombieSpawnOnDeathListener implements Listener {

	private final ZombieModule module;

	@EventHandler(ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		Player plr = event.getEntity();
		Location loc = plr.getLocation();
		module.setupZombie( loc.getWorld().spawn( loc, Zombie.class ) );
	}
}
