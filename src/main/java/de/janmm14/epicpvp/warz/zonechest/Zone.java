package de.janmm14.epicpvp.warz.zonechest;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.stream.Collectors;
import de.janmm14.epicpvp.warz.util.random.RandomThingGroupHolder;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Zone {

	private final String name;
	private final List<RandomThingGroupHolder<ItemStack>> items;
	private final int minItemGroups;
	private final int maxItemGroups;

	public List<ItemStack> getRandomChoosenChestItems() {
		//TODO min / max itemgroup amounts
		return RandomThingGroupHolder.groupChooseRandom( items );
	}

	public static Zone byConfigurationSection(String name, ConfigurationSection section) {
		ConfigurationSection itemSection = section.getConfigurationSection( "itemgroups" );

		List<RandomThingGroupHolder<ItemStack>> itemGroups = itemSection.getKeys( false ).stream()
			.map( key -> ConfigUtil.readItemStackGroup( itemSection.getConfigurationSection( key ) ) )
			.collect( Collectors.toList() );

		return new Zone( name, itemGroups, section.getInt( "itemgroup_minamount" ), section.getInt( "itemgroup_maxamount" ) );
	}
}
