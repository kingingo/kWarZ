package de.janmm14.epicpvp.warz;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import de.janmm14.epicpvp.warz.command.CommandWarZ;
import de.janmm14.epicpvp.warz.hooks.LanguageConverter;
import de.janmm14.epicpvp.warz.hooks.UserDataConverter;
import dev.wolveringer.bukkit.permissions.GroupTyp;
import dev.wolveringer.client.connection.ClientType;
import eu.epicpvp.datenclient.client.debug.Debugger;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameType;
import eu.epicpvp.kcore.AACHack.AACHack;
import eu.epicpvp.kcore.ChunkGenerator.CleanroomChunkGenerator;
import eu.epicpvp.kcore.Command.CommandHandler;
import eu.epicpvp.kcore.Command.Admin.CommandCMDMute;
import eu.epicpvp.kcore.Command.Admin.CommandChatMute;
import eu.epicpvp.kcore.Command.Admin.CommandFly;
import eu.epicpvp.kcore.Command.Admin.CommandFlyspeed;
import eu.epicpvp.kcore.Command.Admin.CommandK;
import eu.epicpvp.kcore.Command.Admin.CommandPvPMute;
import eu.epicpvp.kcore.Command.Admin.CommandToggle;
import eu.epicpvp.kcore.Command.Admin.CommandTp;
import eu.epicpvp.kcore.Command.Admin.CommandTpHere;
import eu.epicpvp.kcore.Command.Admin.CommandTppos;
import eu.epicpvp.kcore.Command.Admin.CommandVanish;
import eu.epicpvp.kcore.Command.Commands.CommandClearInventory;
import eu.epicpvp.kcore.Command.Commands.CommandEnderchest;
import eu.epicpvp.kcore.Command.Commands.CommandInvsee;
import eu.epicpvp.kcore.Command.Commands.CommandRepair;
import eu.epicpvp.kcore.Disguise.DisguiseManager;
import eu.epicpvp.kcore.Kit.Perk;
import eu.epicpvp.kcore.Kit.PerkManager;
import eu.epicpvp.kcore.Kit.Command.CommandPerk;
import eu.epicpvp.kcore.Kit.Perks.PerkHat;
import eu.epicpvp.kcore.Kit.Perks.PerkKillZombie;
import eu.epicpvp.kcore.Kit.Perks.PerkLessDamageCause;
import eu.epicpvp.kcore.Kit.Perks.PerkNoWaterdamage;
import eu.epicpvp.kcore.Listener.AntiCrashListener.AntiCrashListener;
import eu.epicpvp.kcore.Listener.Chat.ChatListener;
import eu.epicpvp.kcore.Listener.Command.ListenerCMD;
import eu.epicpvp.kcore.Listener.EnderChest.EnderChestListener;
import eu.epicpvp.kcore.Permission.PermissionManager;
import eu.epicpvp.kcore.StatsManager.StatsManagerRepository;
import eu.epicpvp.kcore.TeleportManager.TeleportCheck;
import eu.epicpvp.kcore.TeleportManager.TeleportManager;
import eu.epicpvp.kcore.Update.Updater;
import eu.epicpvp.kcore.UpdateAsync.UpdaterAsync;
import eu.epicpvp.kcore.UserDataConfig.UserDataConfig;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilTime;
import lombok.Getter;

@Getter
public class WarZ extends JavaPlugin {

	public static boolean DEBUG;
	public static int SLOTS = 0;
	public static int SLOTS_PREMIUM = 0;
	@Getter
	private static WarZ instance;
	private ModuleManager moduleManager;
	private UserDataConverter userDataConverter = new UserDataConverter();
	private LanguageConverter languageConverter = new LanguageConverter();
	private UserDataConfig userDataConfig;

	public WarZ() {
		instance = this;
	}

	@Override
	public void onEnable() {
		Debugger.setEnabled( false );
		setConfigOptions();
		SLOTS = getConfig().getInt( "slots" );
		SLOTS_PREMIUM = getConfig().getInt( "slots_premium" );
		DEBUG = getConfig().getBoolean( "debug" );
		if ( DEBUG ) {
			getLogger().info( "Debug mode activated!" );
		}

//		registerTabExecutor( "warz", new CommandWarZ( this ) );

		setupKStuff();

		getServer().getPluginManager().registerEvents( new WarZListener( this ), this );
		//need to delay
		getServer().getScheduler().runTask( this, () -> {
			moduleManager = new ModuleManager( this );
			moduleManager.discoverAndLoadModules();
			saveConfig();
		} );
	}

	private void setupKStuff() {
		UtilServer.setPluginInstance( this );

		getConfig().addDefault( "mysql.host", "localhost" );
		getConfig().addDefault( "mysql.user", "user" );
		getConfig().addDefault( "mysql.database", "database" );
		getConfig().addDefault( "mysql.password", "password" );
		UtilServer.createMySQL( getConfig().getString( "mysql.user" ), getConfig().getString( "mysql.password" ), getConfig().getString( "mysql.host" ), getConfig().getString( "mysql.database" ) );

		getConfig().addDefault( "dataserver.host", "localhost" );
		getConfig().addDefault( "dataserver.port", "9052" );
		UtilServer.createClient( this, ClientType.OTHER, getConfig().getString( "dataserver.host" ), getConfig().getInt( "dataserver.port" ), "WarZ" );

		new PermissionManager( this, GroupTyp.WARZ );
		StatsManagerRepository.getStatsManager( GameType.WARZ );
		UtilTime.setTimeManager( UtilServer.getPermissionManager() );
		userDataConfig = new UserDataConfig( this );
		new AACHack( "WARZ" );

		new PerkListener( new PerkManager( this, new Perk[]{
			new PerkNoWaterdamage(),
			new PerkLessDamageCause( 50, DamageCause.FALL ),
			new PerkHat(),
			new PerkKillZombie()
		} ) );
		UtilServer.getCommandHandler().register( CommandPerk.class, new CommandPerk( UtilServer.getPerkManager() ) );
		UtilServer.getCommandHandler().register( CommandFly.class, new CommandFly( this ) );
		UtilServer.getCommandHandler().register( CommandFlyspeed.class, new CommandFlyspeed() );
		UtilServer.getCommandHandler().register( CommandTp.class, new CommandTp() );
		UtilServer.getCommandHandler().register( CommandTpHere.class, new CommandTpHere() );
		UtilServer.getCommandHandler().register( CommandClearInventory.class, new CommandClearInventory() );
		UtilServer.getCommandHandler().register( CommandInvsee.class, new CommandInvsee( UtilServer.getMysql() ) );
		UtilServer.getCommandHandler().register( CommandVanish.class, new CommandVanish( this ) );
		UtilServer.getCommandHandler().register( CommandChatMute.class, new CommandChatMute( this ) );
		UtilServer.getCommandHandler().register( CommandPvPMute.class, new CommandPvPMute( this ) );
		UtilServer.getCommandHandler().register( CommandCMDMute.class, new CommandCMDMute( this ) );
		UtilServer.getCommandHandler().register( CommandToggle.class, new CommandToggle( this ) );
		UtilServer.getCommandHandler().register( CommandTppos.class, new CommandTppos() );
		UtilServer.getCommandHandler().register( CommandEnderchest.class, new CommandEnderchest( UtilServer.getMysql() ) );
		UtilServer.getCommandHandler().register( CommandK.class, new CommandK() );
		UtilServer.getCommandHandler().register( CommandRepair.class, new CommandRepair() );
		UtilServer.getCommandHandler().register( CommandWarZ.class, new CommandWarZ( this ) );

		UtilServer.getLagListener();
		StatsManagerRepository.getStatsManager( GameType.Money );
		StatsManagerRepository.getStatsManager( GameType.WARZ );
		new DisguiseManager( this );
		new ListenerCMD( this );
		new ChatListener();
		new AntiCrashListener( UtilServer.getClient(), UtilServer.getMysql() );
		new EnderChestListener( getUserDataConfig() );
		new TeleportManager( new CommandHandler( this ), UtilServer.getPermissionManager(), TeleportCheck.NEAR );
		new Updater(this);
		new UpdaterAsync(this);

		//lets try to support reloades to some extend
		for ( Player plr : Bukkit.getOnlinePlayers() ) {
			StatsManagerRepository.getStatsManager( GameType.WARZ ).join( new PlayerJoinEvent( plr, "" ) );
			UtilServer.getPermissionManager().loadPlayer( plr, UtilPlayer.getPlayerId( plr ) );
		}
	}

	@Override
	public void onDisable() {
		instance = null;
		saveConfig();
		getModuleManager().onDisable();
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new CleanroomChunkGenerator( "1,bedrock,31,dirt,2,water" );
	}

	public void reloadCfg() {
		reloadConfig();
		setConfigOptions();
		SLOTS = getConfig().getInt( "slots" );
		SLOTS_PREMIUM = getConfig().getInt( "slots_premium" );
		DEBUG = getConfig().getBoolean( "debug" );
		if ( DEBUG ) {
			getLogger().info( "Debug mode is activated!" );
		} else {
			getLogger().info( "Debug mode is deactivated!" );
		}
		moduleManager.reloadAllModuleConfigs();
		saveConfig();
	}

	private void setConfigOptions() {
		getConfig().options()
			.copyDefaults( true )
			.copyHeader( true )
			.header( "WarZ v" + getDescription().getVersion() );
		getConfig().addDefault( "debug", false );
		getConfig().addDefault( "slots", 128 );
		getConfig().addDefault( "slots_premium", 160 );
	}

	public <T extends CommandExecutor & TabCompleter> void registerTabExecutor(String command, T tabExecutor) {
		PluginCommand cmd = getCommand( command );
		cmd.setExecutor( tabExecutor );
		cmd.setTabCompleter( tabExecutor );
	}
}
