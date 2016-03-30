package de.janmm14.epicpvp.warz.resourcepack;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandResourcePack implements CommandExecutor {

	private final ResourcePackModule module;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if ( !( sender instanceof Player ) ) {
			sender.sendMessage( "§cDieser Befehl ist nur für Spieler!" );
			return true;
		}
		module.sendResourcePack( ( Player ) sender );
		return true;
	}
}
