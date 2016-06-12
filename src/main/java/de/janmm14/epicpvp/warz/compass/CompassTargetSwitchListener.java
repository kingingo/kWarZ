package de.janmm14.epicpvp.warz.compass;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.kcore.Inventory.Inventory.InventoryCopy;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;

import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.zonechest.Zone;
import de.janmm14.epicpvp.warz.zonechest.ZoneAndChestsModule;

public class CompassTargetSwitchListener implements Listener {

	private InventoryCopy selectionInventory;

	public CompassTargetSwitchListener(CompassTargetModule module) {
		this.selectionInventory = new InventoryCopy( InventorySize._9, "Set your Compass target" );
		this.selectionInventory.addButton( 1, new CompassButton( module, CompassTarget.ENEMY, 1, UtilItem.Item( new ItemStack( Material.STAINED_CLAY, 1, (short) 14), new String[]{}, "§cEnemies" ), this.selectionInventory ) );
		this.selectionInventory.addButton( 4, new CompassButton( module, CompassTarget.FRIEND, 4, UtilItem.Item( new ItemStack( Material.STAINED_CLAY, 1, (short) 5), new String[]{}, "§aFriends" ), this.selectionInventory ) );
		this.selectionInventory.addButton( 7, new CompassButton( module, CompassTarget.ZONE, 7, UtilItem.Item( new ItemStack( Material.FENCE ), new String[]{}, "§6Zones" ), this.selectionInventory ) );
		this.selectionInventory.fill( Material.STAINED_GLASS_PANE, 7 );
		this.selectionInventory.setCreate_new_inv( true );
		UtilInv.getBase().addPage( this.selectionInventory );
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInteract(PlayerInteractEvent event) {
		if ( event.getPlayer().getGameMode() != GameMode.CREATIVE && event.hasItem() && event.getItem().getType() == Material.COMPASS ) {
			event.setCancelled( true );
			openSelectionInventory( event.getPlayer() );
		}
	}

	private void openSelectionInventory(Player plr) {
		this.selectionInventory.open( plr, UtilInv.getBase() );
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event) {
		if (event.getMessage().equalsIgnoreCase( "zone" )) {
			Zone zone = WarZ.getPlugin( WarZ.class ).getModuleManager().getModule( ZoneAndChestsModule.class ).getZone( event.getPlayer().getLocation() );
			if (zone == null) {
				event.getPlayer().sendMessage( "Keine Zone definiert" );
			} else {
				event.getPlayer().sendMessage( zone.getName() );
			}
		}
	}
}
