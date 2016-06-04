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
		super( plugin, CompassTargetSwitchListener::new );
		plugin.getServer().getScheduler().runTaskTimerAsynchronously( plugin, this, 20, 10 );
	}

	public CompassTarget getCompassTarget(Player plr) {
		CompassTarget compassTarget = selectedTargets.get( plr.getUniqueId() );
		if (compassTarget == null) {
			setCompassTarget( plr, CompassTarget.ENEMY );
			return CompassTarget.ENEMY;
		}
		return compassTarget;
	}

	public void setCompassTarget(Player plr, CompassTarget target) {
		selectedTargets.put( plr.getUniqueId(), target );
	}

	@Override
	public void reloadConfig() {
	}

	@Override
	public void run() {
		for ( Player plr : getPlugin().getServer().getOnlinePlayers() ) {
			CompassTarget target = getCompassTarget( plr );
			plr.setCompassTarget( target.getTarget( this, plr ) );
		}
	}
}
