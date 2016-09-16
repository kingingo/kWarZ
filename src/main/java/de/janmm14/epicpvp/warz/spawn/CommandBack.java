package de.janmm14.epicpvp.warz.spawn;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilWorldGuard;

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
		if ( UtilWorldGuard.RegionFlag( plr, DefaultFlag.PVP ) ) {
			sender.sendMessage( TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_BACK_WARZONE" ) );
			return true;
		}

		module.teleportWarz( plr );
		return true;
	}
}
