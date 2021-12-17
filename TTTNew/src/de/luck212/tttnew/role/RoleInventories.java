package de.luck212.tttnew.role;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import de.luck212.tttnew.main.Main;
import de.luck212.tttnew.util.ItemBuilder;

public class RoleInventories implements Listener {

	public static final String TRAITOR_ITEM = "§cTraitor-Shop", DETECTIVE_ITEM = "§bDetective-Shop",
			TRAITOR_CREEPER = "§cCreeper-Pfeile", TRAITOR_TICKET = "§cTraitor-Ticket",
			DETECTIVE_HEALSTATION = "§bDetective-Healstation", TRAITOR_SHOP_TITLE = RoleInventories.TRAITOR_ITEM,
			DETECTIVE_SHOP_TITLE = RoleInventories.DETECTIVE_ITEM, TRAITOR_TARNKAPPE = "§cTarnkappe",
			DETECTIVE_ONE_SHOT_BOW = "§bOne-Shot";

	private ItemStack traitorItem, detectiveItem;
	private Inventory traitorShop, detectiveShop;
	private PointManager pointManager;
	private ArrayList<String> lores;

	public RoleInventories() {
		traitorItem = new ItemBuilder(Material.BOW).setDisplayName(RoleInventories.TRAITOR_ITEM).build();
		detectiveItem = new ItemBuilder(Material.EMERALD).setDisplayName(RoleInventories.DETECTIVE_ITEM).build();

		traitorShop = Bukkit.createInventory(null, 9 * 1, TRAITOR_SHOP_TITLE);
		detectiveShop = Bukkit.createInventory(null, 9 * 1, DETECTIVE_SHOP_TITLE);
		pointManager = new PointManager();
		fillInventories();
	}

	@EventHandler
	public void handleShopBuy(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;
		Player player = (Player) event.getWhoClicked();

		ItemStack item = event.getCurrentItem();
		if (item == null || item.getItemMeta() == null)
			return;

		if (event.getInventory().getTitle().equals(TRAITOR_SHOP_TITLE)) {
			event.setCancelled(true);
			switch (item.getItemMeta().getDisplayName()) {
			case TRAITOR_CREEPER:
				if (!pointManager.removePoints(player, 2)) {
					player.sendMessage(Main.PREFIX + "§cDu hast zu wenig Punkte.");
					return;
				}

				// Creeper Pfeile noch ausprobieren
				// ItemStack test = new ItemStack(Material.MONSTER_EGG, 1,
				// org.bukkit.entity.EntityType.CREEPER.getTypeId());

				ItemStack creeperArrows = new ItemBuilder(Material.SULPHUR).setDisplayName(TRAITOR_CREEPER).build();
				creeperArrows.setAmount(3);
				player.getInventory().addItem(creeperArrows);
				break;
			case TRAITOR_TICKET:
				if (!pointManager.removePoints(player, 4)) {
					player.sendMessage(Main.PREFIX + "§cDu hast zu wenig Punkte.");
					return;
				}
				player.getInventory()
						.addItem(new ItemBuilder(Material.STAINED_GLASS).setDisplayName(TRAITOR_TICKET).build());
				break;
			case TRAITOR_TARNKAPPE:
				if (!pointManager.removePoints(player, 6)) {
					player.sendMessage(Main.PREFIX + "§cDu hast zu wenig Punkte");
					return;
				}
				ItemStack skull = new ItemStack(397, 1, (short) 3);
				SkullMeta meta = (SkullMeta) skull.getItemMeta();
				meta.setOwner("Steve");
				meta.setDisplayName(TRAITOR_TARNKAPPE);
				skull.setItemMeta(meta);
				player.getInventory().addItem(skull);
				break;
			default:
				break;
			}
		} else if (event.getInventory().getTitle().equals(DETECTIVE_SHOP_TITLE)) {
			event.setCancelled(true);
			switch (item.getItemMeta().getDisplayName()) {
			case DETECTIVE_HEALSTATION:
				if (!pointManager.removePoints(player, 3)) {
					player.sendMessage(Main.PREFIX + "§cDu hast zu wenig Punkte.");
					return;
				}
				player.getInventory()
						.addItem(new ItemBuilder(Material.BEACON).setDisplayName(DETECTIVE_HEALSTATION).build());
				break;
			case DETECTIVE_ONE_SHOT_BOW:
				if (!pointManager.removePoints(player, 5)) {
					player.sendMessage(Main.PREFIX + "§cDu hast zu wenig Punkte");
					return;
				}
				ItemStack oneShotBow = new ItemStack(Material.BOW, 1);
				ItemMeta bowMeta = oneShotBow.getItemMeta();
				bowMeta.addEnchant(Enchantment.ARROW_DAMAGE, 10000, false);
				bowMeta.setDisplayName(DETECTIVE_ONE_SHOT_BOW);
				oneShotBow.setItemMeta(bowMeta);
				oneShotBow.setDurability((short) 1);
				player.getInventory().addItem(oneShotBow);
				break;

			default:
				break;
			}
		}
	}

	@EventHandler
	public void handleItemOpenerClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		Player player = event.getPlayer();
		ItemStack item = player.getItemInHand();
		if (item.getItemMeta() == null)
			return;
		if (item.getItemMeta().getDisplayName() == null)
			return;
		switch (item.getItemMeta().getDisplayName()) {
		case TRAITOR_ITEM:
			player.openInventory(traitorShop);
			break;
		case DETECTIVE_ITEM:
			player.openInventory(detectiveShop);
			break;

		default:
			break;
		}
	}

	private void fillInventories() {
		lores = new ArrayList<String>();
		traitorShop.setItem(3,
				new ItemBuilder(Material.SULPHUR).setDisplayName(TRAITOR_CREEPER)
						.setLore("§7Kauft 3 Creeper Pfeiler", "Diese Spawnen beim Auftreffen des Pfeiles einen Creeper")
						.build());
		traitorShop.setItem(5,
				new ItemBuilder(Material.STAINED_GLASS).setDisplayName(TRAITOR_TICKET)
						.setLore("§7Gibt ein Traitor Ticket, durch den man",
								"§7mit einer Warscheinlichkeit von 75% im Tester", "§7als Innocent erkannt wird")
						.build());
		detectiveShop.setItem(3, new ItemBuilder(Material.BEACON).setDisplayName(DETECTIVE_HEALSTATION)
				.setLore("§7Setzt eine Healstation, der Spieler heilt").build());

		lores.add("§7Werde fuer 45 Sekunden Unsichtbar");
		ItemStack skull = new ItemStack(397, 1, (short) 3);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setDisplayName(TRAITOR_TARNKAPPE);
		meta.setOwner("Steve");
		meta.setLore(lores);
		skull.setItemMeta(meta);
		traitorShop.setItem(8, skull);
		lores.clear();

		lores.add("§7Toete jeden Spieler mit einem Schuss");
		ItemStack oneShotBow = new ItemStack(Material.BOW, 1);
		ItemMeta bowMeta = oneShotBow.getItemMeta();
		bowMeta.setDisplayName(DETECTIVE_ONE_SHOT_BOW);
		bowMeta.setLore(lores);
		oneShotBow.setItemMeta(bowMeta);
		detectiveShop.setItem(5, oneShotBow);
		lores.clear();
	}

	public static boolean removeMaterialItem(Player player, Material material) {
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			if (player.getInventory().getContents()[i] != null) {

				if (player.getInventory().getContents()[i].getType() == material) {
					if (player.getInventory().getContents()[i].getAmount() <= 1)
						player.getInventory().clear(i);
					else
						player.getInventory().getContents()[i]
								.setAmount(player.getInventory().getContents()[i].getAmount() - 1);
					player.updateInventory();
					return true;
				} else
					return false;
			}
		}
		return false;
	}

	public ItemStack getTraitorItem() {
		return traitorItem;
	}

	public ItemStack getDetectiveItem() {
		return detectiveItem;
	}

	public Inventory getTraitorShop() {
		return traitorShop;
	}

	public Inventory getDetectiveShop() {
		return detectiveShop;
	}

	public PointManager getPointManager() {
		return pointManager;
	}

}
