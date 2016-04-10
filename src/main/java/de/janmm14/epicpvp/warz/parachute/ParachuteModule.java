package de.janmm14.epicpvp.warz.parachute;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class ParachuteModule extends Module<ParachuteModule> {

	private static final String PATH_PREFIX = "parachute.";
	private static final String PATH_MAX_TIME_TICKS = PATH_PREFIX + "max_time_ticks";

	public ParachuteModule(WarZ plugin) {
		super( plugin, ParachuteListener::new );
	}

	@Override
	public void reloadConfig() {
		getPlugin().getConfig().addDefault( PATH_MAX_TIME_TICKS, 30 * 20 );
	}

	public int getMaxTimeTicks() {
		return getPlugin().getConfig().getInt( PATH_MAX_TIME_TICKS );
	}
}
