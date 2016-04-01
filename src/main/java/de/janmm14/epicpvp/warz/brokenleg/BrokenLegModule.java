package de.janmm14.epicpvp.warz.brokenleg;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class BrokenLegModule extends Module<BrokenLegModule> {

	private static final String PATH_PREFIX = "brokenleg";
	private static final String PATH_SLOWNESS_AMPLIFIER = PATH_PREFIX + ".slowness.amplifier";
	private static final String PATH_SLOWNESS_DURATION = PATH_PREFIX + ".slowness.duration_ticks";
	private static final String PATH_REQUIRED_DAMAGE = PATH_PREFIX + ".required_fall_damage_half_hearts";

	public BrokenLegModule(WarZ plugin) {
		super( plugin, FallDamageListener::new, BoneUseListener::new );
	}

	@Override
	public void reloadConfig() {
		getPlugin().getConfig().addDefault( PATH_SLOWNESS_DURATION, 30 * 20 );
		getPlugin().getConfig().addDefault( PATH_SLOWNESS_AMPLIFIER, 1 );
		getPlugin().getConfig().addDefault( PATH_REQUIRED_DAMAGE, 4 );
	}

	public int getDurationTicks() {
		return getPlugin().getConfig().getInt( PATH_SLOWNESS_DURATION );
	}

	public int getSlownessAmplifier() {
		return getPlugin().getConfig().getInt( PATH_SLOWNESS_AMPLIFIER );
	}

	public int getRequiredHalfHeartsDamage() {
		return getPlugin().getConfig().getInt( PATH_REQUIRED_DAMAGE );
	}
}
