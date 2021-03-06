package de.janmm14.epicpvp.warz.friends;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;

import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.hooks.UserDataConverter;

public class FriendHurtListener implements Listener {

	private final UserDataConverter userDataConverter;
	private final FriendModule module;

	public FriendHurtListener(FriendModule module) {
		this.module = module;
		this.userDataConverter = module.getPlugin().getUserDataConverter();
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent event) {
		if ( !( event.getEntity() instanceof Player ) || !( event.getDamager() instanceof Player ) ) {
			return;
		}
		if ( WarZ.DEBUG ) {
			System.out.println( "friend on dmg " + event.getEntity() );
		}
		FriendInfoManager manager = module.getFriendInfoManager();
		FriendInfo victimInfo = manager.get( event.getEntity().getUniqueId() );
		UserDataConverter.Profile damagerProfile = userDataConverter.getProfile( event.getDamager().getUniqueId() );
		if ( PlayerFriendRelation.areFriends( manager, victimInfo, damagerProfile.getPlayerId() ) ) {
			event.setCancelled( true );
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true) //highest is for CrackShotTweakModule
	public void onWeaponDamage(WeaponDamageEntityEvent event) {
		if ( event.getVictim() instanceof Player ) {
			UUID victimUuid = event.getVictim().getUniqueId();
			UUID damagerUuid = event.getPlayer().getUniqueId();
			UserDataConverter.Profile damagerProfile = userDataConverter.getProfile( damagerUuid );
			FriendInfoManager manager = module.getFriendInfoManager();
			if ( PlayerFriendRelation.areFriends( manager, manager.get( victimUuid ), damagerProfile.getPlayerId() ) ) {
				event.setCancelled( true );
			}
		}
	}
}
