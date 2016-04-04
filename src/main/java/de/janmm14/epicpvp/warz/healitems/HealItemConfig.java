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
public class HealItemConfig {

	private final int healAmount;
	private final int tickDelay;

	public static HealItemConfig byConfigurationSection(ConfigurationSection section) {
		return new HealItemConfig( section.getInt( "healAmount" ), section.getInt( "tickDelay" ) );
	}
}
