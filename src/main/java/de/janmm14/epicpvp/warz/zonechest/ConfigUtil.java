package de.janmm14.epicpvp.warz.zonechest;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.util.random.RandomThingGroupHolder;
import de.janmm14.epicpvp.warz.util.random.RandomThingHolder;
import de.janmm14.epicpvp.warz.util.random.SimpleRandomThingGroupHolder;
import de.janmm14.epicpvp.warz.util.random.SimpleRandomThingHolder;
import lombok.NonNull;

public class ConfigUtil {

	public static RandomThingGroupHolder<ItemStack> readItemStackRandomGroup(ConfigurationSection section) {
		ConfigurationSection itemSection = section.getConfigurationSection( "items" );

		List<RandomThingHolder<ItemStack>> items = itemSection.getKeys( false ).stream()
			.map( key -> readItemStackRandom( itemSection.getConfigurationSection( key ) ) )
			.sorted( (o1, o2) -> -Double.compare( o1.getProbability(), o2.getProbability() ) ) //sort reverse probability - highest first
			.collect( Collectors.toList() );

		return new SimpleRandomThingGroupHolder<>( items, section.getInt( "minamount" ), section.getInt( "maxamount" ), section.getDouble( "probability" ) );
	}

	@NonNull
	public static RandomThingHolder<ItemStack> readItemStackRandom(@NonNull ConfigurationSection section) {
		ItemStack item;
		try {
			item = section.getItemStack( "item" );
		}
		catch ( Exception ex ) {
			WarZ.getInstance().getLogger().log( Level.SEVERE, "Could not read itemstack from " + section.getCurrentPath() + "!", ex );
			item = new ItemStack( Material.STONE, 42 );
		}
		if ( item == null ) {
			WarZ.getInstance().getLogger().log( Level.SEVERE, "Could not read itemstack from " + section.getCurrentPath() + "!" );
			item = new ItemStack( Material.STONE, 42 );
		}
		double probability = section.getDouble( "probability" );
		return new SimpleRandomThingHolder<>( item, probability );
	}
}
