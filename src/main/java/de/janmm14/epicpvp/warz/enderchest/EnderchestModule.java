package de.janmm14.epicpvp.warz.enderchest;

import java.lang.reflect.Field;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.kcore.Permission.Events.PlayerLoadPermissionEvent;
import eu.epicpvp.kcore.Permission.Permission;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.UserDataConfig.Events.UserDataConfigRemoveEvent;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.kConfig.kConfig;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.InventoryEnderChest;
import net.minecraft.server.v1_8_R3.InventorySubcontainer;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class EnderchestModule extends Module<EnderchestModule> implements Listener {

	public EnderchestModule(WarZ plugin) {
		super( plugin, module -> module );
	}

	@Override
	public void reloadConfig() {
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent ev) {
		if ( UtilEvent.isAction( ev, ActionType.RIGHT_BLOCK ) ) {
			if ( !ev.getPlayer().hasPermission( PermissionType.ENDERCHEST_USE.getPermissionToString() ) ) {
				ev.setCancelled( true );
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent ev) {
		if ( !( ev.getWhoClicked() instanceof Player ) || ev.getInventory() == null || ev.getCursor() == null || ev.getCurrentItem() == null )
			return;

		if ( ev.getCurrentItem().getType() == Material.IRON_FENCE
			&& ev.getCurrentItem().hasItemMeta()
			&& ev.getCurrentItem().getItemMeta().hasDisplayName()
			&& ev.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase( "§0" ) ) {
			ev.setCancelled( true );
		}
	}

	//epicpvp.warz.enderchest.size.1-54
	@EventHandler
	public void onPermissionLoad(PlayerLoadPermissionEvent ev) {
		if ( getPlugin().getUserDataConfig().contains( ev.getPlayer() ) ) {
			kConfig config = getPlugin().getUserDataConfig().getConfig( ev.getPlayer() );
			ItemStack[] items;
			if ( !config.isSet( "Enderchest" ) ) {
				items = new ItemStack[ 54 ];
				for ( int i = 0; i < ev.getPlayer().getEnderChest().getContents().length; i++ ) {
					items[ i ] = ev.getPlayer().getEnderChest().getContents()[ i ];
				}
				config.setItemStackArray( "Enderchest", items );
			}
			items = config.getItemStackArray( "Enderchest" );
			int use_amount = 0;

			if ( ev.getPlayer().isOp() || ev.getPlayer().hasPermission( PermissionType.ALL_PERMISSION.getPermissionToString() ) ) {
				use_amount = 54;
			} else {
				for ( Permission perm : ev.getManager().getPermissionPlayer( ev.getPlayer() ).getPermissions() ) {
					if ( perm.getPermission().contains( "epicpvp.warz.enderchest.size." ) ) {
						use_amount = Integer.valueOf( perm.getPermission().substring( "epicpvp.warz.enderchest.size.".length(), perm.getPermission().length() ) );
						break;
					}
				}
			}

			for ( int i = 0; i < 54; i++ ) {
				if ( ( i + 1 ) > use_amount ) {
					items[ i ] = UtilItem.RenameItem( new ItemStack( Material.IRON_FENCE ), "§0" );
				} else {
					if ( items[ i ] != null
						&& items[ i ].getType() == Material.IRON_FENCE
						&& items[ i ].hasItemMeta()
						&& items[ i ].getItemMeta().hasDisplayName()
						&& items[ i ].getItemMeta().getDisplayName().equalsIgnoreCase( "§0" ) ) {
						items[ i ] = null;
					}
				}
			}

			setEnderchest( ev.getPlayer(), UtilItem.convertItemStackArray( items ) );
		}
		ev.getManager().getPermissionPlayer( ev.getPlayer() ).addPermission( PermissionType.ENDERCHEST_USE.getPermissionToString() );
	}

	@EventHandler
	public void onQuit(UserDataConfigRemoveEvent ev) {
		if ( ev.getPlayer().getEnderChest().getContents().length == 54 ) {
			ev.getConfig().setItemStackArray( "Enderchest", ev.getPlayer().getEnderChest().getContents() );
		}
	}

	private static void setEnderchest(Player player, net.minecraft.server.v1_8_R3.ItemStack[] itemStack) {
		CraftPlayer ep = ( CraftPlayer ) player;
		net.minecraft.server.v1_8_R3.Entity e = getNMSEntity( ep );
		EntityLiving living = ( EntityLiving ) e;
		EntityHuman human = ( EntityHuman ) living;
		InventoryEnderChest echest = human.getEnderChest();
		setNMSItemStackArray( echest, itemStack );
	}

	private static void setNMSItemStackArray(InventorySubcontainer inventorySubcontainer, net.minecraft.server.v1_8_R3.ItemStack[] item) {
		try {
			Field slots = InventorySubcontainer.class.getDeclaredField( "b" );
			slots.setAccessible( true );
			slots.set( inventorySubcontainer, item.length );

			Field items = InventorySubcontainer.class.getDeclaredField( "items" );
			items.setAccessible( true );
			items.set( inventorySubcontainer, item );
		}
		catch ( Exception e ) {
			throw new RuntimeException( e );
		}
	}

	private static net.minecraft.server.v1_8_R3.ItemStack[] getNMSItemStackArray(InventorySubcontainer s) {
		try {
			Field pField = InventorySubcontainer.class.getDeclaredField( "items" );
			pField.setAccessible( true );
			return ( net.minecraft.server.v1_8_R3.ItemStack[] ) pField.get( s );
		}
		catch ( Exception e ) {
			throw new RuntimeException( e );
		}
	}

	private static net.minecraft.server.v1_8_R3.Entity getNMSEntity(CraftEntity ep) {
		try {
			Field pField = CraftEntity.class.getDeclaredField( "entity" );
			pField.setAccessible( true );

			return ( net.minecraft.server.v1_8_R3.Entity ) pField.get( ep );
		}
		catch ( Exception e ) {
			throw new RuntimeException( e );
		}
	}
}
