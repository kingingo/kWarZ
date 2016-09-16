package de.janmm14.epicpvp.warz.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;

import com.google.common.collect.ImmutableList;
import dev.wolveringer.client.debug.Debugger;
import eu.epicpvp.kcore.Util.UtilNumber;
import de.janmm14.epicpvp.warz.WarZ;
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
			case "setslots":
				WarZ.SLOTS = UtilNumber.toInt(args[ 1 ]);
				plugin.getConfig().set( "slots", WarZ.SLOTS );
				plugin.saveConfig();
				sender.sendMessage( "§6Die Slots wurden auf §e"+WarZ.SLOTS+"§6 gesetzt!" );
				break;
			case "setslotspremium":
				WarZ.SLOTS_PREMIUM = UtilNumber.toInt(args[ 1 ]);
				plugin.getConfig().set( "slots_premium", WarZ.SLOTS_PREMIUM );
				plugin.saveConfig();
				sender.sendMessage( "§6Die §ePremium-Slots§6 wurden auf §e"+WarZ.SLOTS_PREMIUM+"§6 gesetzt!" );
				break;
			case "refill":
				plugin.getModuleManager().getModule( ZoneAndChestsModule.class ).getChestContentManager().reset();
				sender.sendMessage( "§aDie Kisten wurden neu gefüllt." );
				break;
			case "debug":
				boolean newState = !WarZ.DEBUG;
				WarZ.DEBUG = newState;
				plugin.getConfig().set( "debug", WarZ.DEBUG );
				plugin.saveConfig();
				if ( newState ) {
					sender.sendMessage( "§6Der Debug-Modus ist nun§a angeschaltet§6." );
				} else {
					sender.sendMessage( "§6Der Debug-Modus ist nun§a ausgeschaltet§6." );
				}
				break;
			case "debug2":
				boolean newState2 = !Debugger.isEnabled();
				Debugger.setEnabled( newState2 );
				if ( newState2 ) {
					sender.sendMessage( "§6Der DatenClient-Debug-Modus ist nun§a angeschaltet§6." );
				} else {
					sender.sendMessage( "§6Der DatenClient-Debug-Modus ist nun§a ausgeschaltet§6." );
				}
				break;
			default:
				sender.sendMessage( "§cUnbekannte Aktion §6" + args[ 0 ] );
		}
		return true;
	}

	private ImmutableList<String> options = ImmutableList.of( "reload", "refill", "debug", "debug2" );

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if ( args.length != 1 ) {
			return Collections.emptyList();
		}
		String arg0 = args[ 0 ].toLowerCase();
		if ( arg0.isEmpty() ) {
			return Arrays.asList( "reload", "refill", "debug", "debug2" );
		}
		return options.stream()
			.filter( option -> option.toLowerCase().startsWith( arg0 ) )
			.collect( Collectors.toList() );
	}
}
