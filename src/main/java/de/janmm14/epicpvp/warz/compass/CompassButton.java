package de.janmm14.epicpvp.warz.compass;

import org.bukkit.inventory.ItemStack;

import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Inventory.Inventory.InventoryCopy;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonCopy;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;

public class CompassButton extends ButtonCopy {

	public CompassButton(CompassTargetModule module, CompassTarget target, int slot, ItemStack item, InventoryCopy inv) {
		super( (player, type, object) -> {
			CompassTarget ptarget = module.getCompassTarget( player );

			if ( ptarget == target ) {
				( ( InventoryPageBase ) object ).setItem( slot, UtilItem.addEnchantmentGlow( item.clone() ) );
			}
		}, (player, type, object) -> {
			CompassTarget ptarget = module.getCompassTarget( player );

			if ( ptarget != target ) {
				module.setCompassTarget( player, target );
				inv.open( player, UtilInv.getBase() );
			}
		}, item );
	}
}
