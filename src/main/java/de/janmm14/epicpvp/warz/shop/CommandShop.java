package de.janmm14.epicpvp.warz.shop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandShop implements CommandExecutor {

	private final ShopModule module;

	public CommandShop(ShopModule module) {
		this.module = module;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ( !( sender instanceof Player ) ) {
			sender.sendMessage( "§cNur für Spieler." );
			return true;
		}
		module.getBuyInventoryHandler().openInventory( ( Player ) sender );
		return false;
	}
}
