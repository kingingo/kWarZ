package de.janmm14.epicpvp.warz.cobwebs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.util.Tuple;

public class CobWebModule extends Module<CobWebModule> implements Listener, Runnable {

	private static final String PATH_PREFIX = "cobweb.";
	private final Map<BlockVector, Tuple<BlockState, Long>> blockStates = new HashMap<>( 32 );
	private long webMillis = TimeUnit.SECONDS.toMillis( 30 );

	public CobWebModule(WarZ plugin) {
		super( plugin, (module) -> module );
		plugin.getServer().getScheduler().runTaskTimer( plugin, this, 25 * 20, 5 );
	}

	@Override
	public void reloadConfig() {
		getConfig().addDefault( PATH_PREFIX + "resetseconds", 30 );
		webMillis = TimeUnit.SECONDS.toMillis( getConfig().getInt( PATH_PREFIX + "resetseconds" ) );
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event) {
		if ( event.getBlockPlaced().getType() != Material.WEB ) {
			if ( !event.getPlayer().isOp() ) {
				event.setCancelled( true );
			}
		} else {
			BlockState oldBlockState = event.getBlockReplacedState();
			blockStates.put( oldBlockState.getLocation().toVector().toBlockVector(), new Tuple<>( oldBlockState, System.currentTimeMillis() ) );
		}
	}

	@Override
	public void run() {
		if ( blockStates.isEmpty() ) {
			return;
		}
		Iterator<Map.Entry<BlockVector, Tuple<BlockState, Long>>> iterator = blockStates.entrySet().iterator(); //using iterator because of Iterator.remove()
		while ( iterator.hasNext() ) {
			Tuple<BlockState, Long> value = iterator.next().getValue();
			if ( value.getB() < System.currentTimeMillis() - webMillis ) {
				value.getA().update( true, false );
				iterator.remove();
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if ( event.hasBlock() && event.hasItem() ) {
			ItemStack item = event.getItem();
			Block clickedBlock = event.getClickedBlock();
			if ( item.getType() == Material.SHEARS && clickedBlock.getType() == Material.WEB ) {
				Tuple<BlockState, Long> tuple = blockStates.get( clickedBlock.getLocation().toVector().toBlockVector() );
				if ( tuple != null ) {
					tuple.getA().update( true, false );
				} else {
					clickedBlock.setType( Material.AIR, false );
				}
				Player plr = event.getPlayer();
				short durability = item.getDurability();
				if (WarZ.DEBUG) {
					getPlugin().getServer().broadcastMessage( "durability: " + durability );
				}
				if ( durability > 1 ) {
					item.setDurability( --durability );
					plr.setItemInHand( item );
				} else {
					plr.setItemInHand( null );
				}
				plr.updateInventory();
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if ( event.getBlock().getType() == Material.WEB ) {
			event.setCancelled( true );
		}
	}
}
