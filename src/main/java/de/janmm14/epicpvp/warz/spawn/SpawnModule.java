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

import com.sk89q.worldguard.protection.flags.DefaultFlag;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilWorldGuard;
import eu.epicpvp.kcore.kConfig.kConfig;
import lombok.Getter;

public class SpawnModule extends Module<SpawnModule> implements Listener{

	@Getter
	private Location spawn;
	@Getter
	private ArrayList<Location> map_spawns;
	private ConfigLocation config;

	public SpawnModule(WarZ plugin) {
		super( plugin, module -> module );
		plugin.getCommand( "spawn" ).setExecutor( new CommandSpawn( this ) );
		config=new ConfigLocation(getConfig());
		spawn = config.getLocation("spawnLocation");
		spawn.getWorld().setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
		
		this.map_spawns=new ArrayList<>();
		loadMapSpawns();
	}
	
	public boolean removeNearestMapSpawn(Player player, double minDistance){
		for(int i = 0; i<map_spawns.size(); i++)
			if(map_spawns.get(i).distance(player.getLocation()) < minDistance){
				map_spawns.remove(i);
				config.setLocationList("Mapspawns", this.map_spawns);
				return true;
			}
		return false;
	}
	
	public void addMapSpawn(Location loc){
		this.map_spawns.add(loc);
		config.setLocationList("Mapspawns", this.map_spawns);
	}
	
	public void loadMapSpawns(){
		this.map_spawns = (ArrayList<Location>) config.getLocationList("Mapspawns");
	}

	@Override
	public void reloadConfig() {
		spawn = config.getLocation("spawnLocation");
	}

	public void setSpawn(Location spawn) {
		this.spawn = spawn;
		config.setLocation("spawnLocation", spawn);
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
	public void join(PlayerJoinEvent ev){
		if(UtilWorldGuard.RegionFlag(ev.getPlayer(), DefaultFlag.PVP)){
			ev.getPlayer().teleport(spawn);
		}
	}
	
	@EventHandler
	public void quit(PlayerQuitEvent ev){
		if(UtilWorldGuard.RegionFlag(ev.getPlayer(), DefaultFlag.PVP)){
			saveLastMapPos(ev.getPlayer(), ev.getPlayer().getLocation());
		}
	}
	
	@EventHandler
	public void death(PlayerDeathEvent ev){
		resetLastMapPos(ev.getEntity());
	}
	
	@EventHandler
	public void move(PlayerMoveEvent ev){
		if(ev.getPlayer().getEyeLocation().getBlock().getType()==Material.PORTAL){
			if(!UtilWorldGuard.RegionFlag(ev.getPlayer(), DefaultFlag.PVP)){
				if(getUserConfig( ev.getPlayer() ).contains("lastMapPos")){
					ev.getPlayer().teleport(getUserConfig(ev.getPlayer()).getLocation("lastMapPos"));
				}else{
					if(!this.map_spawns.isEmpty())
						ev.getPlayer().teleport(this.map_spawns.get( UtilMath.randomInteger(this.map_spawns.size()) ));
				}
			}
		}
	}
}
