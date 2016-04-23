package de.janmm14.epicpvp.warz.fishingrod;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class FishingRodModule extends Module<FishingRodModule> {

	public FishingRodModule(WarZ plugin) {
		super( plugin, FishingRodListener::new );
	}

	@Override
	public void reloadConfig() {

	}
}
