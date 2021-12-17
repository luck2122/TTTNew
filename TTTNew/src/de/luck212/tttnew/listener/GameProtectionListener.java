package de.luck212.tttnew.listener;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import de.luck212.tttnew.gamestates.EndingState;
import de.luck212.tttnew.gamestates.IngameState;
import de.luck212.tttnew.gamestates.LobbyState;
import de.luck212.tttnew.main.Main;
import net.minecraft.server.v1_8_R1.EnumClientCommand;
import net.minecraft.server.v1_8_R1.PacketPlayInClientCommand;

public class GameProtectionListener implements Listener{
	
	private Main plugin;
	private ArrayList<String> buildModePlayers;
	
	public GameProtectionListener(Main plugin) {
		this.plugin = plugin;
		buildModePlayers = new ArrayList<String>();
	}
	
	@EventHandler
	public void handlePlayerBuild(BlockPlaceEvent event) {
		if(buildModePlayers.contains(event.getPlayer().getName())) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	public void handlePlayerBreak(BlockBreakEvent event) {
		if(buildModePlayers.contains(event.getPlayer().getName())) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	public void handlePlayerDrop(PlayerDropItemEvent event) {
		if(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
			event.setCancelled(true);
			return;
		}
		if(plugin.getGameStateManager().getCurrentGameState() instanceof EndingState) {
			event.setCancelled(true);
			return;
		}
		
		Material material = event.getItemDrop().getItemStack().getType();
		if(material == Material.LEATHER_CHESTPLATE || material == Material.STICK ||
			((material == Material.BOW) && (event.getItemDrop().getItemStack().getItemMeta() != null))) 
			event.setCancelled(true);
		
	}
	
	@EventHandler
	public void handleCreatureSpawn(CreatureSpawnEvent event) {
		if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void handleBedEnter(PlayerBedEnterEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void handleBowShot(EntityShootBowEvent event) {
		if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
			event.setCancelled(true);
			return;
		}
		
		IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
		if(ingameState.isInGrace())
			event.setCancelled(true);
	}
	
	@EventHandler
	public void handleEntityDamageEntity(EntityDamageByEntityEvent event) {
		if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
			event.setCancelled(true);
			return;
		}
		
		IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
		if(ingameState.isInGrace())
			event.setCancelled(true);
		
		if(!(event.getDamager() instanceof Player)) return;
		Player player = (Player) event.getDamager();
		if(ingameState.getSpectators().contains(player))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void handlePlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		PacketPlayInClientCommand packet = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
		((CraftPlayer)player).getHandle().playerConnection.a(packet); 
		
		event.getDrops().clear();
		player.getInventory().setChestplate(null);
		player.getInventory().setHelmet(null);
		

		
		if(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState) {
			IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
			ingameState.addSpectator(player);
		}else
			player.setGameMode(GameMode.CREATIVE);
		
	}
	
	@EventHandler
	public void handleInventoryClick(InventoryClickEvent event) {
		if(!(event.getWhoClicked() instanceof Player)) return;
//		if(event.getCurrentItem().getType() == Material.LEATHER_CHESTPLATE || event.getCurrentItem().getType() == Material.NETHER_STAR || event.getCurrentItem().getType() == Material.LEATHER_HELMET)
//			event.setCancelled(true);
		if(event.getSlotType() == InventoryType.SlotType.ARMOR)
			event.setCancelled(true);
		return;
	}
	
	@EventHandler
	public void handleExplosionDestruction(EntityExplodeEvent event) {
		event.blockList().clear();
	}
	
	
	
	
	
	public ArrayList<String> getBuildModePlayers() {
		return buildModePlayers;
	}
	
	

}
