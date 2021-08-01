package com.privatevaults;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class UpdateChecker {

    private static JavaPlugin plugin;
    private static int resourceId;
    private static ScheduledFuture<?> updateCheckerScheduler = null;


    public static void setup(JavaPlugin thisPlugin,int thisResourceId){
        plugin = thisPlugin;
        resourceId = thisResourceId;
        checkUpdate(false);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable checkUpdate = () -> checkUpdate(true);
        updateCheckerScheduler = scheduler.scheduleAtFixedRate(checkUpdate,1,1, TimeUnit.HOURS);
    }

    private static void checkUpdate(boolean isSilent) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId+"&t="+System.currentTimeMillis()).openStream();
                 Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    String newVer = scanner.next();
                    if (plugin.getDescription().getVersion().equalsIgnoreCase(newVer)) {
                        if(!isSilent) {
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Private Vaults is up to date.");
                        }
                    } else {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"\nPrivate Vaults is outdated.\n" +
                                "You are using version "+plugin.getDescription().getVersion()+", but version "+newVer +" is the newest.\n"+
                                "Please upgrade to the new version here https://www.spigotmc.org/resources/private-vaults.94151/\n"+
                                "To report bugs contact me on my discord server https://discord.gg/rA6WHrg7Re");
                    }
                }
            } catch (IOException exception) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Cannot look for updates: " + ChatColor.RESET + exception.getMessage());
            }
        });
    }

    public static void cancelScheduler(){
        if(updateCheckerScheduler!=null){
            updateCheckerScheduler.cancel(false);
        }
    }

}
