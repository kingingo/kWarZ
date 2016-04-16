package de.janmm14.epicpvp.warz.zonechest;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockVector;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import lombok.Getter;

public class ZoneAndChestsModule extends Module<ZoneAndChestsModule> {

	private static final String PATH_PREFIX = "zonechest.";

	private static final String PATH_ZONES = PATH_PREFIX + "zones"; //no dot at the end (!)
	@Getter
	private final ChestContentManager chestContentManager;

	@SuppressWarnings("PointlessArithmeticExpression")
	public ZoneAndChestsModule(WarZ plugin) {
		super( plugin, ChestOpenListener::new );
		getPlugin().getServer().getScheduler().runTaskTimerAsynchronously( getPlugin(), chestContentManager = new ChestContentManager( this ), 1 * 20, 1 * 20 );
	}

	private Map<String, Zone> zones = new HashMap<>();

	private static ItemStack getExampleItemStackWithEverything() {
		ItemStack is = new ItemStack( Material.WOOD_SWORD, 2, ( short ) 21 );
		ItemMeta im = is.getItemMeta();

		im.addItemFlags( ItemFlag.values() );

		Arrays.stream( Enchantment.values() )
			.forEach( ench -> im.addEnchant( ench, 0, true ) );

		im.setDisplayName( "§5Display Name!!!!" );
		im.setLore( Arrays.asList( "§6Loreline 1", "§cLoreline two §7bla" ) );

		im.spigot().setUnbreakable( true );

		is.setItemMeta( im );
		return is;
	}

	@Override
	public void reloadConfig() {
		//TODO set example defaults
		getPlugin().getConfig().set( PATH_ZONES + ".info", "Eine Itemgroup wird nur einmal ausgewählt. Sollte man mehrere Gruppen forcieren wollen, so " );
		getPlugin().getConfig().addDefault( PATH_ZONES + ".ignoredExampleZone.itemgroup_minamount", 2 );
		getPlugin().getConfig().addDefault( PATH_ZONES + ".ignoredExampleZone.itemgroup_maxamount", 2 );
		getPlugin().getConfig().addDefault( PATH_ZONES + ".ignoredExampleZone.itemgroups.itemCategory1_NameIgnored.minamount", 1 );
		getPlugin().getConfig().addDefault( PATH_ZONES + ".ignoredExampleZone.itemgroups.itemCategory1_NameIgnored.maxamount", 1 );
		getPlugin().getConfig().addDefault( PATH_ZONES + ".ignoredExampleZone.itemgroups.itemCategory1_NameIgnored.probability", .5 );
		getPlugin().getConfig().addDefault( PATH_ZONES + ".ignoredExampleZone.itemgroups.itemCategory1_NameIgnored.items.unusedname.item", new ItemStack( Material.STONE, 1 ) );
		getPlugin().getConfig().addDefault( PATH_ZONES + ".ignoredExampleZone.itemgroups.itemCategory1_NameIgnored.items.unusedname.probability", .5 );
		getPlugin().getConfig().addDefault( PATH_ZONES + ".ignoredExampleZone.itemgroups.itemCategory1_NameIgnored.items.unusedname2.item", getExampleItemStackWithEverything() );
		getPlugin().getConfig().addDefault( PATH_ZONES + ".ignoredExampleZone.itemgroups.itemCategory1_NameIgnored.items.unusedname2.probability", .3 );

		//TODO load zones
		ConfigurationSection section = getPlugin().getConfig().getConfigurationSection( PATH_ZONES );

		for ( String key : section.getKeys( false ) ) {
			if ( key.equalsIgnoreCase( "ignoredExampleZone" ) || key.equalsIgnoreCase( "info" ) ) {
				continue;
			}
			zones.put( key, Zone.byConfigurationSection( key, section.getConfigurationSection( key ) ) );
		}
	}

	public Zone getZone(String name) {
		return zones.get( name );
	}

	public Zone getZone(Location location) {
		return WorldGuardPlugin.inst()
			.getRegionManager( location.getWorld() )
			.getApplicableRegions( location )
			.getRegions()
			.stream()
			.sorted() //ProtectedRegion has already an inverted sort on getPriority() - it should be the highest priority first
			.map( (protectedRegion) -> getZone( protectedRegion.getId() ) )
			.filter( Objects::nonNull )
			.findFirst()
			.orElse( null );
	}

	public Zone getZone(World world, BlockVector blockVector) {
		return getZone( new Location( world, blockVector.getX(), blockVector.getY(), blockVector.getZ() ) );
	}
}