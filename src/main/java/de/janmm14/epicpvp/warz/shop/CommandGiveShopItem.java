package de.janmm14.epicpvp.warz.shop;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Joiner;

import de.janmm14.epicpvp.warz.hooks.UserDataConverter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandGiveShopItem implements CommandExecutor {

	private static final Joiner SPACE_JOINER = Joiner.on( ' ' );

	private final ShopModule module;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if ( !sender.isOp() ) {
			return true;
		}
		if ( args.length != 2 && args.length != 3 ) {
			sender.sendMessage( "Could not execute /" + label + " " + SPACE_JOINER.join( args ) + " - Command argument count invalid" );
		}

		String plr = args[ 0 ];
		UserDataConverter.Profile profile = module.getPlugin().getUserDataConverter().getProfile( plr );
		if ( profile == null ) {
			sender.sendMessage( "Could not execute /" + label + " " + SPACE_JOINER.join( args ) + " - Player not found" );
			return true;
		}
		String itemSpec = args[ 1 ];
		ItemStack item;
		String[] split = itemSpec.split( ":" );
		String name = split[ 0 ];
		if ( split.length == 2 ) {
			String idStr = split[ 1 ];
			Material material = Material.matchMaterial( name );
			if ( material == null ) {
				sender.sendMessage( "Could not execute /" + label + " " + SPACE_JOINER.join( args ) + " - Invalid item name " + name );
				return true;
			}
			short id;
			try {
				id = Short.parseShort( idStr );
			}
			catch ( NumberFormatException ex ) {
				sender.sendMessage( "Could not execute /" + label + " " + SPACE_JOINER.join( args ) + " - Invalid data value " + idStr );
				return true;
			}
			item = new ItemStack( material );
			item.setDurability( id );
		} else if ( split.length == 1 ) {
			Material material = Material.matchMaterial( name );
			if ( material == null ) {
				sender.sendMessage( "Could not execute /" + label + " " + SPACE_JOINER.join( args ) + " - Invalid item name " + name );
				return true;
			}
			item = new ItemStack( material );
		} else {
			sender.sendMessage( "Could not execute /" + label + " " + SPACE_JOINER.join( args ) + " - Invalid item spec " + itemSpec );
			return true;
		}
		int amount = 1;
		if ( args.length == 3 ) {
			try {
				amount = Integer.parseInt( args[ 2 ] );
			}
			catch ( NumberFormatException ex ) {
				sender.sendMessage( "Could not execute /" + label + " " + SPACE_JOINER.join( args ) + " - Invalid item amount " + args[ 2 ] );
				return true;
			}
			if ( amount < 1 ) {
				sender.sendMessage( "Could not execute /" + label + " " + SPACE_JOINER.join( args ) + " - Negative item amount " + args[ 2 ] );
				return true;
			}
		}
		item.setAmount( amount );
		sender.sendMessage( "Delivering shop item from command /" + label + " " + SPACE_JOINER.join( args ) + " - item: " + item + " - profile: " + profile );
		boolean online = module.getShopDeliveryHandler().deliverItem( profile, item );
		sender.sendMessage( "Delivered to " + ( online ? "online" : "offline" ) + " player - item: " + item + " - profile: " + profile );
		return true;
	}
}
