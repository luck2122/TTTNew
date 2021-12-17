package de.luck212.tttnew.role;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.luck212.tttnew.main.Main;
import de.luck212.tttnew.util.ConfigLocationUtil;
import de.luck212.tttnew.voting.Map;

public class Tester {
	private static final int TESTING_TIME = 5;
	
	private Main plugin;
	private Map map;

	private Block[] borderBlocks, lamps;
	private Block button;
	private Location testerLocation;
	private Location playerTestLocation;
	private boolean inUse;
	private World world;
	
	

	public Tester(Map map, Main plugin) {
		this.plugin = plugin;
		this.map = map;
		borderBlocks = new Block[3];
		lamps = new Block[2];
	}
	
	public void test(Player player) {
		Role role = plugin.getRoleManager().getPlayerRole(player);
		if(role == Role.DETECTIVE) {
			player.sendMessage(Main.PREFIX + "§cDu bist " + role.getChatColor() + "Detective §cund kannst den Tester nicht benutzen.");
			return;
		}
		
		if(inUse) {
			player.sendMessage(Main.PREFIX + "§cDer Tester wird gerade benutzt.");
			return;
		}
		
		if(role == Role.TRAITOR) {
			if(RoleInventories.removeMaterialItem(player, Material.STAINED_GLASS)) {
				player.sendMessage(Main.PREFIX + "§7Du hast eine Chance von §675% §7als Innocent erkannt zu werden");
				if(Math.random() <= 0.75D)
					role = Role.INNOCENT;
			}
			
//			if(plugin.getRoleInventories().getTicketActivated()) {
//				if(Math.random() <= 0.75D)
//					role = Role.INNOCENT;
//			}
		}
		
		Bukkit.broadcastMessage(Main.PREFIX + "§7" + player.getName() + " hat den Tester betreten.");
		player.teleport(button.getLocation());
		//player.teleport(playerTestLocation);
		inUse = true;
		for(Block current : borderBlocks)
			setColoredGlass(current.getLocation(), DyeColor.WHITE);
		
		for(Entity current : player.getNearbyEntities(4, 4, 4)) {
			if(current instanceof Player) 
				((Player) current).teleport(testerLocation);
		}
		
		Role endRole = role;
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				endTesting(endRole);
				
			}
		}, TESTING_TIME * 20);
	}
	
	private void endTesting(Role role) {
		if(role == Role.TRAITOR) {
			for(Block current : lamps) 
				setColoredGlass(current.getLocation(), DyeColor.RED);
		}else {
			for(Block current : lamps) 
				setColoredGlass(current.getLocation(), DyeColor.GREEN);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				resetTester();
				
			}
		}, TESTING_TIME * 20);
	}
	
	
	public void load() {
		for(int i = 0; i < borderBlocks.length; i++)
			borderBlocks[i] = new ConfigLocationUtil(plugin, "Arenas." + map.getName() + ".Tester.BorderBlocks." + i).loadBlockLocation();
		for(int i = 0; i < lamps.length; i++)
			lamps[i] = new ConfigLocationUtil(plugin, "Arenas." + map.getName() + ".Tester.Lamps." + i).loadBlockLocation();
		
		button = new ConfigLocationUtil(plugin, "Arenas." + map.getName() + ".Tester.Button").loadBlockLocation();
		testerLocation = new ConfigLocationUtil(plugin, "Arenas." + map.getName() + ".Tester.Location").loadLocation();
		
		world = map.getSpectatorLocation().getWorld();
		for(Block current : borderBlocks)
			world.getBlockAt(current.getLocation()).setType(Material.AIR);
		for(Block current : lamps)
			setColoredGlass(current.getLocation(), DyeColor.BLACK);
		resetTester();
		
	}
	
	private void resetTester() {
		inUse = false;
		
		for(Block current : borderBlocks) 
			world.getBlockAt(current.getLocation()).setType(Material.AIR);
		for(Block current : lamps)
			setColoredGlass(current.getLocation(), DyeColor.WHITE);
	}
	
	private void setColoredGlass(Location location, DyeColor dyeColor) {
		Block block = world.getBlockAt(location);
		block.setType(Material.STAINED_GLASS);
		block.setData(dyeColor.getData());
	}
	
	
	public boolean exists() {
		return plugin.getConfig().getString("Arenas." + map.getName() + ".Tester.Location.World") != null;
	}
	
	public Block getButton() {
		return button;
	}
	
	public Block[] getBorderBlocks() {
		return borderBlocks;
	}
	
	public Block[] getLamps() {
		return lamps;
	}
	
	public Location getTesterLocation() {
		return testerLocation;
	}
	
	public Location getPlayerTestLocation() {
		return playerTestLocation;
	}
}
