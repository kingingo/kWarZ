package de.janmm14.epicpvp.warz.crackshot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FixWeaponDamageListener implements Listener {

	private final CrackShotTweakModule module;

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onWeaponDamage(WeaponDamageEntityEvent event) {
		event.getPlayer().setHealth( event.getPlayer().getHealth() - event.getDamage() );
		event.setDamage( 0.00001 );
	}
}
