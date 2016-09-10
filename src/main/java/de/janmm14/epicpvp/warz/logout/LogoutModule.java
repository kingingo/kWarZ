package de.janmm14.epicpvp.warz.logout;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.protection.flags.DefaultFlag;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.TimeSpan;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilWorldGuard;
import eu.epicpvp.kcore.kConfig.kConfig;
import lombok.Getter;

public class LogoutModule extends Module<LogoutModule> implements Listener {
	@Getter
	private HashMap<Integer, NPC> npcs = new HashMap<>();
	
	public LogoutModule(WarZ plugin) {
		super( plugin, module -> module );
	}
	
	@EventHandler
	public void kill(EntityDeathEvent ev){
		if(this.npcs.containsKey(ev.getEntity().getEntityId())){
			NPC npc = this.npcs.get(ev.getEntity().getEntityId());
			npc.drop();
			
			Player player = UtilPlayer.loadPlayer(npc.getPlayername());
			player.getInventory().clear();
			player.getInventory().setArmorContents(new ItemStack[]{});
			
			kConfig config = UtilServer.getUserData().loadConfig(npc.getPlayerId());
			config.set( "lastMapPos", null );
			config.save();
			
			npcs.remove(ev.getEntity().getEntityId());
		}
	}
	
	@EventHandler
	public void quit(PlayerQuitEvent ev){
		if(UtilWorldGuard.RegionFlag(ev.getPlayer(), DefaultFlag.PVP)){
			new NPC(ev.getPlayer(),this);
		}
	}
	
	@EventHandler
	public void time(UpdateEvent ev){
		if(ev.getType()==UpdateType.FAST){
			NPC npc;
			for(int i = 0; i<npcs.size(); i++){
				npc=(NPC)npcs.values().toArray()[i];
				
				if((System.currentTimeMillis() - npc.getTime()) > TimeSpan.SECOND*25){
					npc.remove();
					npcs.remove(npc.getEntityId());
				}
			}
		}
	}

	@Override
	public void reloadConfig() {
		
	}

}
