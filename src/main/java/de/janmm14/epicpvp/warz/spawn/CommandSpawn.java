package de.janmm14.epicpvp.warz.spawn;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilWorldGuard;

import de.janmm14.epicpvp.warz.friends.FriendInfo;
import de.janmm14.epicpvp.warz.friends.FriendInfoManager;
import de.janmm14.epicpvp.warz.friends.FriendModule;
import de.janmm14.epicpvp.warz.friends.PlayerFriendRelation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandSpawn implements CommandExecutor {

	private final SpawnModule module;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if ( !( sender instanceof Player ) ) {
			sender.sendMessage( "§cDieser Befehl ist nur für Spieler!" );
			return true;
		}
		Player plr = ( Player ) sender;
		if ( args.length == 0 || !sender.isOp() ) {
			if ( UtilWorldGuard.RegionFlag( plr, DefaultFlag.PVP ) ) {
				if ( !plr.isOp() ) {
					FriendInfoManager info = module.getModuleManager().getModule( FriendModule.class ).getFriendInfoManager();
					FriendInfo plrF = info.get( UtilPlayer.getPlayerId( plr ) );

					Collection<Entity> nearbyEntities = plr.getWorld().getNearbyEntities( plr.getLocation(), 25, 25, 25 );
					for ( Entity e : nearbyEntities ) {
						if ( e instanceof Player && e.getUniqueId() != plr.getUniqueId() ) {
							if ( !PlayerFriendRelation.areFriends( info, plrF, UtilPlayer.getPlayerId( ( ( Player ) e ) ) ) ) {
								sender.sendMessage( TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_SPAWN_NEAR_TO_PLAYER" ) );
								return true;
							}
						}
					}
				}
				module.saveLastMapPos( plr, plr.getLocation() );
				plr.teleport( module.getSpawn() );
				sender.sendMessage( TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_SPAWN_TELEPORT" ) );
			}
		} else {
			if ( args[ 0 ].equalsIgnoreCase( "setspawn" ) ) {
				sender.sendMessage( TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_SPAWN_SAVE" ) );
				module.setSpawn( plr.getLocation() );
			} else if ( args[ 0 ].equalsIgnoreCase( "removemapspawn" ) ) {
				if ( module.removeNearestMapSpawn( plr, 2 ) ) {
					sender.sendMessage( "§aDer Map Spawn Punkt wurde entfernt!" );
				} else {
					sender.sendMessage( "§cEs wurde kein Map Spawn Punkt in deiner Nähe gefunden (min Nähe 2 Blöcke)" );
				}
			} else if ( args[ 0 ].equalsIgnoreCase( "setmapspawn" ) ) {
				module.addMapSpawn( plr.getLocation() );
				sender.sendMessage( "§aEs wurde ein Map spawn punkt gesetzt!" );
			} else if ( args[ 0 ].equalsIgnoreCase( "setmap" ) ) {
				sender.sendMessage( TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_SPAWN_SET" ) );
				Location loc = plr.getLocation();
				Bukkit.getWorlds().get( 0 ).setSpawnLocation( loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() );
			}
		}
		return true;
	}
}
