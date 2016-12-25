package io.github.thomdare.adminmode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private List<String> adminmodeenable = new ArrayList<String>();
    private List<String> modmodeenable = new ArrayList<String>();
    private List<String> adminmodedisable = new ArrayList<String>();
    private List<String> modmodedisable = new ArrayList<String>();
    private List<String> inMod = new ArrayList<String>();
    private List<String> inAdmin = new ArrayList<String>();
    private HashMap<String, Location> StaffLocations = new HashMap<String, Location>();
    private HashMap<String, ItemStack[]> StaffInventories = new HashMap<String, ItemStack[]>();
    private HashMap<String, Integer> StaffLevel = new HashMap<String, Integer>();
    private HashMap<String, Float> StaffExp = new HashMap<String, Float>();

    public void onEnable() {
        // We need these
        getConfig().addDefault("permissions.adminmode.onenable", adminmodeenable);
        getConfig().addDefault("permissions.modmode.onenable", modmodeenable);
        getConfig().addDefault("permissions.adminmode.ondisable", adminmodedisable);
        getConfig().addDefault("permissions.modmode.ondisable", modmodedisable);
        getConfig().options().copyDefaults(true);
        saveConfig();
        // Lets get those already there
        adminmodeenable = getConfig().getStringList("permissions.adminmode.onenable");
        modmodeenable = getConfig().getStringList("permissions.modmode.onenable");
        adminmodedisable = getConfig().getStringList("permissions.adminmode.ondisable");
        modmodedisable = getConfig().getStringList("permissions.modmode.ondisable");
    }

    public void onDisable() {
        for(String name : inMod){
            Player player = Bukkit.getPlayer(name);
            for (String command : modmodedisable) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("@p", name));
            }
            if (StaffLocations.containsKey(name)) {
                player.teleport(StaffLocations.get(name));
                StaffLocations.remove(name);
            }
            if (StaffInventories.containsKey(name)) {
                player.getInventory().clear();
                //player.getInventory().addItem(StaffInventories.get(player.getName()));
                for (int i = 0; i <StaffInventories.get(name).length;i++) {
                    if(StaffInventories.get(name)[i] == null)
                        continue;
                    player.getInventory().setItem(i,StaffInventories.get(name)[i]);
                }
                StaffInventories.remove(name);
            }
            if (StaffExp.containsKey(name)) {
                player.setExp(StaffExp.get(name));
                player.setLevel(StaffLevel.get(name));
                StaffLevel.remove(name);
                StaffExp.remove(name);
            }
            player.updateInventory();
        }
        for(String name: inAdmin){
            Player player = Bukkit.getPlayer(name);
            for (String command : adminmodedisable) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("@p",name));

            }
            if (StaffLocations.containsKey(name)) {
                player.teleport(StaffLocations.get(name));
                StaffLocations.remove(name);
            }
            if (StaffInventories.containsKey(name)) {
                player.getInventory().clear();
                //player.getInventory().addItem(StaffInventories.get(name));
                for (int i = 0; i <StaffInventories.get(name).length;i++) {
                    if(StaffInventories.get(name)[i] == null)
                        continue;
                    player.getInventory().setItem(i,StaffInventories.get(name)[i]);
                }
                StaffInventories.remove(name);
            }
            if (StaffExp.containsKey(name)) {
                player.setExp(StaffExp.get(name));
                player.setLevel(StaffLevel.get(name));
                StaffLevel.remove(name);
                StaffExp.remove(name);
            }
            player.updateInventory();   
        }
        inMod.clear();
        inAdmin.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to execute that command!");
            return true;
        }

        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("modmode")) {
            if (!player.hasPermission("adminmode.mod")) {
                sender.sendMessage("You don't have permission to do that");
                return true;
            }
            if (inAdmin.contains(player.getName())) {
                sender.sendMessage("You're already in admin mode!");
                return true;
            }

            if (inMod.contains(player.getName())) {
                for (String command : modmodedisable) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("@p", player.getName()));
                }
                inMod.remove(player.getName());
                if (StaffLocations.containsKey(player.getName())) {
                    player.teleport(StaffLocations.get(player.getName()));
                    StaffLocations.remove(player.getName());
                }
                if (StaffInventories.containsKey(player.getName())) {
                    player.getInventory().clear();
                    //player.getInventory().addItem(StaffInventories.get(player.getName()));
                    for (int i = 0; i <StaffInventories.get(player.getName()).length;i++) {
                        if(StaffInventories.get(player.getName())[i] == null)
                            continue;
                        player.getInventory().setItem(i,StaffInventories.get(player.getName())[i]);
                    }
                    StaffInventories.remove(player.getName());
                }
                if (StaffExp.containsKey(player.getName())) {
                    player.setExp(StaffExp.get(player.getName()));
                    player.setLevel(StaffLevel.get(player.getName()));
                    StaffExp.remove(player.getName());
                    StaffLevel.remove(player.getName());
                }
                player.updateInventory();
                return true;
            } else {
                for (String command : modmodeenable) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("@p", player.getName()));

                }
                inMod.add(player.getName());
                StaffLocations.put(player.getName(), player.getLocation());
                StaffInventories.put(player.getName(), player.getInventory().getContents());
                StaffExp.put(player.getName(), player.getExp());
                StaffLevel.put(player.getName(), player.getLevel());
                player.setExp(0);
                player.setLevel(0);
                player.getInventory().clear();
                player.updateInventory();
                getLogger().info(player.getName() + " entered mod mode");
                sender.sendMessage("Entering mod mode");
                return true;
            }

        }

        if (cmd.getName().equalsIgnoreCase("adminmode")) {
            if (!player.hasPermission("adminmode.admin")) {
                sender.sendMessage("You don't have permission to do that");
                return true;
            }
            if (inMod.contains(player.getName())) {
                sender.sendMessage("You're already in mod mode!");
                return true;
            }

            if (inAdmin.contains(player.getName())) {
                for (String command : adminmodedisable) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("@p", player.getName()));

                }
                inAdmin.remove(player.getName());
                if (StaffLocations.containsKey(player.getName())) {
                    player.teleport(StaffLocations.get(player.getName()));
                    StaffLocations.remove(player.getName());
                }
                if (StaffInventories.containsKey(player.getName())) {
                    player.getInventory().clear();
                    //player.getInventory().addItem(StaffInventories.get(player.getName()));
                    for (int i = 0; i <StaffInventories.get(player.getName()).length;i++) {
                        if(StaffInventories.get(player.getName())[i] == null)
                            continue;
                        player.getInventory().setItem(i,StaffInventories.get(player.getName())[i]);
                    }
                    StaffInventories.remove(player.getName());
                }
                if (StaffExp.containsKey(player.getName())) {
                    player.setExp(StaffExp.get(player.getName()));
                    player.setLevel(StaffLevel.get(player.getName()));
                    StaffExp.remove(player.getName());
                    StaffLevel.remove(player.getName());
                }
                player.updateInventory();
                return true;
            } else {
                for (String command : adminmodeenable) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("@p", player.getName()));

                }
                inAdmin.add(player.getName());
                StaffLocations.put(player.getName(), player.getLocation());
                StaffInventories.put(player.getName(), player.getInventory().getContents());
                StaffExp.put(player.getName(), player.getExp());
                StaffLevel.put(player.getName(), player.getLevel());
                player.setExp(0);
                player.setLevel(0);
                player.getInventory().clear();
                player.updateInventory();
                getLogger().info(player.getName() + " entered admin mode");
                sender.sendMessage("Entering admin mode");
                return true;
            }

        }	
        return false;
    }
}
