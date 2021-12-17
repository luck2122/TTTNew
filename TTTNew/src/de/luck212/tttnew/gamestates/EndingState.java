package de.luck212.tttnew.gamestates;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.luck212.tttnew.countdowns.EndingCountdown;
import de.luck212.tttnew.main.Main;

public class EndingState extends GameState{
	
	private EndingCountdown endingCountdown;
	
	public EndingState(Main plugin) {
		endingCountdown = new EndingCountdown(plugin);
	}

	@Override
	public void start() {
		endingCountdown.start();
		
	}

	@Override
	public void stop() {

	}
}
