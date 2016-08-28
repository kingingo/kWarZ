package de.janmm14.epicpvp.warz.shop;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

@Module.Priority(1)
public class ShopModule extends Module<ShopModule> {

	public ShopModule(WarZ plugin) {
		super( plugin, ShopModuleListener::new );
		getPlugin().getCommand( "shop" ).setExecutor( new CommandShop( this ) );
	}

	@Override
	public void reloadConfig() {

	}
}
