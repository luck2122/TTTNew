package de.luck212.tttnew.countdowns;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.luck212.tttnew.main.Main;

public class EndingCountdown extends Countdown{
	
	private static final int ENDING_SECONDS = 15;
	
	private Main plugin;
	private int seconds;
	
	public EndingCountdown(Main plugin) {
		this.plugin = plugin;
		seconds = ENDING_SECONDS;
	}

	@Override
	public void start() {
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				switch (seconds) {
				case 10: case 5: case 3: case 2:
					Bukkit.broadcastMessage(Main.PREFIX + "§7Der Server stoppt in §6" + seconds + " Sekunden§7.");
					break;
				case 1:
					Bukkit.broadcastMessage(Main.PREFIX + "§7Der Server stoppt in §6einer Sekunde§7.");
					break;
				case 0:
					Bukkit.broadcastMessage(Main.PREFIX + "§7Der Server stoppt nun!");
					plugin.getGameStateManager().getCurrentGameState().stop();
					stop();
					break;
					
				default:
					break;
				}
				seconds--;
			}
		}, 0, 20);
		
	}

	@Override
	public void stop() {
		Bukkit.getScheduler().cancelTask(taskID);
		for(Player current : Bukkit.getOnlinePlayers()) {
			current.kickPlayer("Das Spiel hat geendet.");
		}
	}
	
	

}
