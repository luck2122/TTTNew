package de.luck212.tttnew.listener;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.luck212.tttnew.gamestates.IngameState;
import de.luck212.tttnew.main.Main;
import de.luck212.tttnew.role.HealingStation;
import de.luck212.tttnew.role.Role;
import de.luck212.tttnew.role.RoleInventories;


public class ShopItemListener implements Listener{
	
	// TODO Listener für TK maybe mit scheduler
	
	private Main plugin;
	
	public ShopItemListener(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void handleCreeperArrowHit(ProjectileHitEvent event){
		if(event.getEntity().getType() != EntityType.ARROW) return;
		if(!(event.getEntity().getShooter() instanceof Player)) return;
		Player player = (Player) event.getEntity().getShooter();
		if(plugin.getRoleManager().getPlayerRole(player) != Role.TRAITOR) return;
		if(RoleInventories.removeMaterialItem(player, Material.SULPHUR)) {
			World world = event.getEntity().getWorld();
			world.spawnEntity(event.getEntity().getLocation(), EntityType.CREEPER);
			event.getEntity().remove();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void handleHealingStationCreation(BlockPlaceEvent event) {
		if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
		Player player = event.getPlayer();
		if(plugin.getRoleManager().getPlayerRole(player) != Role.DETECTIVE) return;
		
		if(event.getBlock().getType() == Material.BEACON) {
			event.setCancelled(false);
			 new HealingStation(plugin, event.getBlock().getLocation());
		}
	}
	
	@EventHandler
	public void handleOneShotHit(EntityDamageByEntityEvent event) {
		if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
		if(!(event.getDamager() instanceof Projectile)) return;
		
		Projectile projectile = (Projectile) event.getDamager();
		
		if(!(event.getEntity() instanceof Player))return;
		if(!(projectile.getShooter() instanceof Player)) return; 
		
		
		Player shooter = (Player) projectile.getShooter();
		Player victim = (Player) event.getEntity();
		
		Role shooterRole = plugin.getRoleManager().getPlayerRole(shooter);
		Role victimRole = plugin.getRoleManager().getPlayerRole(victim);
		if(plugin.getRoleManager().getPlayerRole(shooter) != Role.DETECTIVE) return;
		if(victimRole == Role.DETECTIVE && shooterRole == Role.DETECTIVE) {
			event.setDamage(0);
			return;
		}else if(shooter.getItemInHand().getItemMeta().getDisplayName().equals(RoleInventories.DETECTIVE_ONE_SHOT_BOW)) 
			event.setDamage((event.getDamage() * 100));
	}
	
	@EventHandler
	public void handleShootOneShot(EntityShootBowEvent event) {
		if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
		if(event.getBow().getItemMeta() == null) return;
		if(event.getBow().getItemMeta().getDisplayName() == null) return;
		if(event.getEntityType() != EntityType.PLAYER) return;
		
		Player player = (Player) event.getEntity();
		ItemStack shotBow = event.getBow();
		ItemMeta shotBowMeta = shotBow.getItemMeta();
		
		for(int i = 0; i < player.getInventory().getSize(); i++) {
			if(player.getInventory().getContents()[i] != null) {
				if(player.getInventory().getContents()[i].getType() == Material.BOW) {
					if(player.getInventory().getContents()[i].getItemMeta() != null) {
						if(player.getInventory().getContents()[i].getItemMeta().getDisplayName() != null) {
							ItemStack currentBow = player.getInventory().getContents()[i];
							ItemMeta currentBowMeta = currentBow.getItemMeta();
							if(currentBowMeta.getDisplayName().equals(RoleInventories.DETECTIVE_ONE_SHOT_BOW))
								player.getInventory().getContents()[i].setAmount(player.getInventory().getContents()[i].getAmount() - 1);	
						}
					}
				}
			}
		}
		
	}
	
	@EventHandler
	public void handleTarnkappeActivation(PlayerInteractEvent event) {
		if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
		if(event.getAction() != Action.RIGHT_CLICK_AIR) return;
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		
		Player player = event.getPlayer();
		Role playerRole = plugin.getRoleManager().getPlayerRole(player);
		player.sendMessage(playerRole.toString());
		if(playerRole != Role.TRAITOR) return;
		player.sendMessage(player.getItemInHand().getItemMeta().toString());
		if(player.getItemInHand().getItemMeta().getDisplayName() == RoleInventories.TRAITOR_TARNKAPPE) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 45, 45));
			return;
		}
	}
}
