package de.janmm14.epicpvp.warz.spawn;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class CommandSpawn implements CommandExecutor {

	private final SpawnModule module;

	public CommandSpawn(SpawnModule module) {
		this.module = module;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if ( !( sender instanceof Player ) ) {
			sender.sendMessage( "§cDieser Befehl ist nur für Spieler!" );
			return true;
		}
		Player plr = ( Player ) sender;
		if ( args.length == 0 || !sender.isOp() ) {
			Collection<Entity> nearbyEntities = plr.getWorld().getNearbyEntities( plr.getLocation(), 25, 25, 25 );
			for ( Entity e : nearbyEntities ) {
				if ( e instanceof Player ) {
					sender.sendMessage( "§cIn deiner Nähe sind Gegner, daher kannst du nicht zum Spawn." );
					return true;
				}
			}
			module.saveLastMapPos( plr, plr.getLocation() );
			plr.teleport( module.getSpawn() );
			sender.sendMessage( "§aDu wurdest zu Spawn teleportiert." );
		} else {
			if ( args[ 0 ].equalsIgnoreCase( "setsave" ) ) {
				sender.sendMessage( "§aSicherer Spawnpunkt gesetzt." );
				module.setSpawn( plr.getLocation() );
			}
			if ( args[ 0 ].equalsIgnoreCase( "setmap" ) ) {
				sender.sendMessage( "§aStartmappunkt gesetzt." );
				Location loc = plr.getLocation();
				Bukkit.getWorlds().get( 0 ).setSpawnLocation( loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() );
			}
		}
		return true;
	}
}
