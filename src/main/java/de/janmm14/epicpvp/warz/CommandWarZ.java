package de.janmm14.epicpvp.warz;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandWarZ implements TabExecutor {

	private final WarZ plugin;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if ( args.length == 0 ) {
			sender.sendMessage( "§aWarZ Plugin by Janmm14" );
			sender.sendMessage( "§c/warz reload §6Liest die Config neu ein." );
			return true;
		}
		switch ( args[ 0 ].toLowerCase() ) {
			case "reload":
				plugin.reloadCfg();
				sender.sendMessage( "§aWarZ config reloaded." );
				if ( !( sender instanceof ConsoleCommandSender ) ) {
					plugin.getServer().getConsoleSender().sendMessage( "§aWarZ config reloaded by" + sender.getName() );
				}
				break;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if ( args[ 0 ].isEmpty() || "reload".startsWith( args[ 0 ].toLowerCase() ) ) {
			return Arrays.asList( "reload" );
		}
		return null;
	}
}
