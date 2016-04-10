package de.janmm14.epicpvp.warz.zombies;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
public class ZombieAttackInfo implements RandomItemHolder<PotionEffect> {

	private final PotionEffect item;
	private final double probability;

	public static ZombieAttackInfo fromConfigurationSection(ConfigurationSection section) {
		PotionEffect potionEffect = new PotionEffect(
			PotionEffectType.getByName( section.getString( "effect" ) ),
			section.getInt( "duration_in_half_ticks" ),
			section.getInt( "amplifier" )
		);
		return new ZombieAttackInfo(potionEffect, section.getDouble( "probability" ) );
	}
}
