package de.janmm14.epicpvp.warz.compass;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class CompassTargetModule extends Module<CompassTargetModule> implements Runnable {

	private final Map<UUID, CompassTarget> selectedTargets = new HashMap<>();

	public CompassTargetModule(WarZ plugin) {
		super( plugin );
		plugin.getServer().getScheduler().runTaskTimerAsynchronously( plugin, this, 20, 10 );
	}

	@Override
	public void reloadConfig() {
	}

	@Override
	public void run() {
		for ( Player plr : getPlugin().getServer().getOnlinePlayers() ) {
			CompassTarget target = selectedTargets.get( plr.getUniqueId() );
			if ( target != null ) {
				plr.setCompassTarget( target.getTarget( this, plr ) );
			}
		}
	}
}
