package io.github.thomdare.adminmode;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class Main extends JavaPlugin {
    private List<Mode> modes = new ArrayList<>();

    public void onEnable() {
        loadModes();
    }

    public void onDisable() {
        for (Mode mode : modes) {
            for (UUID id : mode.getPlayers()) {
                mode.disableMode(Bukkit.getPlayer(id));
            }
        }
    }


    private void loadModes() {
        if (getDataFolder().listFiles() == null || getDataFolder().listFiles().length == 0) {
            File exampleFile = new File(getDataFolder(), "exampleMode.yml");
            exampleFile.getParentFile().mkdirs();
            /*try {
                exampleFile.createNewFile();
            }catch (IOException e) {
                e.printStackTrace();
            }*/
            //saveResource("exampleMode.yml", false);
        }
        assert getDataFolder().listFiles() != null;

        List<File> fileConfigs = Arrays.asList(getDataFolder().listFiles());
        List<YamlConfiguration> fileYaml = new ArrayList<>(fileConfigs.size());
        getLogger().info("Loading " + fileConfigs.size() + " mode(s)");
        try {
            for(File file : getDataFolder().listFiles()){
                modes.add(new Mode(YamlConfiguration.loadConfiguration(file),file));
            }

            if(fileConfigs.size()==0){
                File exampleFile= new File(getDataFolder(), "exampleMode.yml");
                modes.add(new Mode(YamlConfiguration.loadConfiguration(exampleFile),exampleFile));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Mode mode : modes) {
            try {
                final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

                bukkitCommandMap.setAccessible(true);
                CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

                commandMap.register(mode.getName(), new ModeCommand(mode.getName()));
            } catch (IllegalAccessException | SecurityException | NoSuchFieldException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param player the player to check
     * @return the mode a player is in, otherwise null
     */
    private Mode getPlayerMode(Player player) {
        for (Mode mode : modes) {
            if (mode.getPlayers().contains(player.getUniqueId()))
                return mode;
        }
        return null;
    }

    private Mode getMode(String name) {
        for (Mode mode : modes) {
            if (mode.getName().equals(name))
                return mode;
        }
        return null;
    }

    private class ModeCommand extends BukkitCommand implements PluginIdentifiableCommand {

        private ModeCommand(String name) {
            super(name);
            this.description = "Toggles " + name + " mode.";
            this.usageMessage = "/" + name;
            this.setPermission("StaffMode." + name);
            this.setAliases(new ArrayList<String>());
        }

        @Override
        public boolean execute(CommandSender sender, String s, String[] strings) {
            if(!(sender instanceof Player)){
                sender.sendMessage(ChatColor.RED + "You need to be a player to use that command.");
                return true;
            }

            if (!sender.hasPermission(this.getPermission())) {
                sender.sendMessage(ChatColor.RED + "You don't have permission.");
                return true;
            }
            Player player = (Player) sender;
            Mode playerMode = getPlayerMode(player);
            if (playerMode != getMode(getName()) && playerMode != null) {
                sender.sendMessage("You're already in " + getPlayerMode(player) + " mode!");
                return true;
            }

            if (playerMode != null) {
                return true;
            }
            return true;
        }

        @Override
        public Plugin getPlugin() {
            return Main.this;
        }
    }

}
