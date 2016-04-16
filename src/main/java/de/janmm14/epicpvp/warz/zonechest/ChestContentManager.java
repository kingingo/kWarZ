package de.janmm14.epicpvp.warz.zonechest;

import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChestContentManager implements Runnable {

	private static final int REFILL_SECONDS = 5 * 60;

	private final ZoneAndChestsModule module;
	private final Cache<BlockVector, Inventory> createdInventories = CacheBuilder.newBuilder()
		.expireAfterWrite( 5, TimeUnit.MINUTES )
		.build();

	private int secsUntilReset = REFILL_SECONDS;

	@Override
	public void run() {
		if ( secsUntilReset <= 0 ) {
			createdInventories.asMap()
				.forEach( (blockVector, inventory) -> {
					inventory.getViewers().forEach( HumanEntity::closeInventory );
					inventory.clear();
				} );
			createdInventories.invalidateAll();
			secsUntilReset = REFILL_SECONDS;
		} else {
			secsUntilReset--;
		}
		sendRefillTimer( secsUntilReset );
	}

	private void sendRefillTimer(int secsUntilReset) {
		for ( Player plr : module.getPlugin().getServer().getOnlinePlayers() ) {
			plr.setLevel( secsUntilReset );
		}
	}

	public void getInventory(World world, BlockVector blockVector, Inventory inv) {
		try {
			createdInventories.get( blockVector, () -> fillInventory( world, blockVector, inv ) );
		}
		catch ( ExecutionException e ) {
			e.printStackTrace();
		}
	}

	private Inventory fillInventory(World world, BlockVector blockVector, Inventory inv) {
		Random random = new Random();
		int itemAmount = module.getMinItemAmount() + random.nextInt( module.getMaxItemAmount() - module.getMinItemAmount() + 1 ); //+1 -> inclsive maximum
		for ( int i = 0; i < itemAmount; i++ ) {
			int pos = random.nextInt( inv.getSize() );
			ItemStack item = module.getZone( world, blockVector ).getRandomChoosenChestItems();
			inv.setItem( pos, item );
		}
		return inv;
	}
}
