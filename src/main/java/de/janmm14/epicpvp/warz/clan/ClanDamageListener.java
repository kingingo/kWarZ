package de.janmm14.epicpvp.warz.clan;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;

import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Util.UtilServer;

public class ClanDamageListener extends kListener{

	private ClanModule module;
	
	public ClanDamageListener(ClanModule module) {
		super(UtilServer.getPluginInstance(), "ClanDamageListener");
		this.module=module;
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent event) {
		if ( !( event.getEntity() instanceof Player ) || !( event.getDamager() instanceof Player ) ) {
			return;
		}
		
		Player victim = (Player)event.getEntity();
		Player damager = (Player)event.getDamager();
		
		if(module.getHandler().areGildeFriends(damager, victim)){
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true) //highest is for CrackShotTweakModule
	public void onWeaponDamage(WeaponDamageEntityEvent event) {
		if ( event.getVictim() instanceof Player ) {
			if(module.getHandler().areGildeFriends(event.getPlayer(), ((Player)event.getVictim()))){
				event.setCancelled(true);
			}
		}
	}
}
