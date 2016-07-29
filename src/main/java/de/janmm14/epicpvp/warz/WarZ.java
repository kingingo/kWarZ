package de.janmm14.epicpvp.warz;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import dev.wolveringer.bukkit.permissions.GroupTyp;
import dev.wolveringer.client.connection.ClientType;
//import dev.wolveringer.client.debug.Debugger;
import dev.wolveringer.dataserver.gamestats.GameType;
import eu.epicpvp.kcore.AACHack.AACHack;
import eu.epicpvp.kcore.ChunkGenerator.CleanroomChunkGenerator;
import eu.epicpvp.kcore.Listener.AntiCrashListener.AntiCrashListener;
import eu.epicpvp.kcore.Listener.Chat.ChatListener;
import eu.epicpvp.kcore.MySQL.MySQL;
import eu.epicpvp.kcore.Permission.PermissionManager;
import eu.epicpvp.kcore.StatsManager.StatsManagerRepository;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;

import de.janmm14.epicpvp.warz.hooks.LanguageConverter;
import de.janmm14.epicpvp.warz.hooks.UuidNameConverter;

import lombok.Getter;

@Getter
public class WarZ extends JavaPlugin {

	public static boolean DEBUG;
	@Getter
	private static WarZ instance;
	private ModuleManager moduleManager;
	private UuidNameConverter uuidNameConverter = new UuidNameConverter();
	private LanguageConverter languageConverter = new LanguageConverter();

	public WarZ() {
		instance = this;
	}

	@Override
	public void onEnable() {
		setConfigOptions();
		DEBUG = getConfig().getBoolean( "debug" );
		if ( DEBUG ) {
			getLogger().info( "Debug mode activated!" );
		}

		registerTabExecutor( "warz", new CommandWarZ( this ) );

		World world = Bukkit.getWorld( "world" );
		if ( world != null ) {
			getLogger().info( "Disabled automatic saving of world 'world', please do not use /save-all /save-on or any plugin to save worlds." );
			world.setAutoSave( false );
			world.setSpawnLocation( 0, 90, 0 );
		}

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
//		Debugger.setEnabled( false ); //remove "Pong" and "handeling x packets per second" from console
		getConfig().addDefault( "mysql.host", "localhost" );
		getConfig().addDefault( "mysql.user", "user" );
		getConfig().addDefault( "mysql.database", "database" );
		getConfig().addDefault( "mysql.password", "password" );
		new MySQL( getConfig().getString( "mysql.user" ), getConfig().getString( "mysql.password" ), getConfig().getString( "mysql.host" ), getConfig().getString( "mysql.database" ), this );

		getConfig().addDefault( "dataserver.host", "localhost" );
		getConfig().addDefault( "dataserver.port", "9052" );
		UtilServer.createClient( this, ClientType.OTHER, getConfig().getString( "dataserver.host" ), getConfig().getInt( "dataserver.port" ), "WarZ" );

		new PermissionManager( this, GroupTyp.WARZ );
		StatsManagerRepository.getStatsManager( GameType.WARZ );
		new AACHack( "WARZ" );

		new ChatListener( this, UtilServer.getPermissionManager() );
		new AntiCrashListener( UtilServer.getClient(), UtilServer.getMysql() );

		//lets try to support reloades
		for ( Player plr : Bukkit.getOnlinePlayers() ) {
			StatsManagerRepository.getStatsManager( GameType.WARZ ).join( new PlayerJoinEvent( plr, "" ) );
			UtilServer.getPermissionManager().loadPlayer( plr, UtilPlayer.getPlayerId( plr ) );
		}
	}

	@Override
	public void onDisable() {
		instance = null;
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new CleanroomChunkGenerator( "1,bedrock,31,dirt,2,water" );
	}

	public void reloadCfg() {
		reloadConfig();
		setConfigOptions();
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
	}

	public <T extends CommandExecutor & TabCompleter> void registerTabExecutor(String command, T tabExecutor) {
		PluginCommand cmd = getCommand( command );
		cmd.setExecutor( tabExecutor );
		cmd.setTabCompleter( tabExecutor );
	}
}
