package de.janmm14.epicpvp.warz.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.kcore.Inventory.Inventory.InventoryCopy;
import eu.epicpvp.kcore.Util.UtilInv;

import de.janmm14.epicpvp.warz.itemrename.ItemRenameModule;

public class ShopModuleListener implements Listener {

	private final ShopModule module;
	private final InventoryCopy inventory;
	private final ItemRenameModule renameModule;

	public ShopModuleListener(ShopModule module) {
		this.module = module;
		this.inventory = new InventoryCopy( 6 * 9, "§6WarZ Shop" );
		renameModule = module.getModuleManager().getModule( ItemRenameModule.class );

		addButton( 0, Material.WOOD_HOE, 0 );
		addButton( 1, Material.STONE_HOE, 0 );
		addButton( 2, Material.GOLD_HOE, 0 );
		addButton( 3, Material.IRON_HOE, 0 );
		addButton( 4, Material.DIAMOND_HOE, 0 );

		addButton( 9, Material.WOOD_AXE, 0 );
		addButton( 10, Material.STONE_AXE, 0 );
		addButton( 11, Material.GOLD_AXE, 0 );
		addButton( 12, Material.IRON_AXE, 0 );
		addButton( 13, Material.DIAMOND_AXE, 0 );

		addButton( 18, Material.WOOD_PICKAXE, 0 );
		addButton( 19, Material.STONE_PICKAXE, 0 );
		addButton( 20, Material.GOLD_PICKAXE, 0 );
		addButton( 21, Material.IRON_PICKAXE, 0 );
		addButton( 22, Material.DIAMOND_PICKAXE, 0 );

		addButton( 27, Material.WOOD_SPADE, 0 );
		addButton( 28, Material.STONE_SPADE, 0 );
		addButton( 29, Material.GOLD_SPADE, 0 );
		addButton( 30, Material.IRON_SPADE, 0 );
		addButton( 31, Material.DIAMOND_SPADE, 0 );

		addButton( 36, Material.BLAZE_ROD, 0 );

		addButton( 45, Material.FIREWORK_CHARGE, 0 );
		addButton( 46, Material.SLIME_BALL, 0 );
		addButton( 47, Material.MAGMA_CREAM, 0 );

		addButton( 49, Material.DIAMOND_BOOTS, 0 );
		addButton( 50, Material.DIAMOND_LEGGINGS, 0 );
		addButton( 51, Material.DIAMOND_CHESTPLATE, 0 );
		addButton( 52, Material.DIAMOND_HELMET, 0 );

		addButton( 6, Material.INK_SACK, 7, 0 );
		addButton( 15, Material.INK_SACK, 11, 0 );
		addButton( 24, Material.INK_SACK, 8, 0 );
		addButton( 33, Material.INK_SACK, 3, 0 );
		addButton( 42, Material.EGG, 0 );
		inventory.fill( Material.STAINED_GLASS_PANE, 7 );
		UtilInv.getBase().addPage( inventory );
	}

	public void addButton(int pos, Material mat, int buycraftId) {
		addButton( pos, mat, ( short ) 0, buycraftId );
	}

	public void addButton(int pos, Material material, int durability, int buycraftId) {
		ItemStack is = new ItemStack( material );
		is.setDurability( ( short ) durability );
		renameModule.renameIfNeeded( is );
		inventory.addButton( pos, new ShopLinkButton( is, buycraftId ) );
	}

	public void openInventory(Player player) {
		inventory.open( player, UtilInv.getBase() );
	}
}
