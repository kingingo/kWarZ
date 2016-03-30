package de.janmm14.epicpvp.warz.cobwebs;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class CobWebModule extends Module<CobWebModule> {

	public CobWebModule(WarZ plugin) {
		super( plugin, CobWebDigListener::new, CobWebShearsListener::new );
	}
}
