package de.luck212.tttnew.gamestates;

import de.luck212.tttnew.countdowns.RoleCountdown;
import de.luck212.tttnew.main.Main;
import de.luck212.tttnew.role.Role;
import de.luck212.tttnew.voting.Map;
import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;

public class IngameState extends GameState {

	private Main plugin;
	private Map map;
	private ArrayList<Player> players, spectators;
	private RoleCountdown roleCountDown;
	public boolean grace;
	private PacketPlayOutTitle title;

	private Role winningRole;

	public IngameState(Main plugin) {
		this.plugin = plugin;
		roleCountDown = new RoleCountdown(plugin);
		spectators = new ArrayList<Player>();
	}

	@Override
	public void start() {
		grace = true;

		Collections.shuffle(plugin.getPlayers());
		players = plugin.getPlayers();

		map = plugin.getVoting().getWinnerMap();
		map.load();
		for (int i = 0; i < players.size(); i++) {
			players.get(i).setLevel(0);
			players.get(i).teleport(map.getSpawnLocations()[i]);
		}

		for (Player current : players) {
			current.setHealth(20);
			current.setFoodLevel(20);
			current.getInventory().clear();
			current.setGameMode(GameMode.SURVIVAL);
			plugin.getGameProtectionListener().getBuildModePlayers().remove(current.getName());
			updateScoreboard(current);
		}

		roleCountDown.start();
	}
	
	public void updateScoreboard(Player player) {
		
		
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = board.registerNewObjective("abcd", "abcd");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("§6TTT");
		objective.getScore(" ").setScore(3);
		if(!grace) {
			Role role = plugin.getRoleManager().getPlayerRole(player);
			objective.getScore("§7Deine Rolle: " + role.getChatColor() + role.getName()).setScore(2);
		}else
			objective.getScore("§7Deine Rolle ist noch nicht bekannt").setScore(2);
		
		objective.getScore(" ").setScore(1);
		objective.getScore("§7Verbleibende Spieler: §6" + plugin.getPlayers().size()).setScore(0);
		player.setScoreboard(board);
	}

	public void checkGameEnding() {
		if(plugin.getRoleManager().getTraitorPlayers().size() <= 0) {
			winningRole = Role.INNOCENT;
			plugin.getGameStateManager().setGameState(ENDING_STATE);
		}else if(plugin.getRoleManager().getTraitorPlayers().size() == plugin.getPlayers().size());{
			winningRole = Role.TRAITOR;
			plugin.getGameStateManager().setGameState(ENDING_STATE);
		}
	}

	public void addSpectator(Player player) {
		spectators.add(player);
		player.setGameMode(GameMode.CREATIVE);
		if (map == null) {
			System.out.println("Map ist nicht definiert.");
		} else {
			System.out.println(map.getName());
			map = plugin.getVoting().getWinnerMap();
			map.load();
			player.teleport(map.getSpectatorLocation());

			for (Player current : Bukkit.getOnlinePlayers()) {
				current.hidePlayer(player);
			}
		}

	}

	@Override
	public void stop() {
		Bukkit.broadcastMessage(Main.PREFIX + "§6 Sieger: " + winningRole.getChatColor() + winningRole.getName());

		for(Player current : Bukkit.getOnlinePlayers()){
			if(winningRole == Role.TRAITOR)
				title = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + winningRole.getName() +"\", \"color\":\"red\"}"), 20, 40, 20);
			else if(winningRole == Role.DETECTIVE)
				title = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + winningRole.getName() +"\", \"color\":\"blue\"}"), 20, 40, 20);
			else
				title = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + winningRole.getName() +"\", \"color\":\"green\"}"), 20, 40, 20);
			((CraftPlayer) current.getPlayer()).getHandle().playerConnection.sendPacket(title);
			for(int i = 0; i < players.size(); i++)
				current.teleport(map.getSpawnLocations()[i]);
		}
	}

	public void setGrace(boolean grace) {
		this.grace = grace;
	}

	public boolean isInGrace() {
		return grace;
	}

	public ArrayList<Player> getSpectators() {
		return spectators;
	}
	
	public Map getMap() {
		return map;
	}

}
