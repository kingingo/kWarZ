package de.janmm14.epicpvp.warz.spawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginAwareness.Flags;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;

import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilWorldGuard;
import eu.epicpvp.kcore.kConfig.kConfig;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

import lombok.Getter;

public class SpawnModule extends Module<SpawnModule> implements Listener{

	@Getter
	private Location spawn;
	@Getter
	private ArrayList<Location> map_spawns;

	public SpawnModule(WarZ plugin) {
		super( plugin, module -> module );
		plugin.getCommand( "spawn" ).setExecutor( new CommandSpawn( this ) );
		this.map_spawns=new ArrayList<>();
		loadMapSpawns();
	}
	
	public boolean removeNearestMapSpawn(Player player, double minDistance){
		for(int i = 0; i<map_spawns.size(); i++)
			if(map_spawns.get(i).distance(player.getLocation()) < minDistance){
				map_spawns.remove(i);
				getConfig().set("Mapspawns", map_spawns);
				return true;
			}
		return false;
	}
	
	public void removeMapSpawn(Location loc){
		this.map_spawns.remove(loc);
		getConfig().set("Mapspawns", map_spawns);
	}
	
	public void addMapSpawn(Location loc){
		this.map_spawns.add(loc);
		getConfig().set("Mapspawns", map_spawns);
	}
	
	public void loadMapSpawns(){
		this.map_spawns=new ArrayList<>();
		List<String> list = getConfig().getStringList("Mapspawns");
		for(String path : list)this.map_spawns.add(((Location)getConfig().get(path)));
	}

	@Override
	public void reloadConfig() {
		spawn = ( Location ) getConfig().get( "spawnLocation" );
	}

	public void setSpawn(Location spawn) {
		this.spawn = spawn;
		getConfig().set( "spawnLocation", spawn );
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
		if(!UtilWorldGuard.RegionFlag(ev.getPlayer(), DefaultFlag.PVP)){
			ev.getPlayer().teleport(spawn);
		}
	}
	
	@EventHandler
	public void quit(PlayerQuitEvent ev){
		if(!UtilWorldGuard.RegionFlag(ev.getPlayer(), DefaultFlag.PVP)){
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
			if(UtilWorldGuard.RegionFlag(ev.getPlayer(), DefaultFlag.PVP)){
				if(getUserConfig( ev.getPlayer() ).contains("lastMapPos")){
					ev.getPlayer().teleport(getUserConfig(ev.getPlayer()).getLocation("lastMapPos"));
				}else{
					if(!this.map_spawns.isEmpty())
						ev.getPlayer().teleport(this.map_spawns.get(UtilMath.randomInteger(this.map_spawns.size())));
				}
			}
		}
	}
}
