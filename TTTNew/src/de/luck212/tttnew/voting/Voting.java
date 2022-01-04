package de.luck212.tttnew.voting;

import de.luck212.tttnew.main.Main;
import de.luck212.tttnew.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Voting {
	
	public static final int MAP_AMOUNT = 2;
	public static final String VOTING_INVENTORY_TITLE = "§6Voting";
	
	private Main plugin;
	private ArrayList<Map> maps;
	private Map[] votingMaps;
	private int[] votingInventoryOrder = new int[] {3, 5};
	private HashMap<String, Integer> playerVotes;
	private Inventory votingInventory;
	
	public Voting(Main plugin, ArrayList<Map> maps) {
		this.plugin = plugin;
		this.maps = maps;
		votingMaps = new Map[MAP_AMOUNT];
		playerVotes = new HashMap<String, Integer>();
		
		chooseRandomMaps();
		initVotingInventory();
	}
	
	private void chooseRandomMaps() {
		for(int i = 0; i < votingMaps.length; i++) {
			Collections.shuffle(maps);
			votingMaps[i] = maps.remove(0);
		}
	}
	
	public void initVotingInventory() {
		votingInventory = Bukkit.createInventory(null, 9*1, VOTING_INVENTORY_TITLE);
		for(int i = 0; i < votingMaps.length; i++) {
			Map currentMap = votingMaps[i];
			votingInventory.setItem(votingInventoryOrder[i], new ItemBuilder(Material.PAPER)
															.setDisplayName("§6" + currentMap.getName() + "§c - §c§l" + currentMap.getVotes() + " Votes ")
															.setLore(" ", "§7Erbauer: §a" + currentMap.getBuilder()).build());
		}
	}
	
	public Map getWinnerMap() {
		Map winnerMap = votingMaps[0];
		for(int i = 1; i < votingMaps.length; i++) {
			if(votingMaps[i].getVotes() >= winnerMap.getVotes()) 
				winnerMap = votingMaps[i];
		}
		winnerMap.load();
		return winnerMap;
	}
	
	public void vote(Player player, int votingMap) {
		if(!(playerVotes.containsKey(player.getName()))) {
			votingMaps[votingMap].addVote();
			player.closeInventory();
			player.sendMessage(Main.PREFIX + "§aDu hast für die Map §6" + votingMaps[votingMap].getName() + "§a abgestimmt");
			playerVotes.put(player.getName(), votingMap);
			initVotingInventory();
		}else
			player.sendMessage(Main.PREFIX + "§cDu hast bereits gevotet.");
	}
	
	public Map[] getVotingMaps() {
		return votingMaps;
	}
	
	public HashMap<String, Integer> getPlayerVotes() {
		return playerVotes;
	}
	
	public Inventory getVotingInventory() {
		return votingInventory;
	}
	
	public int[] getVotingInventoryOrder() {
		return votingInventoryOrder;
	}

}
