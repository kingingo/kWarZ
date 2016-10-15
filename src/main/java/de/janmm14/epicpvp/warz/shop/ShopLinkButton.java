package de.janmm14.epicpvp.warz.shop;

import org.bukkit.inventory.ItemStack;

import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonCopy;
import eu.epicpvp.kcore.Translation.TranslationHandler;

public class ShopLinkButton extends ButtonCopy {

	public ShopLinkButton(ItemStack item, int buycraftId) {
		super( (player, type, object) -> {
			},
			(player, type, object) -> {
				player.sendMessage( TranslationHandler.getPrefixAndText( player, "WARZ_DELIVERY_LINK", "https://shop.EpicPvP.eu/checkout/packages?action=add&package=" + buycraftId + "&ign=" + player.getName() ) );
			}, item );
	}
}
