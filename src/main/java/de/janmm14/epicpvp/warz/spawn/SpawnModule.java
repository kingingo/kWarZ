package de.janmm14.epicpvp.warz.spawn;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import eu.epicpvp.kcore.kConfig.kConfig;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

import lombok.Getter;

public class SpawnModule extends Module<SpawnModule> {

	@Getter
	private Location spawn;

	public SpawnModule(WarZ plugin) {
		super( plugin );
		plugin.getCommand( "spawn" ).setExecutor( new CommandSpawn( this ) );
	}

	@Override
	public void reloadConfig() {
		spawn = ( Location ) getConfig().get( "spawnLocation" );
	}

	public void setSpawn(Location spawn) {
		this.spawn = spawn;
		getConfig().set( "spawnLocation", spawn );
	}

	public void saveLastMapPos(Player plr, Location loc) {
		getUserConfig( plr ).setLocation( "lastMapPos", loc );
	}

	public kConfig getUserConfig(Player plr) {
		return getPlugin().getUserDataConfig().getConfig( plr );
	}
}
