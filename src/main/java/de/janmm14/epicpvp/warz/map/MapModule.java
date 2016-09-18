package de.janmm14.epicpvp.warz.map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import com.comphenix.packetwrapper.WrapperPlayServerMap;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

import net.minecraft.server.v1_8_R3.MapIcon;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import eu.epicpvp.kcore.Util.UtilWorldGuard;

public class MapModule extends Module<MapModule> implements Listener {

	public MapModule(WarZ plugin) {
		super( plugin, module -> module );
		ProtocolLibrary.getProtocolManager().addPacketListener( new PacketAdapter( new PacketAdapter.AdapterParameteters()
			.plugin( plugin )
			.serverSide()
			.types( PacketType.Play.Server.MAP ) ) {
			@Override
			public void onPacketSending(PacketEvent event) {
				if ( event.getPacketType() != PacketType.Play.Server.MAP ) {
					return;
				}
				
				WrapperPlayServerMap packet = new WrapperPlayServerMap( event.getPacket() );
				
				if( UtilWorldGuard.RegionFlag( event.getPlayer(), DefaultFlag.PVP ) ){
					packet.setMapIcons( new MapIcon[]{ new MapIcon( MapCursor.Type.BLUE_POINTER.getValue(),
							( byte ) ( event.getPlayer().getLocation().getBlockX() / 8 ),
							( byte ) ( event.getPlayer().getLocation().getBlockZ() / 8 ),
							( byte ) ( event.getPlayer().getLocation().getPitch() ) ) } );
				}else{
					packet.setMapIcons(new MapIcon[]{});
				}
				
				if ( WarZ.DEBUG ) {
					System.out.println( "Rewriting map packet for " + event.getPlayer().getName() +" MapId:"+packet.getItemDamage());
				}
			}

			@Override
			public void onPacketReceiving(PacketEvent event) {
				//has to be here due to a protocollib error
			}
		} );
	}

	@Override
	public void reloadConfig() {
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player plr = event.getPlayer();
		ItemStack item = plr.getItemInHand();
		if ( item != null && item.getType() == Material.EMPTY_MAP ) {
			ItemStack map = new ItemStack( Material.MAP );
			map.setDurability( ( short ) 25 ); //map id
			plr.setItemInHand( map );
			event.setCancelled( true );
			if ( WarZ.DEBUG ) {
				System.out.println( "[DEBUG] PlayerInteractEvent: swap empty map with map 25" );
			}
		}
	}

	@EventHandler
	public void onMapInitialize(MapInitializeEvent ev) {
		MapView view = ev.getMap();

		view.setCenterX( 0 );
		view.setCenterZ( 0 );
		view.setScale( MapView.Scale.FARTHEST );

		MapView t = Bukkit.getMap( ( short ) 25 );
		for ( MapRenderer render : t.getRenderers() ) view.addRenderer( render );

		if ( WarZ.DEBUG ) {
			System.out.println( "[DEBUG] MapInitializeEvent: create new Map ID -> " + view.getId() );
		}
	}
}
