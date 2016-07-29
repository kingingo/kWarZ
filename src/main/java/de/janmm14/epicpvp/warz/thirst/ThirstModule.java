package de.janmm14.epicpvp.warz.thirst;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class ThirstModule extends Module<ThirstModule> {

	private static final String PATH_PREFIX = "thirst.";
	private static final String FOOD_CFG_PREFIX = PATH_PREFIX + "thirst.items"; //no dot at the end (!)
	private final Table<Material, Byte, Double> itemThirstFillTable = HashBasedTable.create( 1, 4 );
	private double distanceMultiplier;
	private double lowThirstThreshold;

	public ThirstModule(WarZ plugin) {
		super( plugin, ThirstListener::new );
		//TODO implement
	}

	@Override
	public void reloadConfig() {
		getConfig().addDefault( PATH_PREFIX + "fullThirstBlockDistance", 400.0 );
		distanceMultiplier = 1.0D / getConfig().getDouble( PATH_PREFIX + "fullThirstBlockDistance" );
		getConfig().addDefault( PATH_PREFIX + "lowThirstThreshold", .2 );
		lowThirstThreshold = getConfig().getDouble( PATH_PREFIX + "lowThirstThreshold" );

		itemThirstFillTable.clear();

		getConfig().addDefault( FOOD_CFG_PREFIX + ".STONE:13", .2 );
		getConfig().addDefault( FOOD_CFG_PREFIX + ".STONE", .2 );
		getConfig().addDefault( FOOD_CFG_PREFIX + ".1", .2 );
		getConfig().addDefault( FOOD_CFG_PREFIX + ".1:0", .2 );

		ConfigurationSection section = getConfig().getConfigurationSection( FOOD_CFG_PREFIX );
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
					getPlugin().getLogger().warning( "Item " + key + " is not configured properly, invalid data value found: " + split[ 1 ] );
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
			itemThirstFillTable.put( mat, data, section.getDouble( key ) );
		}
	}

	@SuppressWarnings("deprecation")
	public Double getThirstFillAmount(ItemStack is) {
		return itemThirstFillTable.get( is.getType(), is.getData().getData() );
	}

	public double getDistanceMultiplier() {
		return distanceMultiplier;
	}

	public double getLowThirstThreshold() {
		return lowThirstThreshold;
	}
}
