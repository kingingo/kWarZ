package de.janmm14.epicpvp.warz.shop;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.epicpvp.kcore.Inventory.Inventory.InventoryCopy;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;

public class ShopBuyInventoryHandler {

	private final InventoryCopy inventory = new InventoryCopy( 6 * 9, "§6WarZ Shop" );

	public ShopBuyInventoryHandler() {
		setupInventorz();
	}

	public void openInventory(Player player) {
		inventory.open( player, UtilInv.getBase() );
	}

	private void setupInventorz() {
//		inventory.fillBorder( Material.STAINED_GLASS_PANE, 7 );

		addItem( 5 - 1, 0, Material.NETHER_STAR, "§cWar§lZ§7 - §aShop" );
		UtilItem.addEnchantmentGlow( inventory.getItem( 5 - 1 ) );

		addItem( 9 * 2 + 3 - 1, 2034877, Material.DIAMOND_AXE, "§eBarret 50cal §7[§bEinzelitem§7]" );
		addItem( 9 * 2 + 4 - 1, 2034886, Material.DIAMOND_CHESTPLATE, "§eDiamantrüstung §7[§bEinzelitems§7]" );
		addItem( 9 * 2 + 5 - 1, 2034953, Material.IRON_INGOT, "§eFight-Pack §7[§bPaket§7]", "§7> §eMit diesem Paket erhälst du ein komplettes \"Fight\"-Kit,", "  §ebestehend aus einer §6Diamantrüstung§e, einer §6Barrett 50cal", "  §eund einer §6Ak 47§e inkl. dazugehöriger §6Munition§e und §6Essen§e." );
		addItem( 9 * 2 + 6 - 1, 2034964, Material.GOLD_INGOT, "§eWeapon-Pack §7[§bPaket§7]", "§7> §eMit diesem Paket erhälst du §6jede Tier-IV-Waffe§e in WarZ.", "  §7[§eAk47§7, §eDesert Eagle§7, §eBarret 50cal§7, §eSpas-12§7]" );
		addItem( 9 * 2 + 7 - 1, 2034891, Material.IRON_BOOTS, "§eHalf-Fall-Damage §7[§bPerk§7]", "§7> §eDein Fallschaden §6halbiert§e sich automatisch." );
		addItem( 9 * 3 + 3 - 1, 2034899, Material.SKULL_ITEM, 3, "§eHead-Dropper §7[§bPerk§7]", "§7> §eDer Kopf deiner Gegner dropt, sobald du sie tötest." );
		addItem( 9 * 3 + 4 - 1, 2034901, Material.POTION, "§eNo-Water-Damage §7[§bPerk§7]", "§7> §eDu erhälst im Wasser §6keinen §eSchaden." );
		addItem( 9 * 3 + 5 - 1, 2034912, Material.DIAMOND_SWORD, "§eOne-Hit §7[§bPerk§7]", "§7> §eDu kannst jeden Zombie mit §6einem §eSchlag töten.", "  §eMit jedem Schwert." );
		addItem( 9 * 3 + 6 - 1, 2034925, Material.ANVIL, "§e/Repair §7[§bRechte§7]" );
		addItem( 9 * 3 + 7 - 1, 2034889, Material.MAP, "§eMap erweiterung" );
		addItem( 9 * 4 + 3 - 1, 2063397, Material.CHEST, "§eAlle Waffen" );
		addItem( 9 * 4 + 4 - 1, 2063404, Material.GOLD_AXE, "§eTier V Sniper" );
		addItem( 9 * 4 + 5 - 1, 2063408, Material.GOLD_PICKAXE, "§eTier V Shotgun" );
		addItem( 9 * 4 + 6 - 1, 2063413, Material.GOLD_HOE, "§eTier V Autogun" );
		addItem( 9 * 4 + 7 - 1, 2063416, Material.GOLD_SPADE, "§eTier V Pistol" );
		addItem( 9 * 5 + 3 - 1, 2063422, Material.getMaterial(351), 11, "§eMunitions Paket" );
		
		
		UtilInv.getBase().addPage( inventory );
	}

	private void addItem(int pos, int buycraftId, Material mat, String displayName, String... lore) {
		addItem( pos, buycraftId, mat, ( short ) 0, displayName, lore );
	}

	private void addItem(int pos, int buycraftId, Material material, int durability, String displayName, String... lore) {
		ItemStack item = new ItemStack( material );
		item.setDurability( ( short ) durability );
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName( displayName );
		meta.setLore( Arrays.asList( lore ) );
		item.setItemMeta( meta );
		if ( buycraftId > 0 ) {
			inventory.addButton( pos, new ShopLinkButton( item, buycraftId ) );
		} else {
			inventory.setItem( pos, item );
		}
	}
}
