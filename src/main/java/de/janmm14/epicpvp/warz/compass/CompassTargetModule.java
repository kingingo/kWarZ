package de.janmm14.epicpvp.warz.compass;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import eu.epicpvp.kcore.Util.UtilDirection;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

import lombok.Getter;

public class CompassTargetModule extends Module<CompassTargetModule> implements Runnable {

	private final Map<UUID, CompassTarget> selectedTargets = new HashMap<>();
	private final Map<UUID, UtilDirection> lastDirection = new HashMap<>();
	@Getter
	private int enemyRadius;

	public CompassTargetModule(WarZ plugin) {
		super( plugin, CompassTargetSwitchListener::new );
		plugin.getServer().getScheduler().runTaskTimerAsynchronously( plugin, this, 20, 10 );
	}

	public CompassTarget getCompassTarget(Player plr) {
		CompassTarget compassTarget = selectedTargets.get( plr.getUniqueId() );
		if ( compassTarget == null ) {
			compassTarget = CompassTarget.values()[getPlugin().getUserDataConfig().getConfig( plr ).getInt( "compassTarget" )];
			if ( compassTarget == null ) {
				setCompassTarget( plr, CompassTarget.ENEMY );
				return CompassTarget.ENEMY;
			}
		}
		return compassTarget;
	}

	@Override
	public void onDisable() {
	}

	public void setCompassTarget(Player plr, CompassTarget target) {
		selectedTargets.put( plr.getUniqueId(), target );
		getPlugin().getUserDataConfig().getConfig( plr ).set( "compassTarget", target.ordinal() );
	}

	@Override
	public void reloadConfig() {
		getConfig().addDefault( "compassTarget.maxEnemyRadius", 200 );
		enemyRadius = getConfig().getInt( "compassTarget.maxEnemyRadius" );
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
