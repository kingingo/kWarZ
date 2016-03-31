package de.janmm14.epicpvp.warz.crackshot;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class CrackShotTweakModule extends Module<CrackShotTweakModule> {

	public CrackShotTweakModule(WarZ plugin) {
		super( plugin, FixWeaponDamageListener::new );
	}
}
