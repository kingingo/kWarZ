package de.janmm14.epicpvp.warz.zonechest;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
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
				if (inventory != null) {
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
			createdInventories.asMap()
				.forEach( (blockVector, inventory) -> {
					inventory.getViewers().forEach( HumanEntity::closeInventory );
					inventory.clear();
				} );
			createdInventories.invalidateAll();
			secsUntilReset--;
		} else if (secsUntilReset <= 1) {
			secsUntilReset = REFILL_SECONDS;
		} else {
			secsUntilReset--;
		}
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
		for ( ItemStack item : zone.getRandomChoosenChestItems() ) {
			int pos = random.nextInt( inv.getSize() );
			inv.setItem( pos, item );
		}
		return inv;
	}
}
