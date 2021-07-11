package com.privatevaults.commands;

import com.privatevaults.dataBase.DataMethod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.privatevaults.PrivateVaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OpenVault implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){ sender.sendMessage(ChatColor.RED+"Only players can use this command!"); return true;}
        Player player = (Player) sender;
        List<String> table_names = DataMethod.getTableList();
        if(args.length==1){
            if(table_names.contains(args[0])){
                if(player.hasPermission(DataMethod.getPermission(args[0])) || player.hasPermission("PrivateVaults.Open.invsee")){
                    player.openInventory(DataMethod.getInventory(args[0],player));
                    return true;
                }else{
                    sender.sendMessage(ChatColor.RED +"You don't have permission to do this.");
                }
            }

        }else if(args.length==2){
            if(player.hasPermission("PrivateVaults.open.invsee")) {
                if (table_names.contains(args[0])) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        player.openInventory(DataMethod.getInventory(args[0], target));
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player is offline or the name is wrong.");
                    }
                }
            }else{
                sender.sendMessage(ChatColor.RED +"You don't have permission to do this.");
            }
        }

        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(!(sender instanceof Player)){return null;}



        if(args.length==1) {
            List<String> table_names = DataMethod.getTableList();
            if (args[0].length() >= 1) {
                List<String> completion = new ArrayList<>();
                for (String table_name : table_names) {
                    if (table_name.toLowerCase().startsWith(args[0].toLowerCase().substring(0, 1))) {
                        completion.add(table_name);
                    }
                }


                return completion;
            }else{
                return table_names;
            }
        }else if(args.length==2){
            List<String> player_names = new ArrayList<>();
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            for(Player player : players){
                player_names.add(player.getName());
            }
            if(args[1].length()>=1){
                player_names.removeIf(name -> !name.toLowerCase().startsWith(args[1].toLowerCase().substring(0, 1)));
            }
            return player_names;
        }



        return null;
    }
}
