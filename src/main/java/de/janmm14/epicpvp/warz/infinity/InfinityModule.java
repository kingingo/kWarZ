package de.janmm14.epicpvp.warz.infinity;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import eu.epicpvp.kcore.Util.UtilInv;

public class InfinityModule extends Module<InfinityModule> implements Listener{

	private final static String PATH = "infinity.";
	private final static String PATH_ITEMS = PATH + "items.";
	private final ArrayList<String> items = Lists.newArrayList();
	
	public InfinityModule(WarZ plugin) {
		super( plugin , module -> module);
	}
	
	@EventHandler
	public void drop(PlayerDropItemEvent ev){
		if(getConfig().isBoolean(PATH + "cancel.drop") && ev.getItemDrop()!=null
				&&ev.getItemDrop().getItemStack()!=null
				&&ev.getItemDrop().getItemStack().getType()!=Material.AIR){
			if(items.contains(ev.getItemDrop().getItemStack().getTypeId()+UtilInv.GetData(ev.getItemDrop().getItemStack()))){
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void pickup(PlayerPickupItemEvent ev){
		if(getConfig().isBoolean(PATH + "cancel.pickup") && ev.getItem()!=null
				&&ev.getItem().getItemStack()!=null
				&&ev.getItem().getItemStack().getType()!=Material.AIR){
			if(items.contains(ev.getItem().getItemStack().getTypeId()+UtilInv.GetData(ev.getItem().getItemStack()))){
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void death(PlayerDeathEvent ev){
		if(!getConfig().isBoolean(PATH + "cancel.loseByDeath"))return;
		ItemStack item;
		for(int i = 0; i<ev.getDrops().size(); i++){
			item = (ItemStack)ev.getDrops().get(i);
			
			if(item!=null&&item.getType()!=Material.AIR){
				if(items.contains( item.getTypeId()+UtilInv.GetData(item) )){
					ev.getDrops().remove(item);
					
					final ItemStack item_copy = item;
					Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable() {
						
						@Override
						public void run() {
							ev.getEntity().getInventory().addItem(item_copy);
						}
					}, 20L);
				}
			}
		}
	}

	@Override
	public void reloadConfig() {
		items.clear();
		getConfig().addDefault(PATH + "cancel.pickup", true);
		getConfig().addDefault(PATH + "cancel.drop", true);
		getConfig().addDefault(PATH + "cancel.loseByDeath", true);
		getConfig().addDefault(PATH_ITEMS, "7:0");
		
		ConfigurationSection itemSection = getConfig().getConfigurationSection( PATH_ITEMS );
		for ( String key : itemSection.getKeys( false ) ) {
			if ( key.startsWith( "7:" ) ) {
				continue;
			}
			items.add(key);
		}
	}

	@Override
	public void onDisable() {
		
	}
}
