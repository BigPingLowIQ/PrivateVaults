package com.privatevaults.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import com.privatevaults.PrivateVaults;
import com.privatevaults.dataBase.DataMethod;

import java.util.ArrayList;
import java.util.List;

public class AdminPVRemove implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            remove(sender,args);
            return true;
        }

        Player player = (Player) sender;
        if(player.hasPermission("PrivateVaults.admin.remove")){
            remove(sender,args);
        }

        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length==1) {
            return DataMethod.getTableList();
        }
        return new ArrayList<>();
    }

    private void remove(CommandSender sender, String[] args) {
        if(args.length==1) {
            if (DataMethod.removeTable(args[0])) {
                sender.sendMessage(ChatColor.GREEN + "Inventory successfully removed.");
            }else{
                sender.sendMessage(ChatColor.RED + "Failed to remove inventory, the name provided was not found.");
            }
        }else{
            sender.sendMessage(ChatColor.RED + "Please provide a name for the inventory.");
        }
    }
}
