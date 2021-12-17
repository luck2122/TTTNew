package de.luck212.tttnew.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import de.luck212.tttnew.main.Main;
import de.luck212.tttnew.voting.Map;

public class TesterSetup implements Listener{
	
	private Main plugin;
	private Player player;
	private Map map;
	private int phase;
	private boolean finished;
	
	private Block[] borderBlocks, lamps;
	private Block button;
	private Location testerLocation;
	private Location playerTestLocation;
	
	public TesterSetup(Player player, Map map, Main plugin) {
		this.plugin = plugin;
		this.player = player;
		this.map = map;
		Bukkit.getPluginManager().registerEvents(this, plugin);
		phase = 1;
		finished = false;
		
		borderBlocks = new Block[3];
		lamps = new Block[2];
		
		startSetup();
	}
	
	@EventHandler
	public void handleBlockBreak(BlockBreakEvent event) {
		if(!event.getPlayer().getName().equals(player.getName())) return;
		if(finished) return;
		event.setCancelled(true);
		switch(phase) {
		case 1: case 2: case 3:
			borderBlocks[phase - 1] = event.getBlock();
			player.sendMessage(Main.PREFIX + "§&Grenzblock " + phase + "§a gesetzt.");
			phase++;
			startPhase(phase);
			break;
		case 4: case 5:
			if(event.getBlock().getType() == Material.STAINED_GLASS) {
				lamps[phase - 4] = event.getBlock();
				player.sendMessage(Main.PREFIX + "§&Lampe " + (phase - 3) + "§a gesetzt.");
				phase++;
				startPhase(phase);
			}else 
				player.sendMessage(Main.PREFIX + "§cDie Lampe muss aus §&Glas §cbestehen!");
			break;
		case 6:
			if(event.getBlock().getType() == Material.STONE_BUTTON) {
				button = event.getBlock();
				player.sendMessage(Main.PREFIX + "§aTesterknopf wurde gesetzt.");
				phase++;
				startPhase(phase);
			}else
				player.sendMessage(Main.PREFIX + "§cBitte klicke einen §6Stone-Knopf §can!");
			break;
			default:
				break;
		}
	}
	
	public void startPhase(int phase) {
		switch(phase) {
		case 1: case 2: case 3:
			player.sendMessage(Main.PREFIX + "§7Bitte klicke einen §6Begrenzungsblock §7an!");
			break;
		case 4: case 5:
			player.sendMessage(Main.PREFIX + "§7Bitte klicke eine §6Lampe §7an!");
			break;
		case 6:
			player.sendMessage(Main.PREFIX + "§7Bitte klicke einen §6Testerknopf §7an!");
			break;
		case 7:
			player.sendMessage(Main.PREFIX + "§7Bitte §6sneake §7an der Tester-Location");
			break;
		case 8:
			player.sendMessage(Main.PREFIX + "§7Bitte §6sneake §7innerhalb des Testers an der Player-Tester-Location");
			default:
				break;
		}
	}
	
	@EventHandler
	public void handlePlayerSneak(PlayerToggleSneakEvent event) {
		if(!event.getPlayer().getName().equals(player.getName())) return;
		if(finished) return;
		if(phase == 7) {
			testerLocation = player.getLocation();
			player.sendMessage(Main.PREFIX + "§aDie §6Tester-Location §awurde gesetzt.");
			phase++;
			finishSetup();
		}
	}
	
	public void finishSetup() {
		player.sendMessage(Main.PREFIX + "§aDas Setup wurde abgeschlossen.");
		finished = true;
		
		for(int i = 0; i < borderBlocks.length; i++) 
			new ConfigLocationUtil(plugin, borderBlocks[i].getLocation(), "Arenas." + map.getName() + ".Tester.BorderBlocks." + i).saveBlockLocation();
		
		for(int i = 0; i < lamps.length; i++)
			new ConfigLocationUtil(plugin, lamps[i].getLocation(), "Arenas." + map.getName() + ".Tester.Lamps." + i).saveBlockLocation();
		
		new ConfigLocationUtil(plugin, button.getLocation(), "Arenas." + map.getName() + ".Tester.Button").saveBlockLocation();
		new ConfigLocationUtil(plugin, testerLocation, "Arenas." + map.getName() + ".Tester.Location").saveLocation();
		new ConfigLocationUtil(plugin, playerTestLocation, "Arenas." + map.getName() + ".Tester.PlayerLocation").saveLocation();
	}
	
	public void startSetup() {
		player.sendMessage(Main.PREFIX + "§aDu hast ein Tester-Setup gestartet.");
		player.sendMessage(Main.PREFIX + "§6Starte mit Schritt 1");
		startPhase(phase);
	}

}
