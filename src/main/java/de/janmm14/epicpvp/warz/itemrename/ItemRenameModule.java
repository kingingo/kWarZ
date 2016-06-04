package de.janmm14.epicpvp.warz.itemrename;

import java.util.HashMap;
import java.util.Map;

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

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class ItemRenameModule extends Module<ItemRenameModule> implements Listener {

	private static final String PATH_PREFIX = "itemrename.";
	private static final String ITEM_PATH_PREFIX = PATH_PREFIX + "items";

	private Map<String, String> itemNames = new HashMap<>();

	public ItemRenameModule(WarZ plugin) {
		super( plugin, module -> module );
	}

	@Override
	public void reloadConfig() {
		FileConfiguration cfg = getPlugin().getConfig();

		cfg.addDefault( ITEM_PATH_PREFIX + ".7:0", "&c&lBedrock" );

		ConfigurationSection itemSection = cfg.getConfigurationSection( ITEM_PATH_PREFIX );
		for ( String key : itemSection.getKeys( false ) ) {
			itemNames.put( key, itemSection.getString( key ) );
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

		if ( renameInventory( player ) ) {
			( ( Player ) player ).updateInventory();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		getPlugin().getServer().getScheduler().runTaskLater( getPlugin(), () -> {
			Player player = event.getPlayer();
			if ( renameInventory( player ) ) {
				player.updateInventory();
			}
		}, 5);
	}

	private boolean renameInventory(HumanEntity player) {
		ItemStack[] contents = player.getInventory().getContents();
		boolean renamed = renameInventory0( contents );
		if (renamed) {
			player.getInventory().setContents( contents );
		}
		return renamed;
	}

	private boolean renameInventory0(ItemStack[] contents) {
		boolean renamed = false;
		for ( int i = 0; i < contents.length; i++ ) {
			ItemStack is = contents[ i ];
			if ( renameIfNeeded( is ) ) {
				contents[i] = is;
				renamed = true;
			}
		}
		return renamed;
	}

	@SuppressWarnings("deprecation")
	private boolean renameIfNeeded(ItemStack is) {
		int id = is.getTypeId();
		byte data = is.getData().getData();
		String name = itemNames.get( id + ":" + data );
		if ( name == null ) {
			name = itemNames.get( String.valueOf( id ) );
		}
		if ( name != null ) {
			ItemMeta im = is.getItemMeta();
			im.setDisplayName( name );
			is.setItemMeta( im );
			return true;
		}
		return false;
	}
}
