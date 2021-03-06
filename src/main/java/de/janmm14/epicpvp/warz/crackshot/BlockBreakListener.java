package de.janmm14.epicpvp.warz.crackshot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.BlockVector;

import com.shampaggon.crackshot.events.WeaponHitBlockEvent;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.util.Tuple;
import eu.epicpvp.kcore.Util.UtilWorldGuard;

public class BlockBreakListener implements Listener, Runnable {

	private static final Map<BlockVector, Tuple<BlockState, Long>> blockStates = new HashMap<>( 32 );
	private final CrackShotTweakModule module;

	public BlockBreakListener(CrackShotTweakModule module) {
		this.module = module;
		WarZ plugin = module.getPlugin();
		plugin.getServer().getScheduler().runTaskTimer( plugin, this, 25 * 20, 5 );
	}

	@Override
	public void run() {
		if ( blockStates.isEmpty() ) {
			return;
		}
		Iterator<Map.Entry<BlockVector, Tuple<BlockState, Long>>> iterator = blockStates.entrySet().iterator(); //using iterator because of Iterator.remove()
		while ( iterator.hasNext() ) {
			Tuple<BlockState, Long> value = iterator.next().getValue();
			if ( value.getB() < System.currentTimeMillis() - module.getGlassMillis() ) {
				value.getA().update( true, false );
				iterator.remove();
			}
		}
	}

	@EventHandler
	public void onWeaponBlockHit(WeaponHitBlockEvent event) {
		Block block = event.getBlock();
		if ( !UtilWorldGuard.RegionFlag( event.getBlock().getLocation(), DefaultFlag.PVP ) ) {
			return;
		}
		switch ( block.getType() ) {
			case GLASS:
			case STAINED_GLASS:
			case STAINED_GLASS_PANE:
			case THIN_GLASS: {
				BlockState oldBlockState = block.getState();
				block.setType( Material.AIR, false );
				blockStates.put( oldBlockState.getLocation().toVector().toBlockVector(), new Tuple<>( oldBlockState, System.currentTimeMillis() ) );
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if ( !UtilWorldGuard.RegionFlag( block.getLocation(), DefaultFlag.PVP ) ) {
			return;
		}
		switch ( block.getType() ) {
			case GLASS:
			case STAINED_GLASS:
			case STAINED_GLASS_PANE:
			case THIN_GLASS: {
				BlockState oldBlockState = block.getState();
				block.setType( Material.AIR, false );
				blockStates.put( oldBlockState.getLocation().toVector().toBlockVector(), new Tuple<>( oldBlockState, System.currentTimeMillis() ) );
				break;
			}
			default: {
				if ( !event.getPlayer().isOp() ) {
					event.setCancelled( true );
				}
			}
		}
	}
}
