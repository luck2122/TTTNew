package de.luck212.tttnew.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.luck212.tttnew.gamestates.IngameState;
import de.luck212.tttnew.main.Main;
import de.luck212.tttnew.role.Tester;

public class TesterListener implements Listener{
	
	private Main plugin;
	
	public TesterListener(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void handleTesterclick(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Block clicked = event.getClickedBlock();
		if((clicked.getType() != Material.WOOD_BUTTON) && (clicked.getType() != Material.STONE_BUTTON)) return;
		if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
		IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
		if(ingameState.isInGrace()) return;
		
		Tester tester = ingameState.getMap().getTester();
		if(tester.getButton().getLocation().equals(clicked.getLocation()))
			tester.test(event.getPlayer());
	}

}
