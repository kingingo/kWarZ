package de.janmm14.epicpvp.warz.zombies;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class ZombieModule extends Module<ZombieModule> {

	private static final String PATH_PREFIX = "zombies.";

	private static final String PATH_REDSTONE_TORCH_RANGE = PATH_PREFIX + "redstone_torch_range";
	private static final String PATH_RANDOM_ATTACK_CONFIGURATION = PATH_PREFIX + "random_attacks"; //no dot at the end (!)
	private static final String PATH_ATTACK_DAMAGE = PATH_PREFIX + "attack_damage";

	private final List<ZombieAttackInfo> randomEffects = new ArrayList<>();
	private final Random random = new Random();

	public ZombieModule(WarZ plugin) {
		super( plugin, ZombieBehaviourListener::new, ZombieSpawnListener::new, ZombieAttackListener::new );
		getPlugin().getConfig().addDefault( PATH_REDSTONE_TORCH_RANGE, 15 );
	}

	public void setupZombie(Zombie zombie) {
		zombie.setFireTicks( 0 );
		//TODO setup zombie further - behaviour
	}

	@Override
	public void reloadConfig() {
		getPlugin().getConfig().addDefault( PATH_ATTACK_DAMAGE, -1.0 );
		getPlugin().getConfig().addDefault( PATH_RANDOM_ATTACK_CONFIGURATION + ".irgendwashierhinaberimmerunterschiedlich.effect", "CONFUSION" );
		getPlugin().getConfig().addDefault( PATH_RANDOM_ATTACK_CONFIGURATION + ".irgendwashierhinaberimmerunterschiedlich.duration_in_half_ticks", 40 );
		getPlugin().getConfig().addDefault( PATH_RANDOM_ATTACK_CONFIGURATION + ".irgendwashierhinaberimmerunterschiedlich.amplifier", 1 );
		getPlugin().getConfig().addDefault( PATH_RANDOM_ATTACK_CONFIGURATION + ".irgendwashierhinaberimmerunterschiedlich.probability", 0.1 );
		ConfigurationSection section = getPlugin().getConfig().getConfigurationSection( PATH_RANDOM_ATTACK_CONFIGURATION );
		for ( String key : section.getKeys( false ) ) {
			if ( key.equalsIgnoreCase( "irgendwashierhinaberimmerunterschiedlich" ) ) {
				continue;
			}
			ConfigurationSection subSection = section.getConfigurationSection( key );
			randomEffects.add( ZombieAttackInfo.fromConfigurationSection( subSection ) );
		}
	}

	public PotionEffect getRandomAttackEffect() {
		double rdm = random.nextDouble(); //between 0 and 1

		//fill up probabilities from 0 to 1
		double overallProbability = 0;
		for ( ZombieAttackInfo randomEffect : randomEffects ) {
			double startingProbability = overallProbability + randomEffect.getProbability();
			overallProbability += randomEffect.getProbability();

			if ( rdm > startingProbability && rdm <= ( startingProbability + randomEffect.getProbability() ) ) {
				return randomEffect.getPotionEffect();
			}
		}
		return null;
	}

	public double getZombieDamage() {
		return getPlugin().getConfig().getDouble( PATH_ATTACK_DAMAGE );
	}
}
