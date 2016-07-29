package de.janmm14.epicpvp.warz.brokenleg;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.util.MiscUtil;

public class BrokenLegModule extends Module<BrokenLegModule> {

	private static final String PATH_PREFIX = "brokenleg";
	private static final String PATH_SLOWNESS_AMPLIFIER = PATH_PREFIX + ".slowness.amplifier";
	private static final String PATH_SLOWNESS_DURATION = PATH_PREFIX + ".slowness.duration_ticks";
	private static final String PATH_REQUIRED_DAMAGE = PATH_PREFIX + ".required_fall_damage_half_hearts";
	private static final String PATH_NOTIFICATION_MESSAGE = PATH_PREFIX + ".notification_message";
	private String notificationMessage;
	private int durationTicks;
	private int slownessAmplifier;
	private int requiredHalfHeartsDamage;

	public BrokenLegModule(WarZ plugin) {
		super( plugin, FallDamageListener::new, BoneUseListener::new );
	}

	@Override
	public void reloadConfig() {
		getConfig().addDefault( PATH_SLOWNESS_DURATION, 30 * 20 );
		durationTicks = getConfig().getInt( PATH_SLOWNESS_DURATION );
		getConfig().addDefault( PATH_SLOWNESS_AMPLIFIER, 1 );
		slownessAmplifier = getConfig().getInt( PATH_SLOWNESS_AMPLIFIER ) - 1; //bukkit amplifiers start at 0
		getConfig().addDefault( PATH_REQUIRED_DAMAGE, 4 );
		requiredHalfHeartsDamage = getConfig().getInt( PATH_REQUIRED_DAMAGE );
		getConfig().addDefault( PATH_NOTIFICATION_MESSAGE, "Du hast einen Beinbruch bekommen! Nutze jetzt einen Knochen!" );
		notificationMessage = MiscUtil.translateColorCode( getConfig().getString( PATH_NOTIFICATION_MESSAGE ) );
	}

	public int getDurationTicks() {
		return durationTicks;
	}

	public int getSlownessAmplifier() {
		return slownessAmplifier;
	}

	public int getRequiredHalfHeartsDamage() {
		return requiredHalfHeartsDamage;
	}

	public String getNotificationMessage() {
		return notificationMessage;
	}
}
