package de.janmm14.epicpvp.warz.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import dev.wolveringer.client.debug.Debugger;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Util.UtilNumber;

import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.WarZListener;
import de.janmm14.epicpvp.warz.zonechest.ZoneAndChestsModule;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandWarZ implements CommandExecutor {

	private final WarZ plugin;

	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "wz", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if ( args.length == 0 ) {
			sender.sendMessage( "§aWarZ Plugin by Janmm14" );
			sender.sendMessage( "§c/wz reload §7- §6Liest die Config neu ein." );
			sender.sendMessage( "§c/wz refill §7- §6Füllt die Kisten neu." );
			sender.sendMessage( "§c/wz debug §7- §6Wechselt den Debug-Modus." );
			sender.sendMessage( "§c/wz day §7- §6Setzt auf Tag." );
			sender.sendMessage( "§c/wz night §7- §6Setzt auf Nacht." );
			sender.sendMessage( "§c/wz settime [Time]§7 - §6Setzt die Zeit." );
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
			case "settime":
				WarZListener.TIME = UtilNumber.toInt( args[ 1 ] );
				break;
			case "day":
				WarZListener.TIME = 6000;
				break;
			case "night":
				WarZListener.TIME = 18000;
				break;
			case "setslots":
				WarZ.SLOTS = UtilNumber.toInt( args[ 1 ] );
				plugin.getConfig().set( "slots", WarZ.SLOTS );
				plugin.saveConfig();
				sender.sendMessage( "§6Die Slots wurden auf §e" + WarZ.SLOTS + "§6 gesetzt!" );
				break;
			case "setslotspremium":
				WarZ.SLOTS_PREMIUM = UtilNumber.toInt( args[ 1 ] );
				plugin.getConfig().set( "slots_premium", WarZ.SLOTS_PREMIUM );
				plugin.saveConfig();
				sender.sendMessage( "§6Die §ePremium-Slots§6 wurden auf §e" + WarZ.SLOTS_PREMIUM + "§6 gesetzt!" );
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

//	private ImmutableList<String> options = ImmutableList.of( "reload", "refill", "debug", "debug2" );
//
//	@Override
//	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
//		if ( args.length != 1 ) {
//			return Collections.emptyList();
//		}
//		String arg0 = args[ 0 ].toLowerCase();
//		if ( arg0.isEmpty() ) {
//			return Arrays.asList( "reload", "refill", "debug", "debug2" );
//		}
//		return options.stream()
//			.filter( option -> option.toLowerCase().startsWith( arg0 ) )
//			.collect( Collectors.toList() );
//	}
}
