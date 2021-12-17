package de.luck212.tttnew.gamestates;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import de.luck212.tttnew.countdowns.LobbyCountDown;
import de.luck212.tttnew.main.Main;

public class LobbyState extends GameState{

	private Main plugin;
	public static final int MIN_PLAYERS = 2,
							MAX_PLAYERS = 9;
	
	private LobbyCountDown countdown;
	
	public LobbyState(Main plugin, GameStateManager gameStateManager) {
		this.plugin = plugin;
		countdown = new LobbyCountDown(gameStateManager);
	}
	
	public void updateScoreBoard() {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = board.registerNewObjective("abcde", "abcde");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("§6TTT");
		objective.getScore(" ").setScore(3);
		objective.getScore("§7Spieler: §6§" + plugin.getPlayers().size()).setScore(2);
		objective.getScore(" ").setScore(1);
		objective.getScore("§7Bis Start: §6" + countdown.getSeconds() + " Sekunden").setScore(0);
		for(Player current : Bukkit.getOnlinePlayers())
			current.setScoreboard(board);
	}
	
	@Override
	public void start() {
		countdown.startIdle();
	}

	@Override
	public void stop() {
		
	}
	
	public LobbyCountDown getCountdown() {
		return countdown;
	}

}
