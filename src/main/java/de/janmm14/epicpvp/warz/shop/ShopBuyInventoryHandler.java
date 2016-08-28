package de.janmm14.epicpvp.warz.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.kcore.Inventory.Inventory.InventoryCopy;
import eu.epicpvp.kcore.Util.UtilInv;

import de.janmm14.epicpvp.warz.itemrename.ItemRenameModule;

public class ShopBuyInventoryHandler {

	private final InventoryCopy inventory = new InventoryCopy( 6 * 9, "§6WarZ Shop" );
	private final ItemRenameModule renameModule;

	public ShopBuyInventoryHandler(ItemRenameModule renameModule) {
		this.renameModule = renameModule;
		setupInventorz();
	}

	public void openInventory(Player player) {
		inventory.open( player, UtilInv.getBase() );
	}

	private void setupInventorz() {
		addItem( 0, Material.WOOD_HOE, 0 );
		addItem( 1, Material.STONE_HOE, 0 );
		addItem( 2, Material.GOLD_HOE, 0 );
		addItem( 3, Material.IRON_HOE, 0 );
		addItem( 4, Material.DIAMOND_HOE, 0 );

		addItem( 9, Material.WOOD_AXE, 0 );
		addItem( 10, Material.STONE_AXE, 0 );
		addItem( 11, Material.GOLD_AXE, 0 );
		addItem( 12, Material.IRON_AXE, 0 );
		addItem( 13, Material.DIAMOND_AXE, 0 );

		addItem( 18, Material.WOOD_PICKAXE, 0 );
		addItem( 19, Material.STONE_PICKAXE, 0 );
		addItem( 20, Material.GOLD_PICKAXE, 0 );
		addItem( 21, Material.IRON_PICKAXE, 0 );
		addItem( 22, Material.DIAMOND_PICKAXE, 0 );

		addItem( 27, Material.WOOD_SPADE, 0 );
		addItem( 28, Material.STONE_SPADE, 0 );
		addItem( 29, Material.GOLD_SPADE, 0 );
		addItem( 30, Material.IRON_SPADE, 0 );
		addItem( 31, Material.DIAMOND_SPADE, 0 );

		addItem( 36, Material.BLAZE_ROD, 0 );

		addItem( 45, Material.FIREWORK_CHARGE, 0 );
		addItem( 46, Material.SLIME_BALL, 0 );
		addItem( 47, Material.MAGMA_CREAM, 0 );

		addItem( 49, Material.DIAMOND_BOOTS, 0 );
		addItem( 50, Material.DIAMOND_LEGGINGS, 0 );
		addItem( 51, Material.DIAMOND_CHESTPLATE, 0 );
		addItem( 52, Material.DIAMOND_HELMET, 0 );

		addItem( 6, Material.INK_SACK, 7, 0 );
		addItem( 15, Material.INK_SACK, 11, 0 );
		addItem( 24, Material.INK_SACK, 8, 0 );
		addItem( 33, Material.INK_SACK, 3, 0 );
		addItem( 42, Material.EGG, 0 );
		inventory.fill( Material.STAINED_GLASS_PANE, 7 );
		UtilInv.getBase().addPage( inventory );
	}

	private void addItem(int pos, Material mat, int buycraftId) {
		addItem( pos, mat, ( short ) 0, buycraftId );
	}

	private void addItem(int pos, Material material, int durability, int buycraftId) {
		ItemStack item = new ItemStack( material );
		item.setDurability( ( short ) durability );
		renameModule.renameIfNeeded( item );
		inventory.addButton( pos, new ShopLinkButton( item, buycraftId ) );
	}
}
