package de.janmm14.epicpvp.warz.healitems;

import org.bukkit.configuration.ConfigurationSection;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public class HealItemValues {

	private final int healAmount;
	private final int msDelay;

	public static HealItemValues fromConfigurationSection(ConfigurationSection section) {
		return new HealItemValues( section.getInt( "healAmount" ), section.getInt( "msDelay" ) );
	}

	public static HealItemValues fromConfigurationSection(ConfigurationSection section, HealItemValues defaults) {
		int finalHealAmount = defaults.getHealAmount();
		int finalMsDelay = defaults.getMsDelay();
		Object healAmount = section.get( "healAmount" );
		if ( healAmount != null ) {
			finalHealAmount = section.getInt( "healAmount" );
		}
		Object msDelay = section.get( "msDelay" );
		if ( msDelay != null ) {
			finalMsDelay = section.getInt( "msDelay" );
		}
		return new HealItemValues( finalHealAmount, finalMsDelay );
	}
}
