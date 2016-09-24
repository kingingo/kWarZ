package de.janmm14.epicpvp.warz.shop;

import javax.annotation.Nullable;

import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import lombok.Getter;

@Module.Priority(1)
public class ShopModule extends Module<ShopModule> {

	@Getter
	private final ShopBuyInventoryHandler buyInventoryHandler = new ShopBuyInventoryHandler();
	private ShopChestDeliveryHandler shopDeliveryHandler;
	@Getter
	@Nullable
	private BlockVector deliveryChestLocation;

	public ShopModule(WarZ plugin) {
		super( plugin, ShopModule::getShopDeliveryHandler );
		getPlugin().getCommand( "shop" ).setExecutor( new CommandShop( this ) );
		getPlugin().getCommand( "giveshopitem" ).setExecutor( new CommandGiveShopItem( this ) );
		getPlugin().getCommand( "setdelivery" ).setExecutor( new CommandSetDelivery( this ) );
		getPlugin().getCommand( "shopchestadmin" ).setExecutor( new CommandShopChestAdmin( this ) );
	}

	@Override
	public void reloadConfig() {
		Vector vector = getConfig().getVector( "shop.delivery.chestLocation" );
		if ( vector != null ) {
			deliveryChestLocation = vector.toBlockVector();
		}
	}

	public ShopChestDeliveryHandler getShopDeliveryHandler() {
		ShopChestDeliveryHandler shopDeliveryHandler = this.shopDeliveryHandler;
		if ( shopDeliveryHandler == null ) {
			this.shopDeliveryHandler = shopDeliveryHandler = new ShopChestDeliveryHandler( this );
		}
		return shopDeliveryHandler;
	}

	public void setDeliveryChestLocation(@Nullable BlockVector deliveryChestLocation) {
		this.deliveryChestLocation = deliveryChestLocation;
		getConfig().set( "shop.delivery.chestLocation", deliveryChestLocation );
		getPlugin().saveConfig();
	}
}
