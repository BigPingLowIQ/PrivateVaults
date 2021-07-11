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

public class AdminPVReset implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            reset(sender,args);
            return true;
        }

        Player player = (Player) sender;
        if (player.hasPermission("PrivateVaults.admin.reset")) {
            reset(sender,args);
        }else{
            sender.sendMessage(ChatColor.RED+"You don't have permission to do this!");
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

    private void reset(CommandSender sender,String[] args){
        if (args.length == 1) {

            if (DataMethod.getTableList().contains(args[0]) && DataMethod.resetTableData(args[0])) {
                sender.sendMessage(ChatColor.GREEN + "Inventory successfully reseted.");
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to reset inventory.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Please provide a name for the inventory.");
        }
    }
}
