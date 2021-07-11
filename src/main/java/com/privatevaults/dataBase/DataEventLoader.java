package com.privatevaults.dataBase;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class DataEventLoader implements Listener {


    @EventHandler
    public static void dataLoadEvent(PlayerLoginEvent e){
        //Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED+"Player "+e.getPlayer().getDisplayName()+" joined the server.");
        DataMethod.loadPlayerData(e.getPlayer());

    }

    @EventHandler
    public static void dataUnloadEvent(PlayerQuitEvent e){
        //Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED+"Player "+e.getPlayer().getDisplayName()+" left the server.");
        DataMethod.savePlayerData(e.getPlayer());
    }

}
