package de.luck212.tttnew.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.luck212.tttnew.gamestates.LobbyState;
import de.luck212.tttnew.main.Main;

public class StartCommand implements CommandExecutor{
	
	
	private static final int START_SECONDS = 5;
	private Main plugin;
	public StartCommand(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(player.hasPermission("ttt.start")) {
				if(args.length == 0) {
					if(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
						LobbyState lobbyState = (LobbyState)plugin.getGameStateManager().getCurrentGameState();
						if(lobbyState.getCountdown().isRunning() && (lobbyState.getCountdown().getSeconds() > START_SECONDS)) {
							lobbyState.getCountdown().setSeconds(START_SECONDS);
							player.sendMessage(Main.PREFIX + "§aDer Spielstart wurde beschleunigt.");
						}else
							player.sendMessage(Main.PREFIX + "§cDas Spiel ist bereits gestartet.");
					}else
						player.sendMessage(Main.PREFIX + "§cDas Spiel ist bereits gestartet.");
				}else
					player.sendMessage(Main.PREFIX + "§cBitte benutze §6/start§c!");
			}else
				player.sendMessage(Main.PERMISSION);
		}else
			sender.sendMessage("§cDu bist kein Spieler!");
		return false;
	}

}
