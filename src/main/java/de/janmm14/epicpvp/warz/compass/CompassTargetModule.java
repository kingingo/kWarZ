package de.janmm14.epicpvp.warz.compass;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class CompassTargetModule extends Module<CompassTargetModule> implements Listener {

	private final Map<UUID, CompassTarget> selectedTargets = new HashMap<>();
	private final Map<UUID, UUID> trackedPlayers = new HashMap<>();

	public CompassTargetModule(WarZ plugin) {
		super( plugin, (module) -> module );
	}

	@Override
	public void reloadConfig() {
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player plr = event.getPlayer();
		CompassTarget target = selectedTargets.get( plr.getUniqueId() );
		if ( target != null ) {

		}
	}
}
