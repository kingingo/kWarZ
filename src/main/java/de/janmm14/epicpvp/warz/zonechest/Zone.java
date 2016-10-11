package de.janmm14.epicpvp.warz.zonechest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.shampaggon.crackshot.CSUtility;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.itemrename.ItemRenameModule;
import de.janmm14.epicpvp.warz.util.random.RandomThingGroupHolder;
import de.janmm14.epicpvp.warz.util.random.RandomThingHolder;
import de.janmm14.epicpvp.warz.util.random.RandomUtil;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = "worldguardName")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Zone {

	private static final CSUtility CS_UTILITY = new CSUtility();
	@NonNull
	private final String worldguardName;
	@NonNull
	private final String zoneName;
	@NonNull
	private final List<RandomThingGroupHolder<ItemStack>> itemGroups;
	private final int minItemGroups;
	private final int maxItemGroups;

	public List<ItemStack> getRandomChoosenChestItems() {
		if ( WarZ.DEBUG )
			System.out.println( "getting random chest items for zone " + zoneName + " (" + worldguardName + ")" );
		List<RandomThingGroupHolder<ItemStack>> currItemgroups = new ArrayList<>( this.itemGroups );
		List<ItemStack> result = new ArrayList<>();
		int randomInt = RandomUtil.getRandomInt( minItemGroups, maxItemGroups );
		if ( WarZ.DEBUG )
			System.out.println( "randomInt = " + randomInt );
		for ( int i = 0; i < randomInt; i++ ) {
			RandomThingGroupHolder<ItemStack> itemgroup = RandomThingHolder.chooseRandomHolder( currItemgroups );
//			if ( WarZ.DEBUG )
//				System.out.println( "itemgroup = " + itemgroup );
			List<ItemStack> toAdd = RandomThingGroupHolder.groupChooseRandom( itemgroup );
			if ( WarZ.DEBUG )
				System.out.println( "toAdd = " + toAdd );
			if ( toAdd != null ) {
				for ( ItemStack is : toAdd ) {
					if ( is == null ) {
						if ( WarZ.DEBUG )
							System.out.println( "ItemStack is null" );
						continue;
					}
					is = is.clone();
					WarZ.getInstance().getModuleManager().getModule( ItemRenameModule.class ).renameIfNeeded( is );
					is = crackshotRename( is );
					result.add( is );
				}
//				currItemgroups.remove( itemgroup );
			}
		}
		return result;
	}

	public static ItemStack crackshotRename(ItemStack is) {
		String weaponTitle = CS_UTILITY.getHandle().convItem( is ); //String weaponTitle = CS_UTILITY.getWeaponTitle( is );
		if ( weaponTitle != null ) {
			is = CS_UTILITY.generateWeapon( weaponTitle );
		}
		return is;
	}

	public static Zone byConfigurationSection(String worldguardName, String zoneName, ConfigurationSection section) {
		System.out.println( "Loading zone " + zoneName + " for worldguard region " + worldguardName );
		ConfigurationSection itemSection = section.getConfigurationSection( "itemgroups" );
		if ( itemSection == null ) {
			System.err.println( "Could not find itemgroups section for zone " + zoneName + " (" + worldguardName + ") in " + section.getCurrentPath() );
			return null;
		}

		List<RandomThingGroupHolder<ItemStack>> itemGroups = itemSection.getKeys( false ).stream()
			.map( key -> ConfigUtil.readItemStackRandomGroup( itemSection.getConfigurationSection( key ) ) )
//			.sorted( (o1, o2) -> Double.compare( o2.getProbability(), o1.getProbability() ) ) //sort reverse probability - highest first
			.collect( Collectors.toList() );
		
		return new Zone( worldguardName, zoneName, itemGroups, section.getInt( "itemgroup_minamount" ), section.getInt( "itemgroup_maxamount" ) );
	}

	public Vector calculateMiddle() {
		ProtectedRegion worldGuardRegion = WorldGuardPlugin.inst()
			.getRegionManager( Bukkit.getWorld( "world" ) )
			.getRegion( worldguardName );
		if ( worldGuardRegion == null ) {
			new IllegalStateException( "Could not find worldedit region " + worldguardName + " (" + zoneName + ") while searching for middle point" ).printStackTrace();
			return Bukkit.getWorld( "world" ).getSpawnLocation().toVector();
		}
		com.sk89q.worldedit.Vector middle = com.sk89q.worldedit.Vector.getMidpoint( worldGuardRegion.getMinimumPoint(), worldGuardRegion.getMaximumPoint() );
		return new Vector( middle.getX(), middle.getY(), middle.getZ() );
	}

	public String toShortString() {
		return "Zone@" + Integer.toHexString( super.hashCode() ) + "(wgName=" + this.worldguardName + ", itemGroups.size()=" + this.itemGroups.size() + ")";
	}
}
