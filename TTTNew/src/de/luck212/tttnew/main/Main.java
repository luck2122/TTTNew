package de.luck212.tttnew.main;

import de.luck212.tttnew.commands.BuildCommand;
import de.luck212.tttnew.commands.SetupCommand;
import de.luck212.tttnew.commands.StartCommand;
import de.luck212.tttnew.gamestates.GameState;
import de.luck212.tttnew.gamestates.GameStateManager;
import de.luck212.tttnew.listener.*;
import de.luck212.tttnew.role.RoleInventories;
import de.luck212.tttnew.role.RoleManager;
import de.luck212.tttnew.voting.Map;
import de.luck212.tttnew.voting.Voting;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class Main extends JavaPlugin {
	
	public static final String PREFIX = "§7[§cTTT§7] §r",
							   PERMISSION = PREFIX + "§cDazu hast du keine Rechte!";
	// TODO Bugs: OneShotBow geht noch nciht kaputt und ist nicht one shot. TK klappt noch nicht geht vielleicht auch nie
	// TODO Bugs: Am Ende Spieler Reviven und an alle an LobbyLocation teleportieren. Wenn Spieler tot dann an Spectatorlocation teleportiern und gm 1 setzen und Spieler unsichtrbar machen maybe auch mit invis
	private GameStateManager gameStateManager;
	private ArrayList<Player> players;
	private ArrayList<Map> maps;
	private Voting voting;
	private RoleManager roleManager;
	private GameProgressListener gameProgressListener;
	private GameProtectionListener gameProtectionListener;
	private RoleInventories roleInventories;
	
	@Override
	public void onEnable() {
		
		gameStateManager = new GameStateManager(this);
		setPlayers(new ArrayList<>());
		
		System.out.println("[TTT] Das Plugin wird gestartet");
		
		gameStateManager.setGameState(GameState.LOBBY_STATE);
		
		init(Bukkit.getPluginManager());
		
	}
	
	private void init(PluginManager pluginManager) {
		initVoting();
		roleManager = new RoleManager(this);
		gameProtectionListener = new GameProtectionListener(this);
		roleInventories = new RoleInventories();
		
		getCommand("setup").setExecutor(new SetupCommand(this));
		getCommand("start").setExecutor(new StartCommand(this));
		getCommand("build").setExecutor(new BuildCommand(this));
		
		pluginManager.registerEvents(new PlayerLobbyConnectionListener(this), this);
		pluginManager.registerEvents(new VotingListener(this), this);
		pluginManager.registerEvents(new GameProgressListener(this), this);
		pluginManager.registerEvents(new NeverLoseFoodListener(this), this);
		pluginManager.registerEvents(gameProtectionListener, this);
		pluginManager.registerEvents(new ChatListener(this), this);
		pluginManager.registerEvents(new ChestItemListener(this), this);
		pluginManager.registerEvents(new TesterListener(this), this);
		pluginManager.registerEvents(roleInventories, this);
		pluginManager.registerEvents(new ShopItemListener(this), this);
	}
	
	private void initVoting() {
		maps = new ArrayList<Map>();
		for(String current : getConfig().getConfigurationSection("Arenas.").getKeys(false)) {
			Map map = new Map(this, current);
			if(map.playable()) {
				maps.add(map);
			}else
				Bukkit.getConsoleSender().sendMessage("§cDie Map §4" + map.getName() + "§c ist noch nicht fertig eingerichtet.");
		}
		if(maps.size() >= Voting.MAP_AMOUNT)
			voting = new Voting(this, maps);
		else {
			Bukkit.getConsoleSender().sendMessage("§cFür das Voting müssen mindestens §4" + Voting.MAP_AMOUNT + " §cMaps eingerichtet sein.");
			voting = null;
		}
	}
	
	@Override
	public void onDisable() {
		System.out.println("[TTT] Das Plugin wurde beendet");
	}
	public GameStateManager getGameStateManager() {
		return gameStateManager;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	
	public Voting getVoting() {
		return voting;
	}
	
	public ArrayList<Map> getMaps() {
		return maps;
	}
	
	public RoleManager getRoleManager() {
		return roleManager;
	}
	
	public GameProgressListener getGameProgressionListener() {
		return gameProgressListener;
	}
	
	public GameProtectionListener getGameProtectionListener() {
		return gameProtectionListener;
	}
	
	public RoleInventories getRoleInventories() {
		return roleInventories;
	}
}
