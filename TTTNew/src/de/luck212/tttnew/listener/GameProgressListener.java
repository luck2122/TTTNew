package de.luck212.tttnew.listener;

import de.luck212.tttnew.gamestates.IngameState;
import de.luck212.tttnew.main.Main;
import de.luck212.tttnew.role.Role;
import de.luck212.tttnew.role.RoleManager;
import net.minecraft.server.v1_8_R1.EnumClientCommand;
import net.minecraft.server.v1_8_R1.PacketPlayInClientCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameProgressListener implements Listener {

	private Main plugin;
	private RoleManager roleManager;

	public GameProgressListener(Main plugin) {
		this.plugin = plugin;
		this.roleManager = plugin.getRoleManager();
	}

	@EventHandler
	public void handlePlayerDamage(EntityDamageByEntityEvent event) {
		if (!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState))
			return;
		if (!(event.getDamager() instanceof Player))
			return;
		if (!(event.getEntity() instanceof Player))
			return;
		Player damager = (Player) event.getDamager(), victim = (Player) event.getEntity();
		Role damagerRole = roleManager.getPlayerRole(damager);
		Role victimRole = roleManager.getPlayerRole(victim);
		if (damagerRole == null && victimRole == null)
			event.setCancelled(true);
		if ((damagerRole == Role.INNOCENT || damagerRole == Role.DETECTIVE) && victimRole == Role.DETECTIVE)
			damager.sendMessage(Main.PREFIX + "§cDu hast einen Detective angegriffen!");
		if (damagerRole == Role.TRAITOR && victimRole == Role.TRAITOR)
			event.setDamage(0);
	}

	@EventHandler
	public void handlePlayerFallDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		Player victim = (Player) event.getEntity();
		Role victimRole = plugin.getRoleManager().getPlayerRole(victim);
		if (event.getCause() == DamageCause.FALL && victimRole == null)
			event.setCancelled(true);
	}

	
	@EventHandler(priority = EventPriority.HIGH)
	public void handlePlayerDeathEvent(PlayerDeathEvent event) {
		if (!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState))
			return;
			
		event.setDeathMessage(null);
		IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
		Player victim = event.getEntity();

		PacketPlayInClientCommand packet = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
		((CraftPlayer)victim).getHandle().playerConnection.a(packet);

		event.getDrops().clear();
		victim.getInventory().setChestplate(null);
		victim.getInventory().setHelmet(null);



		if(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState) {
			ingameState.addSpectator(victim);
		}else
			victim.setGameMode(GameMode.CREATIVE);



		if (victim.getKiller() != null) {

			Player killer = victim.getKiller();
			Role killerRole = roleManager.getPlayerRole(killer), victimRole = roleManager.getPlayerRole(victim);

			switch (killerRole) {
			case TRAITOR:
				if (victimRole == Role.TRAITOR) {
					killer.sendMessage(Main.PREFIX + "§cDu hast einen Traitor getötet! §e-50 Karma");
				} else if(victimRole == Role.INNOCENT){
					killer.sendMessage(
							Main.PREFIX + "§a Du hast §e" + victim.getName() + " §agetötet. Dieser Spieler war ein "
									+ victimRole.getChatColor() + victimRole.getName() + " §e +10 §aKarma.");
					plugin.getRoleInventories().getPointManager().addPoints(killer, 1);
				}else {
					killer.sendMessage(
							Main.PREFIX + "§a Du hast §e" + victim.getName() + " §agetötet. Dieser Spieler war ein "
									+ victimRole.getChatColor() + victimRole.getName() + " §e +20 §aKarma.");
					plugin.getRoleInventories().getPointManager().addPoints(killer, 2);
				}
				break;
			case INNOCENT:
				if (victimRole == Role.TRAITOR) {
					killer.sendMessage(
							Main.PREFIX + "§aDu hast §e" + victim.getName() + "§a getötet. Dieser Spieler war ein "
									+ victimRole.getChatColor() + victimRole.getName() + " §e +20 §aKarma.");
				} else if (victimRole == Role.DETECTIVE) {
					killer.sendMessage(
							Main.PREFIX + "§cDu hast §e" + victim.getName() + "§c getötet. Dieser Spieler war ein "
									+ victimRole.getChatColor() + victimRole.getName() + " §c -50 Karma.");
				} else if (victimRole == Role.INNOCENT)
					killer.sendMessage(
							Main.PREFIX + "§cDu hast §e" + victim.getName() + "§c getötet. Dieser Spieler war ein "
									+ victimRole.getChatColor() + victimRole.getName() + " §c -20 Karma.");
			break;	
			case DETECTIVE:
				if (victimRole == Role.TRAITOR) {
					killer.sendMessage(
							Main.PREFIX + "§aDu hast §e" + victim.getName() + "§a getötet. Dieser Spieler war ein "
									+ victimRole.getChatColor() + victimRole.getName() + " §e +20 §aKarma.");
					plugin.getRoleInventories().getPointManager().addPoints(killer, 2);
				} else if (victimRole == Role.DETECTIVE) {
					killer.sendMessage(
							Main.PREFIX + "§cDu hast §e" + victim.getName() + "§c getötet. Dieser Spieler war ein "
									+ victimRole.getChatColor() + victimRole.getName() + " §c -50 Karma.");
				} else if (victimRole == Role.INNOCENT)
					killer.sendMessage(
							Main.PREFIX + "§cDu hast §e" + victim.getName() + "§c getötet. Dieser Spieler war ein "
									+ victimRole.getChatColor() + victimRole.getName() + " §c -20 Karma.");
				break;
			default:
				break;
			}

			victim.sendMessage(
					Main.PREFIX + "§7Du wurdest von " + killerRole.getChatColor() + killer.getName() + " §7 getötet.");

			if (victimRole == Role.TRAITOR) {
				plugin.getRoleManager().getTraitorPlayers().remove(victim.getName());
			}
			plugin.getPlayers().remove(victim);

			ingameState.checkGameEnding();
			
		} else {
			victim.sendMessage(Main.PREFIX + "§cDu bist gestorben.");
			if(plugin.getRoleManager().getPlayerRole(victim) == Role.TRAITOR)
				plugin.getRoleManager().getTraitorPlayers().remove(victim.getName());
			plugin.getPlayers().remove(victim.getName());
			
			ingameState.checkGameEnding();
		}
		for(Player current : Bukkit.getOnlinePlayers()) {
			ingameState.updateScoreboard(current);
		}
	}
	
	@EventHandler
	public void handlePlayerQuit(PlayerQuitEvent event) {
		if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
		Player player = event.getPlayer();
		if(plugin.getPlayers().contains(player)) {
			IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
			plugin.getPlayers().remove(player);
			event.setQuitMessage(Main.PREFIX + "§7Der spieler §6" + player.getName() + "§7 hat das Spiel verlassen.");
			
			ingameState.checkGameEnding();
		}
	}

}
