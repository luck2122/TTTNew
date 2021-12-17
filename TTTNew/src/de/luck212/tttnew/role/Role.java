package de.luck212.tttnew.role;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public enum Role {
	
	INNOCENT("Innocent", ChatColor.GREEN, Color.GREEN),
	DETECTIVE("Detective", ChatColor.BLUE, Color.BLUE),
	TRAITOR("Traitor", ChatColor.RED, Color.RED);
	
	private Role(String name, ChatColor chatColor, Color color) {
		this.name = name;
		this.chatColor = chatColor;
		this.color = color;
	}
	
	private String name;
	private ChatColor chatColor;
	private Color color;
	
	public ChatColor getChatColor() {
		return chatColor;
	}
	
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}

}
