package de.janmm14.epicpvp.warz.spawn;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.sk89q.worldguard.protection.flags.DefaultFlag;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.itemrename.ItemRenameModule;
import de.janmm14.epicpvp.warz.logout.LogoutModule;
import de.janmm14.epicpvp.warz.util.ConfigLocationAdapter;
import de.janmm14.epicpvp.warz.zonechest.Zone;
import eu.epicpvp.kcore.PacketAPI.Packets.WrapperPacketPlayOutWorldBorder;
import eu.epicpvp.kcore.UserDataConfig.Events.UserDataConfigLoadEvent;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilWorld;
import eu.epicpvp.kcore.Util.UtilWorldGuard;
import eu.epicpvp.kcore.kConfig.kConfig;
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
		border = UtilWorld.createWorldBorder( new Location( spawn.getWorld(), 0, 90, 0 ), 2048, 10, 10 );
	}

	public void onDisable() {

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
	
	public void teleport(Player plr, Location loc){
		if(!plr.teleport(loc)){
			if(plr.getPassenger()!=null){
				Entity e = plr.getPassenger();
				plr.eject();
				plr.teleport(loc);		
				Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable() {
					@Override
					public void run() {
						e.teleport(plr);
						plr.setPassenger(e);
					}
				}, 10L);
			}
		}
	}

	public void teleportWarz(Player plr) {
		if ( getUserConfig( plr ).contains( "lastMapPos" ) ) {
			Location loc = getUserConfig( plr ).getLocation( "lastMapPos" );
			if ( UtilWorldGuard.RegionFlag( loc, DefaultFlag.PVP ) ) {
				teleport( plr , loc );
				return;
			}
		}
		if ( !this.mapSpawns.isEmpty() ) {
			teleport( plr , getRandomMapSpawn() );
		}
	}

	public void setStarterKit(Player plr) {
		PlayerInventory inventory = plr.getInventory();
		if ( isEmpty( inventory.getHelmet() ) ) {
			inventory.setHelmet( new ItemStack( Material.LEATHER_HELMET ) );
		} else {
			inventory.addItem( new ItemStack( Material.LEATHER_HELMET ) );
		}
		if ( isEmpty( inventory.getChestplate() ) ) {
			inventory.setChestplate( new ItemStack( Material.LEATHER_CHESTPLATE ) );
		} else {
			inventory.addItem( new ItemStack( Material.LEATHER_CHESTPLATE ) );
		}
		if ( isEmpty( inventory.getLeggings() ) ) {
			inventory.setLeggings( new ItemStack( Material.LEATHER_LEGGINGS ) );
		} else {
			inventory.addItem( new ItemStack( Material.LEATHER_LEGGINGS ) );
		}
		if ( isEmpty( inventory.getBoots() ) ) {
			inventory.setBoots( new ItemStack( Material.LEATHER_BOOTS ) );
		} else {
			inventory.addItem( new ItemStack( Material.LEATHER_BOOTS ) );
		}
		inventory.addItem( Zone.crackshotRename( new ItemStack( Material.STONE_SPADE ) ) );
		inventory.addItem( new ItemStack( Material.WOOD_SWORD ) );
		inventory.addItem( new ItemStack( Material.EMPTY_MAP ) );
		inventory.addItem( new ItemStack( 351, 16, ( short ) 13 ) );
		inventory.addItem( new ItemStack( 351, 16, ( short ) 6 ) );
		inventory.addItem( new ItemStack( 351, 16, ( short ) 3 ) );
		plr.setExp( 1 );
		plr.setFoodLevel( 20 );
		plr.setSaturation( Float.MAX_VALUE );

		this.getModuleManager().getModule( ItemRenameModule.class ).renameItemStackArray( inventory.getContents() );
		plr.updateInventory();
	}

	private boolean isEmpty(ItemStack is) {
		return is == null || is.getType() == Material.AIR;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if ( UtilWorldGuard.RegionFlag( event.getPlayer(), DefaultFlag.PVP ) && !getModuleManager().getModule( LogoutModule.class ).containsNpc( event.getPlayer() ) ) {
			saveLastMapPos( event.getPlayer(), event.getPlayer().getLocation() );
			event.getPlayer().teleport( spawn );
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if ( UtilWorldGuard.RegionFlag( event.getPlayer(), DefaultFlag.PVP ) ) {
			saveLastMapPos( event.getPlayer(), event.getPlayer().getLocation() );
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void teleport(PlayerTeleportEvent event) {
		Player plr = event.getPlayer();
		Location to = event.getTo();
		if ( UtilWorldGuard.RegionFlag( to, DefaultFlag.PVP ) ) {
			sendBorder( plr );
		} else {
			Location from = event.getFrom();
			if ( UtilWorldGuard.RegionFlag( from, DefaultFlag.PVP ) ) {
				saveLastMapPos( plr, from );
			}
			resetBorder( plr );
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void newPlayer(UserDataConfigLoadEvent event) {
		if ( event.isNewConfig() ) {
			if ( WarZ.DEBUG ) System.out.println( "NEW PLAYER " + event.getPlayer().getName() );
			setStarterKit( event.getPlayer() );
		}
	}

	@EventHandler
	public void respawn(PlayerRespawnEvent event) {
		setStarterKit( event.getPlayer() );
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Entity vehicle = event.getEntity().getVehicle();
		if(vehicle!=null){
			event.getEntity().leaveVehicle();
			vehicle.remove();
		}
		resetLastMapPos( event.getEntity() );
	}

	@EventHandler
	public void move(PlayerMoveEvent event) {
		if ( event.getPlayer().getEyeLocation().getBlock().getType() == Material.PORTAL ) {
			if ( !UtilWorldGuard.RegionFlag( event.getPlayer(), DefaultFlag.PVP ) ) {
				teleportWarz( event.getPlayer() );
			}
		}
	}
}
