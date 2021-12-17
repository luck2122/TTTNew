package de.luck212.tttnew.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.luck212.tttnew.gamestates.LobbyState;
import de.luck212.tttnew.listener.GameProgressListener;
import de.luck212.tttnew.listener.GameProtectionListener;
import de.luck212.tttnew.main.Main;

public class BuildCommand implements CommandExecutor {

	private Main plugin;

	public BuildCommand(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("ttt.build")) {
				if (args.length == 0) {
					if (plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
						GameProtectionListener gameProtectionListener = plugin.getGameProtectionListener();
						if (!gameProtectionListener.getBuildModePlayers().contains(player.getName())) {
							gameProtectionListener.getBuildModePlayers().add(player.getName());
							player.sendMessage(Main.PREFIX + "§6Du bist nun im §6Baumodus§7.");
						} else {
							gameProtectionListener.getBuildModePlayers().remove(player.getName());
							player.sendMessage(Main.PREFIX + "§7Du bist nun nicht mehr im §6Baumodus§7.");
						}
					} else 
						player.sendMessage(Main.PREFIX + "§cDu kannst nur im §6LobbyState §cin den Baumodus");

				} else
					player.sendMessage(Main.PREFIX + "§cBitte benutze §6/build §c!");
			} else
				player.sendMessage(Main.PERMISSION);

		}
		return false;
	}

}
