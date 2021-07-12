package com.privatevaults;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

public class UpdateChecker {

    private final JavaPlugin plugin;
    private final int resourceId;

    public UpdateChecker(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
        checkUpdate();
    }

    private void checkUpdate() {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream();
                 Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    if (plugin.getDescription().getVersion().equalsIgnoreCase(scanner.next())) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"Private Vaults is up to date.");
                    } else {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Private Vaults is outdated.");

                    }
                }
            } catch (IOException exception) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Cannot look for updates: " + ChatColor.RESET +exception.getMessage());
            }
        });
    }

}
