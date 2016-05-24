package de.janmm14.epicpvp.warz;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import dev.wolveringer.dataserver.gamestats.GameType;
import eu.epicpvp.kcore.Events.ServerStatusUpdateEvent;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;

public class WarZListener extends kListener{
	
	private JavaPlugin instance;
	
	public WarZListener(JavaPlugin instance) {
		super(instance, "WarZListener");
		this.instance=instance;
	}
	
	@EventHandler
	public void BlockBurn(BlockBurnEvent ev){
		ev.setCancelled(true);
	}
	
	@EventHandler
	public void Explosion(ExplosionPrimeEvent ev){
		ev.setCancelled(true);
	}
	
	@EventHandler
	public void soilChangeEntity(EntityInteractEvent event){
	    if ((event.getEntityType() != EntityType.PLAYER) && (event.getBlock().getType() == Material.SOIL)) event.setCancelled(true);
	}
	
	@EventHandler
	public void Death(PlayerDeathEvent ev){
		ev.setDeathMessage(null);
		if(ev.getEntity() instanceof Player){
			UtilPlayer.RespawnNow(((Player)ev.getEntity()), instance );
		}
	}
	
	@EventHandler
	public void join(PlayerJoinEvent ev){
		ev.setJoinMessage(null);
		UtilPlayer.setTab(ev.getPlayer(), "WarZ");
	}
	
	@EventHandler
	public void quit(PlayerQuitEvent ev){
		ev.setQuitMessage(null);
	}
	
	@EventHandler
	public void update(ServerStatusUpdateEvent ev){
		ev.getPacket().setPlayers(UtilServer.getPlayers().size());
		ev.getPacket().setTyp(GameType.WARZ);
	}
	
	@EventHandler
	public void Sign(SignChangeEvent ev){
		if(ev.getPlayer().hasPermission(PermissionType.CHAT_FARBIG.getPermissionToString())){
			ev.setLine(0, ev.getLine(0).replaceAll("&", "§"));
			ev.setLine(1, ev.getLine(1).replaceAll("&", "§"));
			ev.setLine(2, ev.getLine(2).replaceAll("&", "§"));
			ev.setLine(3, ev.getLine(3).replaceAll("&", "§"));
		}
	}
	
	@EventHandler
	public void Command(PlayerCommandPreprocessEvent ev){
		String cmd = "";
	    if (ev.getMessage().contains(" ")){
	      String[] parts = ev.getMessage().split(" ");
	      cmd = parts[0];
	    }else{
	      cmd = ev.getMessage();
	    }
	     
	    if(cmd.startsWith("/me") || cmd.startsWith("/bukkit")){
			ev.setCancelled(true);
		}
	}

}
