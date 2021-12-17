package de.luck212.tttnew.gamestates;

import de.luck212.tttnew.main.Main;

public class GameStateManager {
	
	private Main plugin;
	private GameState[] gameStates;
	private GameState currentGameState;
	
	public GameStateManager(Main plugin) {
		this.plugin = plugin;
		gameStates = new GameState[3];
		
		gameStates[GameState.LOBBY_STATE] = new LobbyState(plugin, this);
		gameStates[GameState.INGAME_STATE] = new IngameState(plugin);
		gameStates[GameState.ENDING_STATE] = new EndingState(plugin);
	}
	
	public void setGameState(int gameStateID) {
		if(currentGameState != null)
			currentGameState.stop();
		currentGameState = gameStates[gameStateID];
		currentGameState.start();
	}
	
	public void stopCurrentGameState() {
		if(currentGameState != null) {
			currentGameState.stop();
			currentGameState = null;
		}
	}
	
	public GameState getCurrentGameState() {
		return currentGameState;
	}
	
	public Main getPlugin() {
		return plugin;
	}

}
