package de.janmm14.epicpvp.warz.shop;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import lombok.Getter;
import lombok.Setter;

public class ShopInventoryHolder implements InventoryHolder {

	@Getter
	@Setter
	private Inventory inventory;
}
