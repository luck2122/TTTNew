package de.luck212.tttnew.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.luck212.tttnew.gamestates.LobbyState;
import de.luck212.tttnew.main.Main;
import de.luck212.tttnew.role.RoleInventories;
import de.luck212.tttnew.voting.Voting;

public class VotingListener implements Listener{
	
	private Voting voting;
	public Main plugin;
	
	public VotingListener(Main plugin) {
		this.plugin = plugin;
		voting = plugin.getVoting();
	}
	
	@EventHandler
	public void handleVotingMenuOpener(PlayerInteractEvent event) {
		if(!(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)) return;
		if(!(event.getAction() == Action.RIGHT_CLICK_AIR)) return;
		Player player = event.getPlayer();
		ItemStack item = player.getItemInHand();
		if(item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) return;
		if(item.getItemMeta().getDisplayName().equals(PlayerLobbyConnectionListener.VOTING_ITEM_NAME));
			player.openInventory(voting.getVotingInventory());
	}
	
	@EventHandler
	public void handleVotingClick(InventoryClickEvent event) {
		if(!(event.getWhoClicked() instanceof Player)) return;
		Player player = (Player) event.getWhoClicked();
		if(!event.getInventory().getTitle().equals(Voting.VOTING_INVENTORY_TITLE)) return;
		event.setCancelled(true);
		for(int i = 0; i < voting.getVotingInventoryOrder().length; i++) {
			if(voting.getVotingInventoryOrder()[i] == event.getSlot()) {
				voting.vote(player, i);
				return;
			}
		}
	}

}
