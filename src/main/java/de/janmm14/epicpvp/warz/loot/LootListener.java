package de.janmm14.epicpvp.warz.loot;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import com.shampaggon.crackshot.events.WeaponExplodeEvent;

import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.logout.LogoutPlayerQuitEvent;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Util.UtilString;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class LootListener extends kListener{

	private LootModule module;
	private final BaseComponent[] USE_LOOT_MESSAGE = new ComponentBuilder("").append("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n\n").color(ChatColor.GRAY).bold(true).append(UtilString.center("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n\n", "Möchtest du deine Loot Time in Anspruch nehmen?\n")+"Möchtest du deine Loot Time in Anspruch nehmen?\n").color(ChatColor.YELLOW).bold(false).append(UtilString.center("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n\n", "KLICK MICH\n\n")+"KLICK MICH\n\n").color(ChatColor.GREEN).bold(false).event(new ClickEvent(Action.RUN_COMMAND, "/loot use")).append("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").color(ChatColor.GRAY).bold(true).create();
	
	public LootListener(LootModule module) {
		super(module.getPlugin(), "LootListener");
		this.module=module;
	}
	
	@EventHandler
	public void target(EntityTargetEvent ev){
		if(ev.getTarget() instanceof Player){
			if(module.getLoottimer().containsKey( ((Player)ev.getTarget()) )){
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void edamage(EntityDamageByEntityEvent ev){
		if(ev.getEntity() instanceof Player){
			if(module.getLoottimer().containsKey( ((Player)ev.getEntity()) )){
				ev.setCancelled(true);
			}
		}else if(ev.getDamager() instanceof Player){
			if(module.getLoottimer().containsKey( ((Player)ev.getDamager()) )){
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void damage(WeaponDamageEntityEvent ev){
		if(module.getLoottimer().containsKey( ev.getPlayer() )){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void damage(EntityDamageEvent ev){
		if(ev.getEntity() instanceof Player){
			if(module.getLoottimer().containsKey( ((Player)ev.getEntity()) )){
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void pickup(PlayerPickupItemEvent ev){
		if(module.getLoottimer().containsKey(ev.getPlayer())){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void drop(PlayerDropItemEvent ev){
		if(module.getLoottimer().containsKey(ev.getPlayer())){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void logoutquit(LogoutPlayerQuitEvent ev){
		if(module.getLoottimer().containsKey(ev.getPlayer())){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void quit(PlayerQuitEvent ev){
		module.stopLootTime(ev.getPlayer());
	}
	
	@EventHandler
	public void respawn(PlayerRespawnEvent event) {
		if(module.canUseLoot(event.getPlayer())){
			event.getPlayer().spigot().sendMessage(USE_LOOT_MESSAGE);
			module.getCachedlist().update();
			module.getCachedlist().add(event.getPlayer());
		}
	}

}
