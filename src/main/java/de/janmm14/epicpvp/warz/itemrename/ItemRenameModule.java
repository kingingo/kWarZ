package de.janmm14.epicpvp.warz.itemrename;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.util.MiscUtil;

public class ItemRenameModule extends Module<ItemRenameModule> implements Listener {

	private static final String PATH_PREFIX = "itemrename.";
	private static final String ITEM_PATH_PREFIX = PATH_PREFIX + "items";

	private Multimap<String, String> itemNamesAndLores = HashMultimap.create();//have some default that will never occurr in the iterable
	@SuppressWarnings("RedundantStringConstructorCall")
	private static final String NOT_PRESENT_VALUE = new String( "notPresentValue" );

	public ItemRenameModule(WarZ plugin) {
		super( plugin, module -> module );
	}

	@Override
	public void reloadConfig() {
		FileConfiguration cfg = getConfig();

		cfg.addDefault( ITEM_PATH_PREFIX + ".7:0", "&c&lBedrock" );
		cfg.addDefault( ITEM_PATH_PREFIX + ".7:1", Arrays.asList( null, "&c&lBedrock" ) );

		ConfigurationSection itemSection = cfg.getConfigurationSection( ITEM_PATH_PREFIX );
		for ( String key : itemSection.getKeys( false ) ) {
			if ( key.startsWith( "7:" ) ) {
				continue;
			}
			List<String> stringList = itemSection.getStringList( key );
			if ( stringList == null || stringList.isEmpty() ) {
				itemNamesAndLores.put( key, itemSection.getString( key ) );
			} else {
				itemNamesAndLores.putAll( key, stringList );
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPickup(PlayerPickupItemEvent event) {
		Item item = event.getItem();
		ItemStack is = item.getItemStack();
		if ( renameIfNeeded( is ) ) {
			item.setItemStack( is );
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPickup(InventoryCreativeEvent event) {
		ItemStack is = event.getCursor();
		if ( renameIfNeeded( is ) ) {
			event.setCursor( is );
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryOpen(InventoryOpenEvent event) {
		HumanEntity player = event.getPlayer();

		if ( renameInventoryOf( player ) ) {
			( ( Player ) player ).updateInventory();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		getPlugin().getServer().getScheduler().runTaskLater( getPlugin(), () -> {
			Player player = event.getPlayer();
			if ( renameInventoryOf( player ) ) {
				player.updateInventory();
			}
		}, 5 );
	}

	private boolean renameInventoryOf(HumanEntity player) {
		ItemStack[] contents = player.getInventory().getContents();
		boolean renamed = renameItemStackArray( contents );
		if ( renamed ) {
			player.getInventory().setContents( contents );
		}
		return renamed;
	}

	private boolean renameItemStackArray(ItemStack[] contents) {
		boolean renamed = false;
		for ( int i = 0; i < contents.length; i++ ) {
			ItemStack is = contents[ i ];
			if ( renameIfNeeded( is ) ) {
				contents[ i ] = is;
				renamed = true;
			}
		}
		return renamed;
	}

	@SuppressWarnings({ "deprecation", "StringEquality" })
	public boolean renameIfNeeded(ItemStack is) {
		if ( is == null ) {
			return false;
		}
		int id = is.getTypeId();
		byte data = is.getData().getData();
		Collection<String> nameAndLore = itemNamesAndLores.get( id + ":" + data );
		if ( nameAndLore == null ) {
			nameAndLore = itemNamesAndLores.get( String.valueOf( id ) );
		}
		if ( nameAndLore == null ) {
			nameAndLore = itemNamesAndLores.get( is.getType() + ":" + data );
		}
		if ( nameAndLore == null ) {
			nameAndLore = itemNamesAndLores.get( is.getType().toString() );
		}
		String name = Iterables.getFirst( nameAndLore, NOT_PRESENT_VALUE );
		if ( name != NOT_PRESENT_VALUE ) {
			ItemMeta im = is.getItemMeta();
			if ( name != null && !name.equals( "null" ) ) {
				im.setDisplayName( MiscUtil.translateColorCode( name ) );
			}
			if ( nameAndLore.size() > 1 ) {
				List<String> lore = nameAndLore.stream()
					.map( MiscUtil::translateColorCode )
					.collect( Collectors.toList() );
				lore.remove( 0 );
				im.setLore( lore );
			}
			is.setItemMeta( im );
			return true;
		}
		return false;
	}
}
