package de.janmm14.epicpvp.warz.shop;

import javax.annotation.Nullable;

import org.bukkit.util.BlockVector;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.itemrename.ItemRenameModule;

import lombok.Getter;
import lombok.Setter;

@Module.Priority(1)
public class ShopModule extends Module<ShopModule> {

	@Getter
	private final ShopBuyInventoryHandler buyInventoryHandler = new ShopBuyInventoryHandler( getModuleManager().getModule( ItemRenameModule.class ) );
	private ShopChestDeliveryHandler shopDeliveryHandler;
	@Getter
	@Setter
	@Nullable
	private BlockVector deliveryChestLocation;

	public ShopModule(WarZ plugin) {
		super( plugin, ShopModule::getShopDeliveryHandler );
		getPlugin().getCommand( "shop" ).setExecutor( new CommandShop( this ) );
		getPlugin().getCommand( "giveshopitem" ).setExecutor( new CommandGiveShopItem( this ) );
		getPlugin().getCommand( "setdelivery" ).setExecutor( new CommandSetDelivery( this ) );
	}

	@Override
	public void reloadConfig() {
		deliveryChestLocation = getConfig().getVector( "shop.delivery.chestLocation" ).toBlockVector();
	}

	public ShopChestDeliveryHandler getShopDeliveryHandler() {
		ShopChestDeliveryHandler shopDeliveryHandler = this.shopDeliveryHandler;
		if (shopDeliveryHandler == null) {
			this.shopDeliveryHandler = shopDeliveryHandler = new ShopChestDeliveryHandler( this );
		}
		return shopDeliveryHandler;
	}
}
