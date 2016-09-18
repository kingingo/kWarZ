package de.janmm14.epicpvp.warz.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilDirection;
import eu.epicpvp.kcore.Util.UtilServer;
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
				
				if(event.getPlayer().getItemInHand()==null||event.getPlayer().getItemInHand().getType()!=Material.MAP){
					event.setCancelled(true);
					return;
				}
				
				WrapperPlayServerMap packet = new WrapperPlayServerMap( event.getPacket() );
				
				if( UtilWorldGuard.RegionFlag( event.getPlayer(), DefaultFlag.PVP ) ){
					if(event.getPlayer().isOp()){
						List<MapIcon> icons = new ArrayList<>();
						icons.add(new MapIcon( MapCursor.Type.BLUE_POINTER.getValue(),
								( byte ) ( event.getPlayer().getLocation().getBlockX() / 8 ),
								( byte ) ( event.getPlayer().getLocation().getBlockZ() / 8 ),
								( byte ) ( getRotation(event.getPlayer().getLocation()) )));
						
						for(Player plr : UtilServer.getPlayers()){
							if (plr.getUniqueId() != event.getPlayer().getUniqueId() ) {
								icons.add(new MapIcon( MapCursor.Type.RED_POINTER.getValue(),
										( byte ) ( plr.getLocation().getBlockX() / 8 ),
										( byte ) ( plr.getLocation().getBlockZ() / 8 ),
										( byte ) ( getRotation(plr.getLocation()) )));
							}
						}
						createCircle(icons, 200, event.getPlayer().getLocation());
						
						packet.setMapIcons( icons.toArray(new MapIcon[icons.size()]) );
					}else if(event.getPlayer().hasPermission(PermissionType.WARZ_MAP_OTHER_PLAYER.getPermissionToString())){
						List<MapIcon> icons = new ArrayList<>();
						icons.add(new MapIcon( MapCursor.Type.BLUE_POINTER.getValue(),
								( byte ) ( event.getPlayer().getLocation().getBlockX() / 8 ),
								( byte ) ( event.getPlayer().getLocation().getBlockZ() / 8 ),
								( byte ) ( getRotation(event.getPlayer().getLocation()) )));
						
						Collection<Entity> nearbyEntities = event.getPlayer().getWorld().getNearbyEntities( event.getPlayer().getLocation(), 200, 200, 200 );
						for ( Entity e : nearbyEntities ) {
							if ( e instanceof Player && e.getUniqueId() != event.getPlayer().getUniqueId() ) {
								icons.add(new MapIcon( MapCursor.Type.RED_POINTER.getValue(),
										( byte ) ( e.getLocation().getBlockX() / 8 ),
										( byte ) ( e.getLocation().getBlockZ() / 8 ),
										( byte ) ( getRotation(e.getLocation()) )));
							}
						}
						
						packet.setMapIcons( icons.toArray(new MapIcon[icons.size()]) );
					}else{
						packet.setMapIcons( new MapIcon[]{ new MapIcon( MapCursor.Type.BLUE_POINTER.getValue(),
								( byte ) ( event.getPlayer().getLocation().getBlockX() / 8 ),
								( byte ) ( event.getPlayer().getLocation().getBlockZ() / 8 ),
								( byte ) ( getRotation(event.getPlayer().getLocation()) ) ) } );
					}
					
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
	
	public void createCircle(List<MapIcon> icons,int r,Location center){
		int x;
		int z;    
        for (double i = 0.0; i < 360.0; i += 0.1) {
        	double angle = i * Math.PI / 180;
            x = (int)(center.getBlockX() + r * Math.cos(angle));
            z = (int)(center.getBlockZ() + r * Math.sin(angle));
            
            if((x/8) >= -64 && (x/8) <= 64
            		&& (z/8) >= -64 && (z/8) <= 64){
                icons.add(new MapIcon(MapCursor.Type.WHITE_POINTER.getValue(), (byte)(x/8), (byte)(z/8), (byte)0));
            }
        }
	}
	
	public byte getRotation(Location location) {
        double rotation = (location.getYaw() - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
         if (0 <= rotation && rotation < 22.5) {
            return 4; //WEST
        } else if (22.5 <= rotation && rotation < 67.5) {
            return 6; //NORTHWEST
        } else if (67.5 <= rotation && rotation < 112.5) {
            return 8; // NORTH
        } else if (112.5 <= rotation && rotation < 157.5) {
            return 10; //NORTHEAST
        } else if (157.5 <= rotation && rotation < 202.5) {
            return 12; //EAST
        } else if (202.5 <= rotation && rotation < 247.5) {
            return 14; //SOUTHEAST
        } else if (247.5 <= rotation && rotation < 292.5) {
            return 0; //SOUTH
        } else if (292.5 <= rotation && rotation < 337.5) {
            return 2; //SOUTHWEST
        } else if (337.5 <= rotation && rotation < 360.0) {
            return 4; //WEST
        } else {
            return 8; //NORTH
        }
    }

	@Override
	public void reloadConfig() {
	}

	@EventHandler
	public void join(PlayerJoinEvent ev){
		for(ItemStack item : ev.getPlayer().getInventory().getContents()){
			if(item!=null&&item.getType()==Material.MAP){
				item.setDurability((short) 0);
				item.setType(Material.EMPTY_MAP);
			}
		}
		ev.getPlayer().updateInventory();
	}
	
//	@EventHandler
//	public void onInteract(PlayerInteractEvent event) {
//		Player plr = event.getPlayer();
//		ItemStack item = plr.getItemInHand();
//		if ( item != null && item.getType() == Material.EMPTY_MAP ) {
//			ItemStack map = new ItemStack( Material.MAP );
//			map.setDurability( ( short ) 25 ); //map id
//			plr.setItemInHand( map );
//			event.setCancelled( true );
//			if ( WarZ.DEBUG ) {
//				System.out.println( "[DEBUG] PlayerInteractEvent: swap empty map with map 25" );
//			}
//		}
//	}

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
