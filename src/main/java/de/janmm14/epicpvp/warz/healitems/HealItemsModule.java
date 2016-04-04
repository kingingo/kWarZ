package de.janmm14.epicpvp.warz.healitems;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class HealItemsModule extends Module<HealItemsModule> {

	private static final String PATH_PREFIX = "healitems.";
	static final String FOOD_CFG_PREFIX = PATH_PREFIX + "cfg"; //no dot at the end (!)
	private final Table<Material, Byte, HealItemConfig> itemHealTable = HashBasedTable.create( 1, 4 );

	public HealItemsModule(WarZ plugin) {
		super( plugin, HealUseListener::new );
	}

	@Override
	public void reloadConfig() {
		itemHealTable.clear();

		//TODO add cofnig defaults

		ConfigurationSection section = getPlugin().getConfig().getConfigurationSection( FOOD_CFG_PREFIX );
		for ( String key : section.getKeys( false ) ) {
			String matStr;
			byte data;
			if ( key.contains( ":" ) ) {
				String[] split = key.split( ":" );
				matStr = split[ 0 ];
				data = Byte.parseByte( split[ 1 ] );
			} else {
				matStr = key;
				data = 0;
			}
			Material mat = Material.matchMaterial( matStr );
			itemHealTable.put( mat, data, HealItemConfig.byConfigurationSection( section.getConfigurationSection( key ) ) );
		}
	}

	@SuppressWarnings("deprecation")
	public HealItemConfig getItemConfig(ItemStack is) {
		return itemHealTable.get( is.getType(), is.getData().getData() );
	}
}
