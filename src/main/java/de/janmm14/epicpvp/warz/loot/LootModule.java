package de.janmm14.epicpvp.warz.loot;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.DefaultFlag;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.spawn.SpawnModule;
import eu.epicpvp.datenserver.definitions.arrays.CachedArrayList;
import eu.epicpvp.kcore.Hologram.nametags.NameTagPacketSpawner;
import eu.epicpvp.kcore.PacketAPI.Packets.WrapperArmorStandDataWatcher;
import eu.epicpvp.kcore.PacketAPI.Packets.WrapperPacketPlayOutAttachEntity;
import eu.epicpvp.kcore.PacketAPI.Packets.WrapperPacketPlayOutSpawnEntityLiving;
import eu.epicpvp.kcore.Util.TimeSpan;
import eu.epicpvp.kcore.Util.Title;
import eu.epicpvp.kcore.Util.UtilNumber;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilWorldGuard;
import eu.epicpvp.kcore.kConfig.kConfig;
import lombok.Getter;

public class LootModule extends Module<LootModule>{

	@Getter
	private HashMap<Player,Long> loottimer = new HashMap<>();
	private static final String PATH_PREFIX = "lootmodule.";
	@Getter
	private long LOOT_TIME_MILI = 60 * 10 * 1000;
	private int AMOUNT = 1;
	private int AMOUNT_PER_DAYS = 1;
	@Getter
	private CachedArrayList<Player> cachedlist = new CachedArrayList<>(30, TimeUnit.SECONDS);
	
	public LootModule(WarZ plugin) {
		super( plugin );
		
		UtilServer.getCommandHandler().register(CommandLoot.class, new CommandLoot(this));
		getPlugin().getServer().getScheduler().runTaskTimerAsynchronously( getPlugin(), new LootRunner( this ), 1 * 20, 1 * 20 );
		new LootListener(this);
		
		for(ArmorStand as : Bukkit.getWorld("world").getEntitiesByClass(ArmorStand.class)){
			if(as.getCustomName()!=null && as.getCustomName().equalsIgnoreCase("§cLootTime")){
				as.remove();
			}
		}
	}
	
	public void stopLootTime(Player plr){
		if(loottimer.containsKey(plr)){
			if ( UtilWorldGuard.RegionFlag( plr, DefaultFlag.PVP ) ) {
				plr.teleport( getModuleManager().getModule( SpawnModule.class ).getSpawn() );
			}
			loottimer.remove(plr);
//			Entity passenger = plr.getPassenger();
//			plr.eject();
//			passenger.remove();
			Title title = new Title("", "§cDie Loot Zeit ist abgelaufen!");
			title.send(plr);
		}
	}
	
	public void startLootTime(Player plr){
		if ( !UtilWorldGuard.RegionFlag( plr, DefaultFlag.PVP ) ) {
			List<Long> list = getLootList(plr);
			list.add(System.currentTimeMillis());
			setLootList(plr, list);
			loottimer.put(plr, System.currentTimeMillis()+LOOT_TIME_MILI);
			
//			WrapperPacketPlayOutSpawnEntityLiving armorStand = new WrapperPacketPlayOutSpawnEntityLiving(Integer.MAX_VALUE, EntityType.ARMOR_STAND, plr.getLocation());
//			armorStand.setY(plr.getLocation().getY());
//			
//			WrapperArmorStandDataWatcher watcher = new WrapperArmorStandDataWatcher(plr.getLocation().getWorld());
//			watcher.setCustomName("§cLoot time");
//			watcher.setCustomNameVisible(true);
//			watcher.setVisible(false);
//			watcher.setBasePlate(false);
//			watcher.setSmall(true);
//			armorStand.setDataWatcher(watcher);
//			
//			WrapperPacketPlayOutAttachEntity attach = new WrapperPacketPlayOutAttachEntity();
//			attach.setEntityID(armorStand.getEntityID());
//			attach.setVehicleID(plr.getEntityId());
//			attach.setLeached(false);
//
//			UtilPlayer.sendPacket(plr, armorStand);
//			UtilPlayer.sendPacket(plr, attach);
			
			Title title = new Title("", "§aDie Loot Zeit wurde gestartet...");
			title.send(plr);
		}
	}
	
	public void setLootList(Player plr,List<Long> list){
		kConfig config = getUserConfig( plr );
		config.set(PATH_PREFIX + "list", list);
		config.save();
	}
	
	public List<Long> getLootList(Player plr){
		kConfig config = getUserConfig( plr );
		List<Long> list = config.getLongList(PATH_PREFIX + "list");
		Collections.sort(list, Collections.reverseOrder());
		return list;
	}
	
	public boolean canUseLoot(Player plr){
		if(loottimer.containsKey(plr))return false;
		List<Long> list = getLootList(plr);
		
		if(list.size() >= AMOUNT){
			int amount = 0;
			for(int i = 0; i < AMOUNT; i++)
				if(list.get(i) > (System.currentTimeMillis()-(AMOUNT_PER_DAYS*TimeSpan.DAY)))
					amount++;
				
			if(WarZ.DEBUG)System.out.println("canUseLoot boolean=["+(!(amount >= AMOUNT))+"], amount= ["+amount+"], ConfigAmount= ["+AMOUNT+"], perDay= ["+AMOUNT_PER_DAYS+"], size= ["+list.size()+"]");
				
			return !(amount >= AMOUNT);
		}
		return true;
	}

	public kConfig getUserConfig(Player plr) {
		return getPlugin().getUserDataConfig().getConfig( plr );
	}
	
	@Override
	public void reloadConfig() {
		getConfig().addDefault(PATH_PREFIX + ".time", 60*10);
		getConfig().addDefault(PATH_PREFIX + ".amount", "2/7");

		this.AMOUNT = UtilNumber.toInt(getConfig().getString(PATH_PREFIX + ".amount").split("/")[0]);
		this.AMOUNT_PER_DAYS = UtilNumber.toInt(getConfig().getString(PATH_PREFIX + ".amount").split("/")[1]);
		this.LOOT_TIME_MILI = getConfig().getInt(PATH_PREFIX + ".time")*TimeSpan.SECOND;
	}

	@Override
	public void onDisable() {
		
	}
}
