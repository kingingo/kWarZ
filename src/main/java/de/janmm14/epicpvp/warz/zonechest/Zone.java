package de.janmm14.epicpvp.warz.zonechest;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.janmm14.epicpvp.warz.util.RandomItemHolder;

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
	private final List<ChestItemHolder> items;

	public ItemStack getRandomChestItem() {
		return RandomItemHolder.chooseRandom( items );
	}

	public static Zone byConfigurationSection(String name, ConfigurationSection section) {
		List<ChestItemHolder> chestItemHolders = new ArrayList<>();
		ConfigurationSection itemSection = section.getConfigurationSection( "items" );
		chestItemHolders.addAll(
			itemSection.getKeys( false ).stream()
				.map( key -> ChestItemHolder.byConfigurationSection( itemSection.getConfigurationSection( key ) ) )
				.collect( Collectors.toList() ) );
		return new Zone( name, chestItemHolders );
	}
}
