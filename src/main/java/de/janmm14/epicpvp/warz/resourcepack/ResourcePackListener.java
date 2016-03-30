package de.janmm14.epicpvp.warz.resourcepack;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResourcePackListener implements Listener {

	private final ResourcePackModule module;

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent event) {
		int resourcePackDelay = module.getResourcePackDelay();
		if ( resourcePackDelay == 0 ) {
			module.sendResourcePack( event.getPlayer() );
		} else {
			module.getPlugin().getServer().getScheduler().runTaskLater( module.getPlugin(), () -> module.sendResourcePack( event.getPlayer() ), resourcePackDelay );
		}
	}
}
