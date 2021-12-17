package de.luck212.tttnew.countdowns;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import de.luck212.tttnew.gamestates.IngameState;
import de.luck212.tttnew.main.Main;
import de.luck212.tttnew.role.Role;
import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;

public class RoleCountdown extends Countdown{
	
	private int seconds = 5;
	private Main plugin;
	
	public RoleCountdown(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public void start() {
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				switch(seconds) {
				case 30:
					Bukkit.broadcastMessage(Main.PREFIX + "§7Die §6Rollen §7werden in §6" + seconds + " Sekunden §7 verteilt.");
					break;
				case 15: case 10: case 5: case 4: case 3: case 2:
					Bukkit.broadcastMessage(Main.PREFIX + "§7Noch §6" + seconds + " Sekunden §7bis zur Rollen vergabe.");
					break;
				case 1:
					Bukkit.broadcastMessage(Main.PREFIX + "§7Noch §6eine Sekunde §7 bis zur Rollen vergabe.");
					break;
				case 0: 
					stop();
					IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
					ingameState.setGrace(false);
					
					Bukkit.broadcastMessage(Main.PREFIX + "§aDie §6Rollen §awurden verteilt.");
					plugin.getRoleManager().calculateRoles();
					
					
					
					ArrayList<String> traitorPlayers = plugin.getRoleManager().getTraitorPlayers();
					for(Player current: plugin.getPlayers()) {
						Role playerRole = plugin.getRoleManager().getPlayerRole(current);
						
						if(playerRole == Role.DETECTIVE || playerRole == Role.TRAITOR)
							plugin.getRoleInventories().getPointManager().setPoints(current, 10);
						
						current.sendMessage(Main.PREFIX + "§7Deine Rolle §l" + playerRole.getChatColor() + playerRole.getName());
						current.setDisplayName(playerRole.getChatColor() + current.getName());
						
						PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + playerRole.getName() +"\", \"color\":\"" + playerRole.getColor().toString().toLowerCase()  + "\"}"), 20, 40, 20);
						((CraftPlayer) current.getPlayer()).getHandle().playerConnection.sendPacket(title);
						
						
						if(playerRole == Role.TRAITOR) 
							current.sendMessage( Main.PREFIX + "§7Die Traitor sind: §c§l" + String.join(", ", traitorPlayers));
						ingameState.updateScoreboard(current);
						
						
						// TODO 10 noch auf 2 setzen
						
						current.setLevel(0);
					}
					break;
					
					default:
						break;
				}
				Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
				Objective objective = board.registerNewObjective("abcdef", "abcdef");
				objective.setDisplaySlot(DisplaySlot.SIDEBAR);
				objective.setDisplayName("§6TTT");
				objective.getScore(" ").setScore(3);
				objective.getScore("§7Spieler: §6§" + plugin.getPlayers().size()).setScore(2);
				objective.getScore(" ").setScore(1);
				objective.getScore("§7Bis Rollen vergabe: §6" + seconds + " Sekunden").setScore(0);
				for(Player current : Bukkit.getOnlinePlayers())
					current.setScoreboard(board);
				seconds--;
			}
		}, 0, 20);
	}

	
	@Override
	public void stop() {
		Bukkit.getScheduler().cancelTask(taskID);
		
	}

}
