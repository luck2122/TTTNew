package de.luck212.tttnew.role;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class PointManager {
	
	private HashMap<String, Integer> playerPoints;
	
	public PointManager() {
		playerPoints = new HashMap<String, Integer>();
	}
	
	public void setPoints(Player player, int points) {
		playerPoints.put(player.getName(), points);
	}
	
	public void addPoints(Player player, int points) {
		if(playerPoints.containsKey(player.getName()))
			playerPoints.put(player.getName(), playerPoints.get(player.getName()) + points);
	}
	
	public boolean removePoints(Player player, int points) {
		if(!playerPoints.containsKey(player.getName())) return false;
		if(playerPoints.get(player.getName()) >= points) {
			playerPoints.put(player.getName(), playerPoints.get(player.getName()) - points);
			if(playerPoints.get(player.getName()) < 0) 
				setPoints(player, 0);
			return true;
		}
		return false;
	}

}
