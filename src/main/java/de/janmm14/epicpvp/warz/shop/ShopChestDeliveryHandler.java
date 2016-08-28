package de.janmm14.epicpvp.warz.shop;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

import com.sun.org.apache.regexp.internal.RE;
import eu.epicpvp.kcore.UserDataConfig.UserDataConfig;
import eu.epicpvp.kcore.kConfig.kConfig;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import de.janmm14.epicpvp.warz.hooks.UserDataConverter;
import de.janmm14.epicpvp.warz.itemrename.ItemRenameModule;

public class ShopChestDeliveryHandler implements Listener {

	private static final net.md_5.bungee.api.chat.BaseComponent[] NO_ITEMS_TO_DELIVER_MSG = new ComponentBuilder( "" )
		.append( "Es warten aktuell keine Items auf dich.\n" ).color( ChatColor.RED )
		.append( "Klicke " ).color( ChatColor.GOLD )
		.append( "hier" )
			.underlined( true )
			.event( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "" ).append( "Klicke, um den Shop zu öffnen." ).color( ChatColor.GOLD ).create() ) )
			.event( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/shop" ) )
		.append( " oder gebe /shop ein, um dir ein Item zu kaufen.", ComponentBuilder.FormatRetention.FORMATTING ).underlined( false )
		.create();
	private final ShopModule module;
	private final TIntObjectMap<Inventory> inventories = new TIntObjectHashMap<>();
	private final UserDataConfig userDataConfig;

	public ShopChestDeliveryHandler(ShopModule module) {
		this.module = module;
		userDataConfig = module.getPlugin().getUserDataConfig();
	}

	public void deliverItem(UserDataConverter.Profile profile, ItemStack item) {
		Player plr = module.getPlugin().getServer().getPlayer( profile.getUuid() );
		if ( plr != null ) {
			inventories.get( profile.getPlayerId() ).addItem( item );
			return;
		}
		Inventory inv = loadInventory( profile );
		inv.addItem( item );
		saveInventory( profile, inv );
	}

	private Inventory loadInventory(UserDataConverter.Profile profile) {
		kConfig config = userDataConfig.getConfig( profile.getPlayerId() );
		ItemStack[] items = config.getItemStackArray( "shop.delivery.chestcontent" );
		Inventory inv = module.getPlugin().getServer().createInventory( null, 6 * 9, "§6Shop delivery" );
		for ( ItemStack item : items ) {
			if ( item == null ) {
				continue;
			}
			inv.addItem( item );
		}
		return inv;
	}

	private void saveInventory(UserDataConverter.Profile profile, Inventory inv) {
		kConfig config = userDataConfig.getConfig( profile.getPlayerId() );
		config.setItemStackArray( "shop.delivery.chestcontent", inv.getContents() );
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		UserDataConverter.Profile profile = module.getPlugin().getUserDataConverter().getProfile( event.getPlayer() );
		inventories.put( profile.getPlayerId(), loadInventory( profile ) );
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		UserDataConverter.Profile profile = module.getPlugin().getUserDataConverter().getProfile( event.getPlayer() );
		inventories.remove( profile.getPlayerId() );
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onKick(PlayerKickEvent event) {
		UserDataConverter.Profile profile = module.getPlugin().getUserDataConverter().getProfile( event.getPlayer() );
		inventories.remove( profile.getPlayerId() );
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInventoryOpen(PlayerInteractEvent event) {
		Player plr = event.getPlayer();
		if ( plr.isOp() && event.getAction() == Action.LEFT_CLICK_BLOCK ) {
			//allow block to be destroyed by ops
			return;
		}
		BlockVector chestLoc = module.getDeliveryChestLocation();
		if ( chestLoc == null ) {
			return;
		}
		Block block = event.getClickedBlock();
		if ( event.hasBlock() && ( block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST ) ) {
			if ( chestLoc.equals( block.getLocation().toVector().toBlockVector() ) ) {
				event.setCancelled( true );
				openInvetory( plr );
			}
		}
	}

	public void openInvetory(Player plr) {
		Inventory inv = inventories.get( module.getPlugin().getUserDataConverter().getProfile( plr ).getPlayerId() );
		if ( inv == null || isEmpty( inv ) ) {
			plr.spigot().sendMessage( NO_ITEMS_TO_DELIVER_MSG );
			return;
		}
		ItemStack[] contents = inv.getContents();
		if (module.getModuleManager().getModule( ItemRenameModule.class ).renameItemStackArray( contents )) {
			inv.setContents( contents );
		}
		plr.openInventory( inv );
	}

	private boolean isEmpty(Inventory inv) {
		for ( ItemStack item : inv.getContents() ) {
			if ( item != null && item.getType() != Material.AIR ) {
				return false;
			}
		}
		return true;
	}
}
