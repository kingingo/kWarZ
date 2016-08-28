package de.janmm14.epicpvp.warz.zonechest;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.util.random.RandomUtil;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChestContentManager implements Runnable {

	private static final int REFILL_SECONDS = 5 * 60;

	private final ZoneAndChestsModule module;
	private final Cache<BlockVector, Inventory> createdInventories = CacheBuilder.newBuilder()
		.expireAfterWrite( REFILL_SECONDS, TimeUnit.SECONDS )
		.removalListener( new RemovalListener<BlockVector, Inventory>() {
			@Override
			public void onRemoval(@NonNull RemovalNotification<BlockVector, Inventory> notification) {
				Inventory inventory = notification.getValue();
				if ( inventory != null ) {
					inventory.getViewers().forEach( HumanEntity::closeInventory );
					inventory.clear();
				}
			}
		} )
		.build();

	private int secsUntilReset = REFILL_SECONDS;

	@Override
	public void run() {
		sendRefillTimer( secsUntilReset );
		if ( secsUntilReset == REFILL_SECONDS ) {
			reset();
			secsUntilReset--;
		} else if ( secsUntilReset <= 1 ) {
			secsUntilReset = REFILL_SECONDS;
		} else {
			secsUntilReset--;
		}
	}

	public void reset() {
		createdInventories.asMap()
			.forEach( (blockVector, inventory) -> {
				inventory.getViewers().forEach( HumanEntity::closeInventory );
				inventory.clear();
			} );
		createdInventories.invalidateAll();
	}

	private void sendRefillTimer(int secsUntilReset) {
		for ( Player plr : module.getPlugin().getServer().getOnlinePlayers() ) {
			plr.setLevel( secsUntilReset );
			if ( secsUntilReset == REFILL_SECONDS ) {
				plr.playSound( plr.getLocation(), Sound.LEVEL_UP, 1, 1 );
			}
		}
	}

	public Inventory getInventory(World world, BlockVector blockVector, CustomChestInventoryHolder owner) {
		Inventory inv = createdInventories.getIfPresent( blockVector );
		if ( inv == null ) {
			System.out.println( "Creating inventory for " + blockVector );
			inv = fillInventory( world, blockVector, Bukkit.createInventory( owner, InventoryType.CHEST ) );
			if ( inv != null ) {
				createdInventories.put( blockVector, inv );
			}
		}
		return inv;
	}

	private Inventory fillInventory(World world, BlockVector blockVector, Inventory inv) {
		Random random = new Random();
		Zone zone = module.getZone( world, blockVector );
		if ( zone == null ) {
			return null;
		}
		if ( WarZ.DEBUG )
			System.out.println( "zone " + zone.getWorldguardName() + " @" + Integer.toHexString( System.identityHashCode( zone ) ) );
		for ( ItemStack item : zone.getRandomChoosenChestItems() ) {
			if ( item == null ) {
				module.getPlugin().getLogger().warning( "Null element in chest at " + blockVector + ", contents: " + Arrays.asList( inv.getContents() ) );
				continue;
			}
			if ( item.getType() == Material.INK_SACK || item.getType() == Material.WEB ) {
				String s = String.valueOf( item.getAmount() );
				int lower, upper;
				switch ( s.length() ) {
					case 1: {
						lower = item.getAmount();
						upper = lower;
						break;
					}
					case 2: {
						char[] chars = s.toCharArray();
						lower = Integer.valueOf( String.valueOf( chars[ 0 ] ) );
						upper = Integer.valueOf( String.valueOf( chars[ 1 ] ) );
						break;
					}
					case 3: {
						char[] chars = s.toCharArray();
						lower = Integer.valueOf( String.valueOf( chars[ 0 ] ) );
						upper = Integer.valueOf( String.valueOf( chars[ 1 ] ) + chars[ 2 ] );
						break;
					}
					case 4: {
						char[] chars = s.toCharArray();
						lower = Integer.valueOf( String.valueOf( chars[ 0 ] ) + chars[ 1 ] );
						upper = Integer.valueOf( String.valueOf( chars[ 2 ] ) + chars[ 3 ] );
						break;
					}
					default: {
						lower = 1;
						upper = 1;
					}
				}
				if ( lower < 0 ) {
					lower = 1;
				}
				if ( lower > upper ) {
					upper = lower;
				}
				item.setAmount( RandomUtil.getRandomInt( lower, upper ) );
			}
			int tries = 0;
			while ( true ) {
				int pos = random.nextInt( inv.getSize() );
				ItemStack origItem = inv.getItem( pos );
				if ( origItem == null || origItem.getType() == Material.AIR ) {
					if ( WarZ.DEBUG )
						System.out.println( "Adding item " + item + " to chest at " + blockVector + " (pos: " + pos + ")" );
					inv.setItem( pos, item );
					break;
				} else {
					if ( ++tries > 100 ) {
						if ( WarZ.DEBUG )
							System.out.println( "No place found for " + item + " in chest at " + blockVector + ", contents: " + Arrays.asList( inv.getContents() ) );
						break;
					}
				}
			}
		}
		return inv;
	}
}
