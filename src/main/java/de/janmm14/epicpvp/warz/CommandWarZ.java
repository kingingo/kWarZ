package de.janmm14.epicpvp.warz;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;

import de.janmm14.epicpvp.warz.zonechest.ZoneAndChestsModule;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandWarZ implements TabExecutor {

	private final WarZ plugin;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if ( args.length == 0 ) {
			sender.sendMessage( "§aWarZ Plugin by Janmm14" );
			sender.sendMessage( "§c/warz reload §7- §6Liest die Config neu ein." );
			sender.sendMessage( "§c/warz refill §7- §6Füllt die Kisten neu." );
			sender.sendMessage( "§c/warz debug §7- §6Wechselt den Debug-Modus." );
			return true;
		}
		switch ( args[ 0 ].toLowerCase() ) {
			case "reload":
				plugin.reloadCfg();
				sender.sendMessage( "§aWarZ config reloaded." );
				if ( !( sender instanceof ConsoleCommandSender ) ) {
					plugin.getServer().getConsoleSender().sendMessage( "§6WarZ config reloaded by " + sender.getName() );
				}
				break;
			case "refill":
				plugin.getModuleManager().getModule( ZoneAndChestsModule.class ).getChestContentManager().reset();
				sender.sendMessage( "§aDie Kisten wurden neu gefüllt." );
				break;
			case "debug":
				WarZ.DEBUG = !WarZ.DEBUG;
				if (WarZ.DEBUG) {
					sender.sendMessage( "§6Der Debug-Modus ist nun§a angeschaltet§6." );
				} else {
					sender.sendMessage( "§6Der Debug-Modus ist nun§a ausgeschaltet§6." );
				}
				break;
			default:
				sender.sendMessage( "§cUnbekannte Aktion §6" + args[ 0 ] );
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if ( args.length == 1 && ( args[ 0 ].isEmpty() || "reload".startsWith( args[ 0 ].toLowerCase() ) ) ) {
			return Collections.singletonList( "reload" );
		}
		return null;
	}
}
