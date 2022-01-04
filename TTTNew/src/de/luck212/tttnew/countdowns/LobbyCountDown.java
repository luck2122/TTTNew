package de.luck212.tttnew.countdowns;

import de.luck212.tttnew.gamestates.GameState;
import de.luck212.tttnew.gamestates.GameStateManager;
import de.luck212.tttnew.gamestates.LobbyState;
import de.luck212.tttnew.main.Main;
import de.luck212.tttnew.voting.Map;
import de.luck212.tttnew.voting.Voting;
import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

public class LobbyCountDown extends Countdown{
	
	private static final int COUNTDOWN_TIME = 60, IDLE_TIME = 15;
	
	private int seconds;
	private boolean isRunning;
	private int idleID;
	private boolean isIdling;
	private GameStateManager gameStateManager;
	 public LobbyCountDown(GameStateManager gameStateManager) {
		this.gameStateManager = gameStateManager;
		seconds = COUNTDOWN_TIME;
	}

	@Override
	public void start() {
		isRunning = true;
		ArrayList<Player> playerList = gameStateManager.getPlugin().getPlayers();
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(gameStateManager.getPlugin(), new Runnable() {
			
			@Override
			public void run() {
				switch(seconds) {
				case 60: case 45: case 30: case 15: case 3: case 2:
					Bukkit.broadcastMessage(Main.PREFIX + "§7Das Spiel startet in §a" + seconds + " Sekunden§7.");
					
					if(seconds == 3) {
						Voting voting = gameStateManager.getPlugin().getVoting();
						Map winningMap;
						if(voting != null) {
							winningMap = voting.getWinnerMap();
						}else {
							ArrayList<Map> maps = gameStateManager.getPlugin().getMaps();
							Collections.shuffle(maps);
							winningMap = maps.get(0);
						}
						Bukkit.broadcastMessage(Main.PREFIX + "§7Sieger des Votings: §6" + winningMap.getName());
						winningMap.load();
						for(Player current : Bukkit.getOnlinePlayers())
							current.getInventory().clear();
					}
					
					break;
				case 1:
					Voting voting = gameStateManager.getPlugin().getVoting();
					PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + voting.getWinnerMap().getName() +"\", \"color\":\"green\"}"), 20, 40, 20);	
					PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\":\" Von: " + voting.getWinnerMap().getBuilder() +"\", \"color\": \"yellow\"}"), 20, 40, 20);
					
					
					for(int i = 0; i < playerList.size(); i++) {
						Player player = playerList.get(i);
						((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(title);
				        ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(subtitle);
					}
					Bukkit.broadcastMessage(Main.PREFIX + "§7Das Spiel startet in §aeiner Sekunde§7.");
					break;
				case 0:
					gameStateManager.setGameState(GameState.INGAME_STATE);
					stop();
					break;
					
					default:
						break;
				}
				if(gameStateManager.getCurrentGameState() instanceof LobbyState)
					((LobbyState) gameStateManager.getCurrentGameState()).updateScoreBoard();
				
				seconds--;
				
				for(Player current : Bukkit.getOnlinePlayers()) {
					current.setLevel(seconds);
				
				
				
					switch(seconds){
					case 60: case 30: case 15: case 3: case 2: case 1:
						current.playSound(current.getLocation(), Sound.NOTE_BASS, 0.2f, 1.4f);
						break;
						
						default:
							break;
					}
					
				}
			}
		}, 0, 20);
	}

	@Override
	public void stop() {
		if(isRunning) {
			Bukkit.getScheduler().cancelTask(taskID);
			isRunning = false;
			seconds = COUNTDOWN_TIME;
			if(gameStateManager.getCurrentGameState() instanceof LobbyState)
				((LobbyState) gameStateManager.getCurrentGameState()).updateScoreBoard();
		}
	}
	
	public void startIdle() {
		isIdling = true;
		idleID = Bukkit.getScheduler().scheduleSyncRepeatingTask(gameStateManager.getPlugin(), new Runnable() {
			
			@Override
			public void run() {
				Bukkit.broadcastMessage(Main.PREFIX + "§7Bis zum Spielstart fehlen noch §6" + 
										(LobbyState.MIN_PLAYERS - gameStateManager.getPlugin().getPlayers().size()) + 
										" Spieler§7.");
				
			}
		}, 0, 20 * IDLE_TIME);
	}
	
	public void stopIdle() {
		if(isIdling) {
			Bukkit.getScheduler().cancelTask(idleID);
			isIdling = false;
		}
	}
	
	public int getSeconds() {
		return seconds;
	}
	
	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	
	public boolean isRunning() {
		return isRunning;
	}


	
	
}
