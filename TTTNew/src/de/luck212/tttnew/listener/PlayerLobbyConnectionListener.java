package de.luck212.tttnew.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import de.luck212.tttnew.countdowns.LobbyCountDown;
import de.luck212.tttnew.gamestates.LobbyState;
import de.luck212.tttnew.main.Main;
import de.luck212.tttnew.util.ConfigLocationUtil;
import de.luck212.tttnew.util.ItemBuilder;
import de.luck212.tttnew.voting.Voting;

public class PlayerLobbyConnectionListener implements Listener{
	
	public static final String VOTING_ITEM_NAME = "§6§lVoting-Menu";
	
	private Main plugin;
	private ItemStack voteItem;
	
	public PlayerLobbyConnectionListener(Main plugin) {
		this.plugin = plugin;
		voteItem = new ItemBuilder(Material.NETHER_STAR).setDisplayName(VOTING_ITEM_NAME).build();
	}
	
	@EventHandler
	public void handlePlayerJoin(PlayerJoinEvent event) {
		if(!(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)) return;
		Player player = event.getPlayer();
		plugin.getPlayers().add(player);
		event.setJoinMessage(Main.PREFIX + "§a" + player.getDisplayName() + " §7ist dem Spiel beigetreten. [" + 
							plugin.getPlayers().size() + "/" + LobbyState.MAX_PLAYERS + "]" );
		
		player.getInventory().clear();
		player.getInventory().setChestplate(null);
		player.getInventory().setHelmet(null);
		player.getInventory().setItem(4, voteItem);
		player.setGameMode(GameMode.SURVIVAL);
		for(Player current : Bukkit.getOnlinePlayers()) {
			current.showPlayer(player);
			player.showPlayer(current);
		}
				
		
		
		ConfigLocationUtil locationUtil = new ConfigLocationUtil(plugin, "Lobby");
		if(locationUtil.loadLocation() != null) {
			player.teleport(locationUtil.loadLocation());
		}else
			Bukkit.getConsoleSender().sendMessage("§cDie Lobby-Location wurde noch nicht gesetzt!");
		
		LobbyState lobbyState= (LobbyState)plugin.getGameStateManager().getCurrentGameState();
		LobbyCountDown countdown = lobbyState.getCountdown(); 
		if(plugin.getPlayers().size() >= LobbyState.MIN_PLAYERS) {
			if(!countdown.isRunning()) {
				countdown.stopIdle();
				countdown.start();
			}
		}
		lobbyState.updateScoreBoard();
	}
	
	@EventHandler
	
	public void handlePlayerQuit(PlayerQuitEvent event) {
		if(!(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)) {
			Player player = event.getPlayer();
			event.setQuitMessage(Main.PREFIX + "§c" + player.getDisplayName() + " §7hat das Spiel verlassen.");
			return;
		}
		Player player = event.getPlayer();
		plugin.getPlayers().remove(player);
		event.setQuitMessage(Main.PREFIX + "§c" + player.getDisplayName() + " §7hat das Spiel verlassen. [" + 
							plugin.getPlayers().size() + "/" + LobbyState.MAX_PLAYERS + "]" );
		
		LobbyState lobbyState= (LobbyState)plugin.getGameStateManager().getCurrentGameState();
		LobbyCountDown countdown = lobbyState.getCountdown(); 
		if(plugin.getPlayers().size() < LobbyState.MIN_PLAYERS) {
			if(countdown.isRunning()) {
				countdown.stop();
				countdown.startIdle();
			}
		}
		lobbyState.updateScoreBoard();
	}

}
