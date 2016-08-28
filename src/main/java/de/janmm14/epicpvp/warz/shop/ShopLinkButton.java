package de.janmm14.epicpvp.warz.shop;

import org.bukkit.inventory.ItemStack;

import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonCopy;

public class ShopLinkButton extends ButtonCopy {

	public ShopLinkButton(ItemStack item, int buycraftId) {
		super( (player, type, object) -> {
			},
			(player, type, object) -> {
				player.sendMessage( "§aKlicke auf diesen Link, um das gewählte Item in den Warenkorb zu legen:\n" +
					"§6§nhttps://shop.clashmc.eu/checkout/packages?action=add&package=" + buycraftId + "&ign=" + player.getName() );
			}, item );
	}
}
