package de.janmm14.epicpvp.warz.compass;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Inventory.Inventory.InventoryCopy;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonCopy;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;

public class CompassButton extends ButtonCopy{

	public CompassButton(CompassTargetModule module,CompassTarget target,int slot, ItemStack item,InventoryCopy inv) {
		super(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				CompassTarget ptarget = module.getCompassTarget( player );
				
				if(ptarget==target){
					((InventoryPageBase)object).setItem(slot, UtilItem.addEnchantmentGlow(item.clone()));
				}
			}
			
		},new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				CompassTarget ptarget = module.getCompassTarget( player );
				
				if(ptarget!=target){
					module.setCompassTarget(player, target);
					player.closeInventory();
					inv.open(player, UtilInv.getBase());
				}
			}
			
		}, item);
	}

}
