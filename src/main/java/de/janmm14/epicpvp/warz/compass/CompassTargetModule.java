package de.janmm14.epicpvp.warz.compass;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import eu.epicpvp.kcore.Util.UtilDirection;

public class CompassTargetModule extends Module<CompassTargetModule> implements Runnable {

	private final Map<UUID, CompassTarget> selectedTargets = new HashMap<>();
	private final Map<UUID, UtilDirection> lastDirection = new HashMap<>();

	public CompassTargetModule(WarZ plugin) {
		super( plugin, CompassTargetSwitchListener::new );
		plugin.getServer().getScheduler().runTaskTimerAsynchronously( plugin, this, 20, 10 );
	}

	public CompassTarget getCompassTarget(Player plr) {
		CompassTarget compassTarget = selectedTargets.get( plr.getUniqueId() );
		if ( compassTarget == null ) {
			setCompassTarget( plr, CompassTarget.ENEMY );
			return CompassTarget.ENEMY;
		}
		return compassTarget;
	}
	
	public void onDisable() {
		
	}

	public void setCompassTarget(Player plr, CompassTarget target) {
		selectedTargets.put( plr.getUniqueId(), target );
	}

	@Override
	public void reloadConfig() {
	}

	public void remove(Player plr) {
		selectedTargets.remove( plr.getUniqueId() );
		lastDirection.remove( plr.getUniqueId() );
	}

	@Override
	public void run() {
		for ( Player plr : getPlugin().getServer().getOnlinePlayers() ) {
			CompassTarget target = getCompassTarget( plr );
			Location targetLoc = target.getTarget( this, plr );
			if ( targetLoc == null ) {
				UtilDirection direction = lastDirection.get( plr.getUniqueId() );
				if ( direction == null ) {
					direction = UtilDirection.NORTH;
				}
				direction = direction.nextDirection();
				lastDirection.put( plr.getUniqueId(), direction );
				targetLoc = direction.get( plr.getLocation() );
			}
			plr.setCompassTarget( targetLoc );
		}
	}
}
