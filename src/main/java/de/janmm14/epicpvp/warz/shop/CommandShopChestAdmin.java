package de.janmm14.epicpvp.warz.shop;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
			}
		} );
		return true;
	}
}
