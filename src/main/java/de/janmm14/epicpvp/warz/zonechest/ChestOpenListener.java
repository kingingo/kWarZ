package de.janmm14.epicpvp.warz.zonechest;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.util.random.RandomThingGroupHolder;
import de.janmm14.epicpvp.warz.util.random.RandomThingHolder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChestOpenListener implements Listener {

	private final ZoneAndChestsModule module;

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if ( event.getPlayer().isOp() && event.getAction() == Action.LEFT_CLICK_BLOCK ) {
			event.getPlayer().sendMessage( "Opening raw chest as you're op and used left click" );
			return;
		}
		if ( event.hasBlock() && ( event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType() == Material.TRAPPED_CHEST ) ) {

			Player plr = event.getPlayer();

			BlockVector blockVector = plr.getLocation().toVector().toBlockVector();
			CustomChestInventoryHolder owner = new CustomChestInventoryHolder( blockVector );
			Inventory inv = module.getChestContentManager().getInventory( plr.getWorld(), blockVector, owner );
			if ( inv != null ) {
				System.out.println( "Opening custom inventory for " + blockVector );
				event.setCancelled( true );
				owner.setInventory( inv );
				plr.openInventory( inv );
			} else {
				System.out.println( "Inventory is null for " + blockVector );
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event) { //TODO remove debug
		Player plr = event.getPlayer();
		if ( event.getMessage().equalsIgnoreCase( "zone" ) ) {
			Zone zone = WarZ.getPlugin( WarZ.class ).getModuleManager().getModule( ZoneAndChestsModule.class ).getZone( plr.getLocation() );
			if ( zone == null ) {
				plr.sendMessage( "Keine Zone definiert" );
			} else {
				plr.sendMessage( "Du bist in der Zone " + zone.getZoneName() );
			}
		}
		if ( event.getMessage().equalsIgnoreCase( "zone more" ) ) {
			Zone zone = WarZ.getPlugin( WarZ.class ).getModuleManager().getModule( ZoneAndChestsModule.class ).getZone( plr.getLocation() );
			if ( zone == null ) {
				plr.sendMessage( "Keine Zone definiert" );
			} else {
				plr.sendMessage( "Du bist in der Zone " + zone.getZoneName() );
				Vector middle = zone.calculateMiddle();
				plr.sendMessage( "Errechnete Mitte: x: " + middle.getX() + " z:" + middle.getZ() );
				List<RandomThingGroupHolder<ItemStack>> itemGroups = zone.getItemGroups();
				plr.sendMessage( "ItemGruppen: min: " + zone.getMinItemGroups() + " max: " + zone.getMaxItemGroups() + " Anzahl: " + itemGroups.size() );
				for ( int i = 0; i < itemGroups.size(); i++ ) {
					RandomThingGroupHolder<ItemStack> itemGroup = itemGroups.get( i );
					plr.sendMessage( "ItemGruppe " + i + " Wahrscheinlichkeit: " + itemGroup.getProbability() * 100.0 + "%" );
					List<RandomThingHolder<ItemStack>> items = itemGroup.getItem();
					plr.sendMessage( "  Items min: " + itemGroup.getMinAmount() + " max: " + itemGroup.getMaxAmount() + " Anzahl: " + items.size() );
					for ( int j = 0; j < items.size(); j++ ) {
						RandomThingHolder<ItemStack> item = items.get( j );
						plr.sendMessage( "  #" + j + " " + item.getProbability() * 100.0 + "%: " + item.getItem() );
					}
				}
			}
		}
	}
}
