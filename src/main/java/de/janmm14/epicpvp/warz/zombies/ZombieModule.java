package de.janmm14.epicpvp.warz.zombies;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.util.random.RandomThingHolder;
import de.janmm14.epicpvp.warz.util.random.RandomUtil;

public class ZombieModule extends Module<ZombieModule> {

	private static final String PATH_PREFIX = "zombies.";

	private static final String PATH_REDSTONE_TORCH_RANGE = PATH_PREFIX + "redstone_torch_range";
	private static final String PATH_RANDOM_ATTACK_CONFIGURATION = PATH_PREFIX + "random_attacks"; //no dot at the end (!)
	private static final String PATH_ATTACK_DAMAGE = PATH_PREFIX + "attack_damage";

	private final List<ZombieAttackInfo> randomEffectHolderList = new ArrayList<>();
	private double zombieDamage;

	public ZombieModule(WarZ plugin) {
		super( plugin, ZombieBehaviourListener::new, ZombieSpawnListener::new, ZombieAttackListener::new );
		getConfig().addDefault( PATH_REDSTONE_TORCH_RANGE, 15 );
	}

	public void setupZombie(Zombie zombie) {
		zombie.setFireTicks( 0 );
		zombie.setBaby( false );
		zombie.setHealth( 10 );
		zombie.setVillager( false );
		if ( RandomUtil.getRandomInt( 1, 15 ) == 1 ) {
			zombie.addPotionEffect( new PotionEffect( PotionEffectType.SPEED, RandomUtil.getRandomInt( 20 * 20, 60 * 20 ), 1, true ) );
		}
		//TODO setup zombie further - behaviour
	}

	@Override
	public void reloadConfig() {
		getConfig().addDefault( PATH_ATTACK_DAMAGE, 2.0 );
		zombieDamage = getConfig().getDouble( PATH_ATTACK_DAMAGE );
		getConfig().addDefault( PATH_RANDOM_ATTACK_CONFIGURATION + ".irgendwashierhinaberimmerunterschiedlich.effect", "CONFUSION" );
		getConfig().addDefault( PATH_RANDOM_ATTACK_CONFIGURATION + ".irgendwashierhinaberimmerunterschiedlich.duration_in_half_ticks", 40 );
		getConfig().addDefault( PATH_RANDOM_ATTACK_CONFIGURATION + ".irgendwashierhinaberimmerunterschiedlich.amplifier", 1 );
		getConfig().addDefault( PATH_RANDOM_ATTACK_CONFIGURATION + ".irgendwashierhinaberimmerunterschiedlich.probability", 0.1 );
		ConfigurationSection section = getConfig().getConfigurationSection( PATH_RANDOM_ATTACK_CONFIGURATION );
		for ( String key : section.getKeys( false ) ) {
			if ( key.equalsIgnoreCase( "irgendwashierhinaberimmerunterschiedlich" ) ) {
				continue;
			}
			ConfigurationSection subSection = section.getConfigurationSection( key );
			randomEffectHolderList.add( ZombieAttackInfo.fromConfigurationSection( subSection ) );
		}
	}

	public PotionEffect getRandomAttackEffect() {

		return RandomThingHolder.chooseRandomItem( randomEffectHolderList );
	}

	public double getZombieDamage() {
		return zombieDamage;
	}
}
