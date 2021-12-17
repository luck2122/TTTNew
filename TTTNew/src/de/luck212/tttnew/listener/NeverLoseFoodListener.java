package de.luck212.tttnew.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import de.luck212.tttnew.main.Main;

public class NeverLoseFoodListener implements Listener{
	
	private Main plugin;
	
	public NeverLoseFoodListener(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	
	public void handlePlayerFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

}
