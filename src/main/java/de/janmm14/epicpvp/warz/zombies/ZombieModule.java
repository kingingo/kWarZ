package de.janmm14.epicpvp.warz.zombies;

import org.bukkit.entity.Zombie;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class ZombieModule extends Module<ZombieModule> {

	private static final String PATH_PREFIX = "zombies.";

	private static final String PATH_REDSTONE_TORCH_RANGE = PATH_PREFIX + "redstone_torch_range";

	public ZombieModule(WarZ plugin) {
		super( plugin, ZombieBehaviourListener::new, ZombieSpawnListener::new, ZombieAttackListener::new );
		getPlugin().getConfig().addDefault( PATH_REDSTONE_TORCH_RANGE, 15 );
	}

	public void setupZombie(Zombie zombie) {
		zombie.setFireTicks( 0 );
		//TODO setup zombie further
	}

	@Override
	public void reloadConfig() {
	}
}
