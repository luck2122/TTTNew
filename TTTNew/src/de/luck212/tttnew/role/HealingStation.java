package de.luck212.tttnew.role;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import de.luck212.tttnew.main.Main;

public class HealingStation {
	
	private static final int HEALING_DELAY = 5,
							 HEALING_RADIUS = 5;
	
	private static final double HEAL_POWER = 4;
	
	private Main plugin;
	private int taskID, durability;
	private Entity dummyEntity;
	private Location location;
	
	public HealingStation(Main plugin, Location location) {
		this.plugin = plugin; 
		durability = 3;
		this.location = location;
		generateStation();
	}
	
	private void generateStation() {
		dummyEntity = location.getWorld().spawnEntity(location, EntityType.ARROW);
		
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				durability--;
				dummyEntity.remove();
				dummyEntity = location.getWorld().spawnEntity(location, EntityType.ARROW);
				for(Entity current : dummyEntity.getNearbyEntities(HEALING_RADIUS, HEALING_RADIUS, HEALING_RADIUS)){
					if(current instanceof Player) {
						Player player = (Player) current;
						if(player.getHealth() <= (20 - HEAL_POWER))
							player.setHealth(player.getHealth() + HEAL_POWER);
						else
							player.setHealth(20);
					}
				}
				
				if(durability <= 0)
					destroyStation();
			}
		}, 0, 20*HEALING_DELAY);
	}
	
	private void destroyStation() {
		dummyEntity.remove();
		location.getBlock().setType(Material.AIR);
		Bukkit.getScheduler().cancelTask(taskID);
	}

}
