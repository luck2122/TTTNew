package de.luck212.tttnew.role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import de.luck212.tttnew.main.Main;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityEquipment;

public class RoleManager {
	
	private Main plugin;
	private HashMap<String, Role> playerRoles;
	private ArrayList<Player> players;
	private ArrayList<String> traitorPlayers;
	
	private int traitors, detectives, innocents;
	
	public RoleManager(Main plugin) {
		this.plugin = plugin;
		playerRoles = new HashMap<>();
		players = plugin.getPlayers();
		traitorPlayers = new ArrayList<String>();
	}
	
	public void calculateRoles() {
		int playerSize = players.size();
//		traitors = (int) Math.round(Math.log(playerSize) * 1.2);
//		detectives = (int) Math.round(Math.log(playerSize) * 0.5);
		traitors = 1;
		detectives = 1;
		innocents = playerSize - traitors - detectives;
		
		System.out.println("Traitors: " + traitors);
		System.out.println("Detectives: " + detectives);
		System.out.println("Innocents: " + innocents);
		
		Collections.shuffle(players);
		
		int counter = 0;
		for(int i = counter; i < traitors; i++) {
			playerRoles.put(players.get(i).getName(), Role.TRAITOR);
			traitorPlayers.add(players.get(i).getName());
		}
		counter += traitors;
		
		for(int i = counter; i < detectives + counter; i++) {
			playerRoles.put(players.get(i).getName(), Role.DETECTIVE);
		}
		counter += detectives;
		
		for(int i = counter; i < innocents + counter; i++) {
			playerRoles.put(players.get(i).getName(), Role.INNOCENT);
		}
		
		for(Player current: players) {
			switch(getPlayerRole(current)) {
			case TRAITOR :
				for(Player others : players)
					setFakeArmor(others, current.getEntityId(), (getPlayerRole(others) != Role.TRAITOR) ? Color.GREEN : Color.RED);
				current.getInventory().setItem(4, plugin.getRoleInventories().getTraitorItem());
				break;
			case DETECTIVE:
				setArmor(current, Color.BLUE);
				current.getInventory().setItem(4, plugin.getRoleInventories().getDetectiveItem());
				break;
				
			case INNOCENT:
				setArmor(current, Color.GREEN);
				break;
				
			default:
				break;
			}
		}
	}
	
	public void setArmor(Player player, Color color) {
		player.getInventory().setChestplate(getColoredChestplate(color));
	}
	
	public void setFakeArmor(Player player, int entityID, Color color) {
		ItemStack armor = getColoredChestplate(color);
		
		final int CHESTPLAYSLOT = 3;
		PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(entityID, CHESTPLAYSLOT, CraftItemStack.asNMSCopy(armor));
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
	
	private ItemStack getColoredChestplate(Color color) {
		ItemStack armor = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta armorMeta = (LeatherArmorMeta)armor.getItemMeta();
		armorMeta.setColor(color);
		armor.setItemMeta(armorMeta);
		return armor;
	}
	
	public Role getPlayerRole(Player player) {
		return playerRoles.get(player.getName());
	}
	
	public ArrayList<String> getTraitorPlayers() {
		return traitorPlayers;
	}

}
