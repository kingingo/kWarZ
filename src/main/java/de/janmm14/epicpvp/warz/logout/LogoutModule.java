package de.janmm14.epicpvp.warz.logout;

import java.util.HashMap;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.UserDataConfig.Events.UserDataConfigLoadEvent;
import eu.epicpvp.kcore.Util.TimeSpan;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilWorldGuard;
import eu.epicpvp.kcore.kConfig.kConfig;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

import lombok.Getter;

public class LogoutModule extends Module<LogoutModule> implements Listener {

	@Getter
	private HashMap<Integer, NPC> npcs = new HashMap<>();
	@Getter
	private HashMap<Integer, NPC> npcs_playerId = new HashMap<>();

	public LogoutModule(WarZ plugin) {
		super( plugin, module -> module );
	}

	public boolean containsNpc(Player player){
		if(npcs_playerId.containsKey(UtilPlayer.getPlayerId(player))){
			NPC npc = npcs_playerId.get(UtilPlayer.getPlayerId(player));
			
			npc.remove();
			if(WarZ.DEBUG)System.out.println("Player joined and replaced the NPC");
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void kill(EntityDeathEvent ev) {
		if ( this.npcs.containsKey( ev.getEntity().getEntityId() ) ) {
			NPC npc = this.npcs.get( ev.getEntity().getEntityId() );
			npc.drop();

			kConfig config = UtilServer.getUserData().loadConfig( npc.getPlayerId() );
			config.set( "lastMapPos", null );
			config.set( "Death", true );
			config.save();
		}
	}

	@EventHandler
	public void load(UserDataConfigLoadEvent ev) {
		if ( ev.getConfig().contains( "Death" ) ) {
			if ( ev.getConfig().getBoolean( "Death" ) ) {
				ev.getPlayer().getInventory().clear();
				ev.getPlayer().getInventory().setArmorContents( new ItemStack[]{} );
				ev.getConfig().set( "Death", null );
				
				ev.getConfig().save();
			}
		}
	}

	@EventHandler
	public void quit(PlayerQuitEvent ev) {
		if ( UtilWorldGuard.RegionFlag( ev.getPlayer(), DefaultFlag.PVP ) ) {
			new NPC( ev.getPlayer(), this );
			
			if(WarZ.DEBUG)System.err.println("Player logout out "+ev.getPlayer().getName());
		}
	}

	@EventHandler
	public void time(UpdateEvent ev) {
		if ( ev.getType() == UpdateType.FAST ) {
			NPC npc;
			for ( int i = 0; i < npcs.size(); i++ ) {
				npc = ( NPC ) npcs.values().toArray()[ i ];

				if (( System.currentTimeMillis() - npc.getTime() ) > TimeSpan.SECOND * 25 ) {
					if(WarZ.DEBUG)System.err.println("NPC Time is over "+npc.getPlayername()+" "+(( System.currentTimeMillis() - npc.getTime() ) > TimeSpan.SECOND * 25));
					npc.remove();
				}
			}
		}
	}

	@EventHandler
	public void loadWorld(WorldLoadEvent ev) {
		for ( Entity e : ev.getWorld().getEntities() ) {
			if ( e instanceof Skeleton ) {
				e.remove();
			}
		}
	}

	@Override
	public void reloadConfig() {

	}
}
