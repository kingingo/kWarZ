package de.janmm14.epicpvp.warz.zombies;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.util.random.RandomThingHolder;

public class ZombieModule extends Module<ZombieModule> {

	private static final String PATH_PREFIX = "zombies.";

	private static final String PATH_REDSTONE_TORCH_RANGE = PATH_PREFIX + "redstone_torch_range";
	private static final String PATH_RANDOM_ATTACK_CONFIGURATION = PATH_PREFIX + "random_attacks"; //no dot at the end (!)
	private static final String PATH_ATTACK_DAMAGE = PATH_PREFIX + "attack_damage";

	private final List<ZombieAttackInfo> randomEffectHolderList = new ArrayList<>();

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
			randomEffectHolderList.add( ZombieAttackInfo.fromConfigurationSection( subSection ) );
		}
	}

	public PotionEffect getRandomAttackEffect() {

		return RandomThingHolder.chooseRandomItem( randomEffectHolderList );
	}

	public double getZombieDamage() {
		return getPlugin().getConfig().getDouble( PATH_ATTACK_DAMAGE );
	}
}
