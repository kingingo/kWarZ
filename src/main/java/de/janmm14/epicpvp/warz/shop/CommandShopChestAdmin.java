package de.janmm14.epicpvp.warz.shop;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.janmm14.epicpvp.warz.hooks.UserDataConverter;
import eu.epicpvp.kcore.kConfig.kConfig;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandShopChestAdmin implements CommandExecutor {

	private final ShopModule module;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		module.getPlugin().getServer().getScheduler().runTaskAsynchronously( module.getPlugin(), () -> {
			if (!sender.isOp()) {
				return;
			}
			if (args.length == 0){
				sender.sendMessage( "§6/" + alias + " clearall §7- §eLöscht alle Shopkisteninhalte" );
				sender.sendMessage( "§6/" + alias + " showinv <name/uuid> §7- §eZeigt die Shopkisteninhalte des Spielers" );
				return;
			}
			switch ( args[0].toLowerCase() ) {
				case "clearall":
					File[] userdatas = new File( module.getPlugin().getDataFolder(), "userdata" ).listFiles();
					if (userdatas == null) {
						sender.sendMessage( "Could not list files in dir." );
						return;
					}
					int amount = userdatas.length;
					sender.sendMessage( "Found " + amount + " userdata configs. Start clearing shop delivery chest content" );
					for ( File userdata : userdatas ) {
						kConfig config = new kConfig( userdata );
						if (!config.contains( "shop.delivery.chestcontent" )) {
							continue;
						}
						config.set( "shop.delivery.chestcontent", null );
						config.save();
					}
					sender.sendMessage( "Done!" );
					break;
				case "showinv":
					if (!(sender instanceof Player)) {
						sender.sendMessage( "§cDu musst ein Spieler sein!" );
						return;
					}
					if (args.length != 2) {
						sender.sendMessage( "§cFalsche Anzahl an Argumenten!" );
						sender.sendMessage( "§6/" + alias + " showinv <name/uuid> §7- §eZeigt die Shopkisteninhalte des Spielers" );
						return;
					}
					UserDataConverter.Profile target = module.getPlugin().getUserDataConverter().getProfileFromInput( args[1] );
					sender.sendMessage( "Opening delivery chest of " + target.getName() + "/" + target.getUuid() + "/" + target.getPlayerId() + " premium: " + (target.getUuid().version() == 4) );
					module.getShopDeliveryHandler().openInventory( ( Player ) sender, target );
					break;
				default:
					sender.sendMessage( "§cUnknown subcommand " + args[0] );
					break;
			}
		} );
		return true;
	}
}
