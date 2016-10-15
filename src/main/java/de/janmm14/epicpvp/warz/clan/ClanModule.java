package de.janmm14.epicpvp.warz.clan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import eu.epicpvp.datenclient.client.LoadedPlayer;
import eu.epicpvp.datenclient.gilde.GildSection;
import eu.epicpvp.datenclient.gilde.Gilde;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameType;
import eu.epicpvp.datenserver.definitions.gilde.GildeType;
import eu.epicpvp.kcore.StatsManager.StatsManagerRepository;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.deliverychest.ItemModifier;
import eu.epicpvp.kcore.newGilde.GildeHandler;
import eu.epicpvp.nbt.NBTTagCompound;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

import lombok.Getter;

public class ClanModule extends Module<ClanModule> implements Listener {

	@Getter
	private BlockVector chest;
	@Getter
	private GildeHandler handler;
	private HashMap<UUID, Inventory> inventories = new HashMap<>();

	public ClanModule(WarZ plugin) {
		super( plugin, module -> module );
		this.handler=new GildeHandler(GildeType.WARZ);
		new ClanChestListener(this);
		StatsManagerRepository.getStatsManager(GameType.WARZ).setGilde(handler);
		UtilServer.getCommandHandler().register(CommandSetClanChest.class, new CommandSetClanChest(this));
	}

	@Override
	public void onDisable() {
		for ( Map.Entry<UUID, Inventory> entry : inventories.entrySet() ) {
			Gilde gilde = handler.getGildeManager().getGilde( entry.getKey() );
			GildSection section = gilde.getSelection( GildeType.WARZ );

			section.getCostumData().setString( "inventory", UtilInv.itemStackArrayToBase64( entry.getValue().getContents() ) );
			section.saveCostumData();
		}
		this.inventories.clear();
	}

	public void setChest(@Nullable BlockVector chest) {
		this.chest = chest;
		getConfig().set( "gilde.chest", this.chest );
		getPlugin().saveConfig();
	}

	public void openInventory(Player plr) {
		int playerId = UtilPlayer.getPlayerId( plr );

		if ( handler.hasGilde( playerId ) ) {
			Gilde gilde = handler.getGilde( playerId );

			if ( !inventories.containsKey( gilde.getUuid() ) ) {
				loadInventory( playerId );
			}

			if ( inventories.containsKey( gilde.getUuid() )){
				if(handler.getSection(plr).getPlayers().size() >= 4){
					plr.openInventory( inventories.get( gilde.getUuid() ) );
				}else{
					plr.sendMessage("§cEs müssen min. 4 Spieler im Clan sein, um diese funktion benutzten zukönnen.");
				}
			}
		}else{
			plr.sendMessage("§cDu hast keinen Clan.");
		}
	}

	public void loadInventory(int playerId) {
		if ( handler.hasGilde( playerId ) ) {
			GildSection section = handler.getSection( playerId );

			if ( section != null ) {
				if ( !inventories.containsKey( section.getHandle().getUuid() ) ) {
					NBTTagCompound data = handler.getData( playerId );
					Inventory inventory = Bukkit.createInventory( new ClanChestHolder( this ), 9 * 3, "Gilde Chest:" );
					String base64 = data.getString( "inventory" );

					if ( base64 != null && !base64.isEmpty() ) {
						try {
							ItemStack[] items = UtilInv.itemStackArrayFromBase64( base64 );
							inventory.setContents( items );
						}
						catch ( IOException e ) {
							e.printStackTrace();
						}
					}

					inventories.put( section.getHandle().getUuid(), inventory );
				}
			}
		}
	}

	public void deleteInventory(int playerId) {
		if ( saveInventory( playerId ) ) {
			inventories.remove( handler.getGilde( playerId ).getUuid() );
		}
	}

	public boolean saveInventory(int playerId) {
		if ( handler.hasGilde( playerId ) ) {
			Gilde gilde = handler.getGilde( playerId );

			if ( inventories.containsKey( gilde.getUuid() ) ) {
				Inventory inventory = inventories.get( gilde.getUuid() );
				for ( HumanEntity e : new ArrayList<>( inventory.getViewers() ) ) e.closeInventory();

				NBTTagCompound data = handler.getData( playerId );
				data.setString( "inventory", UtilInv.itemStackArrayToBase64( inventory.getContents() ) );
				handler.saveData( playerId );
				return true;
			}
		}
		return false;
	}

	@Override
	public void reloadConfig() {
		Vector vector = getConfig().getVector( "gilde.chest" );

		if ( vector != null ) {
			this.chest = vector.toBlockVector();
		}
	}
}
