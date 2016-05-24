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
	private final Table<Material, Byte, HealItemValues> itemHealTable = HashBasedTable.create( 1, 4 );

	public HealItemsModule(WarZ plugin) {
		super( plugin, HealUseListener::new );
	}

	@Override
	public void reloadConfig() {
		itemHealTable.clear();

		getPlugin().getConfig().addDefault( FOOD_CFG_PREFIX + ".STONE:13.healAmount", 1 );
		getPlugin().getConfig().addDefault( FOOD_CFG_PREFIX + ".STONE:13.msDelay", 500 );
		getPlugin().getConfig().addDefault( FOOD_CFG_PREFIX + ".STONE.healAmount", 1 );
		getPlugin().getConfig().addDefault( FOOD_CFG_PREFIX + ".STONE.msDelay", 500 );
		getPlugin().getConfig().addDefault( FOOD_CFG_PREFIX + ".1.healAmount", 1 );
		getPlugin().getConfig().addDefault( FOOD_CFG_PREFIX + ".1.msDelay", 500 );
		getPlugin().getConfig().addDefault( FOOD_CFG_PREFIX + ".1:0.healAmount", 1 );
		getPlugin().getConfig().addDefault( FOOD_CFG_PREFIX + ".1:0.msDelay", 500 );

		ConfigurationSection section = getPlugin().getConfig().getConfigurationSection( FOOD_CFG_PREFIX );
		for ( String key : section.getKeys( false ) ) {
			String matStr;
			Byte data;
			if ( key.contains( ":" ) ) {
				String[] split = key.split( ":" );
				matStr = split[ 0 ];
				try {
					data = Byte.parseByte( split[ 1 ] );
				}
				catch ( NumberFormatException ex ) {
					getPlugin().getLogger().warning( "Item " + key + " is not configured properly, invalid data value found." );
					continue;
				}
			} else {
				matStr = key;
				data = 0;
			}
			Material mat = Material.matchMaterial( matStr );
			if ( mat == Material.STONE ) {
				continue;
			}
			if ( mat == null ) {
				getPlugin().getLogger().warning( "Item " + key + " is not configured properly, could not find material " + matStr + '.' );
				continue;
			}
			HealItemValues healItemValues = itemHealTable.get( mat, data );
			ConfigurationSection subSection = section.getConfigurationSection( key );
			if ( healItemValues == null ) {
				itemHealTable.put( mat, data, HealItemValues.fromConfigurationSection( subSection ) );
			} else {
				getPlugin().getLogger().warning( "Duplicate item configuration of item " + mat + ":" + data + " found, overwriting existing" );
				itemHealTable.put( mat, data, HealItemValues.fromConfigurationSection( subSection, healItemValues ) );
			}
		}
	}

	@SuppressWarnings("deprecation")
	public HealItemValues getItemConfig(ItemStack is) {
		return itemHealTable.get( is.getType(), is.getData().getData() );
	}
}
