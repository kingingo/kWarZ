package de.janmm14.epicpvp.warz.spawn;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import eu.epicpvp.kcore.PacketAPI.Packets.WrapperPacketPlayOutWorldBorder;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilWorld;
import eu.epicpvp.kcore.Util.UtilWorldGuard;
import eu.epicpvp.kcore.kConfig.kConfig;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.itemrename.ItemRenameModule;
import de.janmm14.epicpvp.warz.util.ConfigLocationAdapter;
import de.janmm14.epicpvp.warz.zonechest.Zone;
import lombok.Getter;

public class SpawnModule extends Module<SpawnModule> implements Listener {

	@Getter
	private Location spawn;
	@Getter
	private ArrayList<Location> mapSpawns;
	private ConfigLocationAdapter config;
	private WrapperPacketPlayOutWorldBorder border;

	public SpawnModule(WarZ plugin) {
		super( plugin, module -> module );
		plugin.getCommand( "spawn" ).setExecutor( new CommandSpawn( this ) );
		plugin.getCommand( "back" ).setExecutor( new CommandBack( this ) );
	}

	@Override
	public void reloadConfig() {
		config = new ConfigLocationAdapter( getConfig() );
		spawn = config.getLocation( "spawnLocation" );
		spawn.getWorld().setSpawnLocation( spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ() );
		mapSpawns = ( ArrayList<Location> ) config.getLocationList( "Mapspawns" );
		border = UtilWorld.createWorldBorder( new Location( spawn.getWorld(), 0, 90, 0 ), 2050, 10, 10 );
	}

	public boolean removeNearestMapSpawn(Player player, double minDistance) {
		boolean changed = false;
		for ( int i = 0; i < mapSpawns.size(); i++ ) {
			if ( mapSpawns.get( i ).distance( player.getLocation() ) < minDistance ) {
				mapSpawns.remove( i );
				config.setLocationList( "Mapspawns", this.mapSpawns );
				changed = true;
			}
		}
		if ( changed ) {
			getPlugin().saveConfig();
		}
		return changed;
	}

	public void addMapSpawn(Location loc) {
		mapSpawns.add( loc );
		config.setLocationList( "Mapspawns", mapSpawns );
		getPlugin().saveConfig();
	}

	public Location getRandomMapSpawn() {
		if ( mapSpawns.isEmpty() ) {
			return null;
		}
		return mapSpawns.get( UtilMath.randomInteger( mapSpawns.size() ) );
	}

	public void resetBorder(Player player) {
		UtilWorld.resetWorldBoarder( player );
	}

	public void sendBorder(Player player) {
		UtilPlayer.sendPacket( player, border );
	}

	public void setSpawn(Location spawn) {
		this.spawn = spawn;
		config.setLocation( "spawnLocation", spawn );
		getPlugin().saveConfig();
	}

	public void resetLastMapPos(Player plr) {
		getUserConfig( plr ).set( "lastMapPos", null );
	}

	public void saveLastMapPos(Player plr, Location loc) {
		getUserConfig( plr ).setLocation( "lastMapPos", loc );
	}

	public kConfig getUserConfig(Player plr) {
		return getPlugin().getUserDataConfig().getConfig( plr );
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent ev) {
		if ( UtilWorldGuard.RegionFlag( ev.getPlayer(), DefaultFlag.PVP ) ) {
			ev.getPlayer().teleport( spawn );
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent ev) {
		if ( UtilWorldGuard.RegionFlag( ev.getPlayer(), DefaultFlag.PVP ) ) {
			saveLastMapPos( ev.getPlayer(), ev.getPlayer().getLocation() );
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent ev) {
		resetLastMapPos( ev.getEntity() );
	}

	@EventHandler
	public void move(PlayerMoveEvent ev) {
		if ( ev.getPlayer().getEyeLocation().getBlock().getType() == Material.PORTAL ) {
			if ( !UtilWorldGuard.RegionFlag( ev.getPlayer(), DefaultFlag.PVP ) ) {
				sendBorder( ev.getPlayer() );
				if ( getUserConfig( ev.getPlayer() ).contains( "lastMapPos" ) ) {
					ev.getPlayer().teleport( getUserConfig( ev.getPlayer() ).getLocation( "lastMapPos" ) );
				} else {
					if ( !this.mapSpawns.isEmpty() ){
						ev.getPlayer().teleport( getRandomMapSpawn() );
						
						ev.getPlayer().getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
						ev.getPlayer().getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
						ev.getPlayer().getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
						ev.getPlayer().getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
						ev.getPlayer().setItemInHand(Zone.crackshotRename(new ItemStack(Material.STONE_SPADE)));
						ev.getPlayer().getInventory().addItem(new ItemStack(Material.WOOD_SWORD));
						ev.getPlayer().getInventory().addItem(new ItemStack(Material.EMPTY_MAP));
						ev.getPlayer().getInventory().addItem(new ItemStack(351,16,(byte)13));
						ev.getPlayer().getInventory().addItem(new ItemStack(351,16,(byte)3));
						
						this.getModuleManager().getModule( ItemRenameModule.class ).renameItemStackArray(ev.getPlayer().getInventory().getContents());
					}
				}
			}
		}
	}
}
