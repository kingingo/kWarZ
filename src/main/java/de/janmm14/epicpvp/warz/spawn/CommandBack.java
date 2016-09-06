package de.janmm14.epicpvp.warz.spawn;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.epicpvp.kcore.Translation.TranslationHandler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandBack implements CommandExecutor {

	private final SpawnModule module;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if ( !( sender instanceof Player ) ) {
			sender.sendMessage( "§cDieser Befehl ist nur für Spieler!" );
			return true;
		}
		Player plr = ( Player ) sender;
		if ( plr.getWorld().equals( module.getSpawn().getWorld() ) ) {
			sender.sendMessage( TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_BACK_WARZONE" ) );
			return true;
		}
		Location lastMapPos = module.getUserConfig( plr ).getLocation( "lastMapPos" );
		if ( lastMapPos == null ) {
			lastMapPos = module.getRandomMapSpawn();
		}
		plr.teleport( lastMapPos );
		return true;
	}
}
