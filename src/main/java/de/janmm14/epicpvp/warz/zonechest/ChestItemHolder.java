package de.janmm14.epicpvp.warz.zonechest;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

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
public class ChestItemHolder implements RandomItemHolder<ItemStack> {

	private final ItemStack item;
	private final double probability;

	public static ChestItemHolder byConfigurationSection(ConfigurationSection section) {
		return new ChestItemHolder( section.getItemStack( "item" ), section.getDouble( "probability" ) );
	}
}
