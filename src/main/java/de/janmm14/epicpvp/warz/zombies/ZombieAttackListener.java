package de.janmm14.epicpvp.warz.zombies;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ZombieAttackListener implements Listener {

	private final ZombieModule module;

	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event) {
		if ( event.getDamager() instanceof Zombie && event.getEntityType() == EntityType.PLAYER ) {
			PotionEffect randomAttackEffect = module.getRandomAttackEffect();
			if ( randomAttackEffect != null ) {
				( ( LivingEntity ) event.getEntity() ).addPotionEffect( randomAttackEffect );
			}
			double dmg = module.getZombieDamage();
			int dmgInt = ( int ) ( dmg * 2.0 );
			if ( dmgInt >= 0 ) {
				event.setDamage( dmgInt / 2 );
			}
		}
	}
}
