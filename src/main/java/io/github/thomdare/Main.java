package io.github.thomdare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	private List<String> adminmodeenable = new ArrayList<String>();
	private List<String> modmodeenable = new ArrayList<String>();
	private List<String> adminmodedisable = new ArrayList<String>();
	private List<String> modmodedisable = new ArrayList<String>();
	private List<String> inMod = new ArrayList<String>();
	private List<String> inAdmin = new ArrayList<String>();
	private HashMap<String, Location> StaffLocations = new HashMap<String, Location>();

	public void onEnable() {
		// We need these
		getConfig().addDefault("permissions.adminmode.onEnable", adminmodeenable);
		getConfig().addDefault("permissions.modmode.onEnable", modmodeenable);
		getConfig().addDefault("permissions.adminmode.onDisable", adminmodedisable);
		getConfig().addDefault("permissions.modmode.onDisable", modmodedisable);
		getConfig().options().copyDefaults(true);
		saveConfig();
		// Lets get those already there
		adminmodeenable = getConfig().getStringList("permissions.adminmode.onEnable");
		modmodeenable = getConfig().getStringList("permissions.modmode.onEnable");
		adminmodedisable = getConfig().getStringList("permissions.adminmode.onDisable");
		modmodedisable = getConfig().getStringList("permissions.modmode.onDisable");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("modmode")) {
				if (player.hasPermission("adminmode.mod")) {
					if (inMod.contains(player.getName())) {
						for (String command : modmodedisable) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("@p", player.getName()));
							inMod.remove(player.getName());
							if (StaffLocations.containsKey(player.getName())) {
								player.teleport(StaffLocations.get(player.getName()));
								StaffLocations.remove(player.getName());
							}
						}
						return true;
					} else {
						for (String command : modmodeenable) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("@p", player.getName()));
							inMod.add(player.getName());
							StaffLocations.put(player.getName(), player.getLocation());

						}
						return true;
					}
				}
			} else if (cmd.getName().equalsIgnoreCase("adminmode")) {
				if (player.hasPermission("adminmode.admin")) {
					if (inAdmin.contains(player.getName())) {
						for (String command : adminmodedisable) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("@p", player.getName()));
							inAdmin.remove(player.getName());
							if (StaffLocations.containsKey(player.getName())) {
								player.teleport(StaffLocations.get(player.getName()));
								StaffLocations.remove(player.getName());
							}
							
						}
						return true;
					} else {
						for (String command : adminmodeenable) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("@p", player.getName()));
							inAdmin.add(player.getName());
							StaffLocations.put(player.getName(), player.getLocation());
						}
						return true;
					}

				} else {
					sender.sendMessage("You must be a player!");
					return false;
				}
			}
		}
		return false;
	}
}
