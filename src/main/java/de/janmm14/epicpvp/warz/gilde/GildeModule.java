package de.janmm14.epicpvp.warz.gilde;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.gilde.GildSection;
import dev.wolveringer.gilde.Gilde;
import dev.wolveringer.gilde.GildeType;
import dev.wolveringer.nbt.NBTTagCompound;
import eu.epicpvp.kcore.StatsManager.StatsManagerRepository;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.newGilde.GildeHandler;
import lombok.Getter;

public class GildeModule extends Module<GildeModule> implements Listener {

	@Getter
	private BlockVector chest;
	@Getter
	private GildeHandler handler;
	private HashMap<UUID,Inventory> inventories = new HashMap<>();
	
	public GildeModule(WarZ plugin) {
		super( plugin, module -> module );
		this.handler=new GildeHandler(GildeType.WARZ);
		StatsManagerRepository.getStatsManager(GameType.WARZ).setGilde(handler);
	}

	public void onDisable() {
		for(UUID uuid : inventories.keySet()){
			Gilde gilde = handler.getGildeManager().getGilde(uuid);
			GildSection section = gilde.getSelection(GildeType.WARZ);
			
			section.getCostumData().setString("inventory", UtilInv.itemStackArrayToBase64(inventories.get(uuid).getContents()));
			section.saveCostumData();
		}
		this.inventories.clear();
	}
	
	public void setChest(@Nullable BlockVector chest){
		this.chest=chest;
		getConfig().set("gilde.chest",this.chest);
		getPlugin().saveConfig();
	}
	
	public void openInventory(Player plr){
		int playerId = UtilPlayer.getPlayerId(plr);
		
		if(handler.hasGilde(playerId)){
			Gilde gilde = handler.getGilde(playerId);
			
			if(!inventories.containsKey(gilde.getUuid())){
				loadInventory(playerId);
			}
			
			if(inventories.containsKey(gilde.getUuid()))
				plr.openInventory(inventories.get(gilde.getUuid()));
		}
	}
	
	public void loadInventory(int playerId){
		if(handler.hasGilde(playerId)){
			GildSection section = handler.getSection(playerId);
			
			if(section != null){
				if(!inventories.containsKey(section.getHandle().getUuid())){
					NBTTagCompound data = handler.getData(playerId);
					Inventory inventory = Bukkit.createInventory(new GildenChestHolder(this), 9, "Gilde Chest:");
					String base64 = data.getString("inventory");
					
					if(!base64.isEmpty()){
						try {
							ItemStack[] items = UtilInv.itemStackArrayFromBase64(base64);
							inventory.setContents(items);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					inventories.put(section.getHandle().getUuid(), inventory);
				}
			}
		}
	}
	
	public void deleteInventory(int playerId){
		if(saveInventory(playerId)){
			inventories.remove(handler.getGilde(playerId).getUuid());
		}
	}
	
	public boolean saveInventory(int playerId){
		if(handler.hasGilde(playerId)){
			Gilde gilde = handler.getGilde(playerId);
			
			if(inventories.containsKey(gilde.getUuid())){
				Inventory inventory = inventories.get(gilde.getUuid());
				for(HumanEntity e : new ArrayList<>(inventory.getViewers()))e.closeInventory();

				NBTTagCompound data = handler.getData(playerId);
				data.setString("inventory", UtilInv.itemStackArrayToBase64(inventory.getContents()));
				handler.saveData(playerId);
				return true;
			}
		}
		return false;
	}

	@Override
	public void reloadConfig() {
		Vector vector = getConfig().getVector( "gilde.chest" );
		
		if(vector != null){
			this.chest=vector.toBlockVector();
		}
	}
}
