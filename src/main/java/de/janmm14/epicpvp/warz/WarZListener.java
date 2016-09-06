package de.janmm14.epicpvp.warz;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import de.janmm14.epicpvp.warz.util.MiscUtil;
import dev.wolveringer.client.ClientWrapper;
import dev.wolveringer.dataserver.gamestats.GameType;
import eu.epicpvp.kcore.Events.ServerStatusUpdateEvent;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilWorld;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WarZListener implements Listener {

	private final WarZ plugin;

	@EventHandler(ignoreCancelled = true)
	public void onAsnycPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		ClientWrapper client = UtilServer.getClient();
		if ( client == null || !client.getHandle().isConnected() || !client.getHandle().isHandshakeCompleted() ) {
			event.disallow( AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Server not fully started up." );
		}
	}

	@EventHandler
	public void loadWorld(WorldLoadEvent ev){
		ev.getWorld().setAutoSave(false);
	}
	
	@EventHandler
	public void rendermap(MapInitializeEvent ev){
		MapView view = ev.getMap();
		
		view.setCenterX(0);
		view.setCenterZ(0);
		view.setScale(MapView.Scale.FARTHEST);
		
		MapView t = Bukkit.getMap((short) 25);
		for(MapRenderer render : t.getRenderers())view.addRenderer(render);
				
		if(WarZ.DEBUG){
			System.out.println("[DEBUG] MapInitializeEvent: create new Map ID -> "+view.getId());
		}
	}
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent ev) {
		ev.setCancelled( true );
	}

	@EventHandler
	public void onExplosion(ExplosionPrimeEvent ev) {
		ev.setCancelled( true );
	}

	@EventHandler
	public void soilChangeEntity(EntityInteractEvent event) {
		if ( ( event.getEntityType() != EntityType.PLAYER ) && ( event.getBlock().getType() == Material.SOIL ) ) {
			event.setCancelled( true );
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent ev) {
		ev.setDeathMessage( null );
		UtilPlayer.RespawnNow( ev.getEntity(), plugin );
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent ev) {
		ev.setJoinMessage( null );
		UtilPlayer.setTab( ev.getPlayer(), "WarZ" );
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent ev) {
		ev.setQuitMessage( null );
	}

	@EventHandler
	public void onServerStatusUpdate(ServerStatusUpdateEvent ev) {
		ev.getPacket().setPlayers( plugin.getServer().getOnlinePlayers().size() );
		ev.getPacket().setTyp( GameType.WARZ );
	}

	@EventHandler
	public void onSignCreate(SignChangeEvent ev) {
		if ( ev.getPlayer().hasPermission( PermissionType.CHAT_FARBIG.getPermissionToString() ) ) {
			for ( int i = 0; i < 4; i++ ) {
				ev.setLine( i, MiscUtil.translateColorCode( ev.getLine( i ) ) );
			}
		}
	}

	@EventHandler
	public void onCommandPreprocess(PlayerCommandPreprocessEvent ev) {
		if ( ev.getPlayer().isOp() ) {
			return;
		}
		String cmd = "";
		if ( ev.getMessage().indexOf( ' ' ) != -1 ) {
			String[] parts = ev.getMessage().split( " " );
			cmd = parts[ 0 ];
		} else {
			cmd = ev.getMessage();
		}

		if ( cmd.indexOf( ':' ) != -1 ) {
			ev.setCancelled( true );
		}
	}
}
