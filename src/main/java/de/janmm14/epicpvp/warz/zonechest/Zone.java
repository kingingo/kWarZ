package de.janmm14.epicpvp.warz.zonechest;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import de.janmm14.epicpvp.warz.util.random.RandomThingGroupHolder;
import de.janmm14.epicpvp.warz.util.random.RandomThingHolder;
import de.janmm14.epicpvp.warz.util.random.RandomUtil;
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
		List<RandomThingGroupHolder<ItemStack>> currItemgroups = this.items;
		List<ItemStack> result = new ArrayList<>();
		int randomInt = RandomUtil.getRandomInt( minItemGroups, maxItemGroups );
		for ( int i = 0; i < randomInt; i++ ) {
			RandomThingGroupHolder<ItemStack> itemgroup = RandomThingHolder.chooseRandomHolder( currItemgroups );
			List<ItemStack> toAdd = RandomThingGroupHolder.groupChooseRandom( itemgroup );
			if (toAdd != null) {
				result.addAll( toAdd );
				currItemgroups.remove( itemgroup );
			}
		}
		return result;
	}

	public static Zone byConfigurationSection(String name, ConfigurationSection section) {
		ConfigurationSection itemSection = section.getConfigurationSection( "itemgroups" );

		List<RandomThingGroupHolder<ItemStack>> itemGroups = itemSection.getKeys( false ).stream()
			.map( key -> ConfigUtil.readItemStackGroup( itemSection.getConfigurationSection( key ) ) )
			.sorted( (o1, o2) -> -Double.compare( o1.getProbability(), o2.getProbability() ) ) //sort reverse probability - highest first
			.collect( Collectors.toList() );

		return new Zone( name, itemGroups, section.getInt( "itemgroup_minamount" ), section.getInt( "itemgroup_maxamount" ) );
	}
}
