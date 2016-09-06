package de.janmm14.epicpvp.warz.zonechest;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

import com.google.common.collect.ImmutableList;
import eu.epicpvp.kcore.Translation.TranslationHandler;

import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.util.random.RandomThingGroupHolder;
import de.janmm14.epicpvp.warz.util.random.RandomThingHolder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChestOpenListener implements Listener {

	private final ZoneAndChestsModule module;

	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		if ( event.getPlayer().isOp() && event.getAction() == Action.LEFT_CLICK_BLOCK ) {
			//allow block to be destroyed by ops
			return;
		}
		if ( event.hasBlock() && ( event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType() == Material.TRAPPED_CHEST ) ) {

			Player plr = event.getPlayer();

			BlockVector blockVector = event.getClickedBlock().getLocation().toVector().toBlockVector();
			BlockVector doubleChestBase = getDoubleChestBase( plr.getWorld(), blockVector );
			CustomChestInventoryHolder owner = new CustomChestInventoryHolder( blockVector );
			Inventory inv;
			if ( doubleChestBase != null ) {
				inv = module.getChestContentManager().getInventory( plr.getWorld(), doubleChestBase, owner, doubleChestBase == blockVector ? blockVector : doubleChestBase );
			} else {
				inv = module.getChestContentManager().getInventory( plr.getWorld(), blockVector, owner, null );
			}
			if ( inv != null ) {
				if ( WarZ.DEBUG )
					System.out.println( "Opening custom inventory for " + blockVector );
				event.setCancelled( true );
				owner.setInventory( inv );
				plr.openInventory( inv );
			} else {
				if ( WarZ.DEBUG )
					System.out.println( "Inventory is null for " + blockVector );
			}
		}
	}

	private static List<BlockFace> faces = ImmutableList.of( BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST );

	private static BlockVector getDoubleChestBase(World world, BlockVector blockVector) {
		Block block = blockVector.toLocation( world ).getBlock();
		BlockFace blockFace = faces.stream()
			.filter( face -> {
				Block relBlock = block.getRelative( face );
				return relBlock.getType() == block.getType();
			} )
			.findFirst().orElse( null );
		if ( blockFace == null ) {
			return null;
		}
		if ( blockFace == BlockFace.SOUTH || blockFace == BlockFace.EAST ) {
			return blockVector;
		}
		return block.getRelative( blockFace ).getLocation().toVector().toBlockVector();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event) { //TODO remove debug
		if ( !WarZ.DEBUG || !event.getPlayer().isOp() ) {
			return;
		}
		Player plr = event.getPlayer();
		if ( event.getMessage().equalsIgnoreCase( "zone" ) ) {
			event.setCancelled( true );
			Zone zone = module.getZone( plr.getLocation() );
			if ( zone == null ) {
				plr.sendMessage( TranslationHandler.getPrefixAndText( plr, "WARZ_ZONE_NOT" ) );
			} else {
				plr.sendMessage( TranslationHandler.getPrefixAndText( plr, "WARZ_ZONE_IS", zone.getZoneName(), zone.getWorldguardName() ) );
			}
		} else if ( event.getMessage().equalsIgnoreCase( "zone more" ) && plr.isOp() ) {
			event.setCancelled( true );
			Zone zone = module.getZone( plr.getLocation() );
			if ( zone == null ) {
				plr.sendMessage( TranslationHandler.getPrefixAndText( plr, "WARZ_ZONE_NOT" ) );
			} else {
				plr.sendMessage( TranslationHandler.getPrefixAndText( plr, "WARZ_ZONE_IS", zone.getZoneName(), zone.getWorldguardName() ) );
				Vector middle = zone.calculateMiddle();
				plr.sendMessage( TranslationHandler.getPrefixAndText( plr, "WARZ_ZONE_CALCULATE", middle.getX(), middle.getZ() ) );
				List<RandomThingGroupHolder<ItemStack>> itemGroups = zone.getItemGroups();
				plr.sendMessage( TranslationHandler.getPrefixAndText( plr, "WARZ_ZONE_ITEMS_CALCULATE", zone.getMinItemGroups(), zone.getMaxItemGroups(), itemGroups.size() ) );
				for ( int i = 0; i < itemGroups.size(); i++ ) {
					RandomThingGroupHolder<ItemStack> itemGroup = itemGroups.get( i );
					plr.sendMessage( TranslationHandler.getPrefixAndText( plr, "WARZ_ZONE_ITEMS_CALCULATE_LIKELIHOOD", i, itemGroup.getProbability() * 100.0 ) );
					List<RandomThingHolder<ItemStack>> items = itemGroup.getItem();
					plr.sendMessage( TranslationHandler.getPrefixAndText( plr, "WARZ_ZONE_ITEM_CALCULATE", itemGroup.getMinAmount(), itemGroup.getMaxAmount(), items.size() ) );
					for ( int j = 0; j < items.size(); j++ ) {
						RandomThingHolder<ItemStack> item = items.get( j );
						plr.sendMessage( "  #" + j + " " + item.getProbability() * 100.0 + "%: " + item.getItem() );
					}
				}
			}
		} else if ( event.getMessage().equalsIgnoreCase( "zones" ) && plr.isOp() ) {
			event.setCancelled( true );
			Map<String, Collection<Zone>> zoneMap = module.getZoneMap();
			for ( Map.Entry<String, Collection<Zone>> entry : zoneMap.entrySet() ) {
				String name = entry.getKey();
				Collection<Zone> zones = entry.getValue();
				plr.sendMessage( "Zone: " + name );
				for ( Zone zone : zones ) {
					if ( zone == null ) {
						plr.sendMessage( "  null" );
					} else {
						plr.sendMessage( "  " + zone.toShortString() );
					}
				}
			}
		}
	}
}
