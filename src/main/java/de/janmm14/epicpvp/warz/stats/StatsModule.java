package de.janmm14.epicpvp.warz.stats;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import eu.epicpvp.kcore.StatsManager.StatsManager;
import eu.epicpvp.kcore.StatsManager.StatsManagerRepository;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class StatsModule extends Module<StatsModule> implements Listener { //TODO /stats <name>

	private final StatsManager manager = StatsManagerRepository.getStatsManager( GameType.WARZ );

	public StatsModule(WarZ plugin) {
		super( plugin, module -> module );
	}

	@Override
	public void reloadConfig() {

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent event) {

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent event) {

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event) {
		Player victim = event.getEntity();
		manager.getAsync( victim, StatsKey.DEATHS, (o, throwable) -> {
			if ( throwable != null ) {
				throwable.printStackTrace();
				return;
			}
			int deaths = ( Integer ) o;
			manager.set( victim, StatsKey.DEATHS, deaths + 1);
		} );
		Player killer = event.getEntity().getKiller();
		if (killer == null) {
			return;
		}
		manager.getAsync( killer, StatsKey.KILLS, (o, throwable) -> {
			if ( throwable != null ) {
				throwable.printStackTrace();
				return;
			}
			int kills = ( Integer ) o;
			manager.set( killer, StatsKey.KILLS, kills + 1 );
		} );
	}
}
