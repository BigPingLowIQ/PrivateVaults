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
import java.util.regex.Pattern;

public class AdminPVAdd implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            add(sender,args);
            return true;
        }

        Player player = (Player) sender;
        if(player.hasPermission("PrivateVaults.admin.add")){
            add(sender,args);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if(args.length==1){
            list.add("<inventory_name>");
        }else if(args.length==2){
            list.add("<inventory_title>");
        }else if(args.length==3){
            list.add("9");
            list.add("18");
            list.add("27");
            list.add("36");
            list.add("45");
            list.add("54");
        }

        return list;
    }

    private void add(CommandSender sender, String[] args) {
        if(args.length==3) {
            String inventory_name = args[0];
            String inventory_title = args[1];
            String slots = args[2];
            if(!inventory_name.equalsIgnoreCase("<inventory_name>")) {
                if(Pattern.matches("[a-zA-Z_][\\w]{0,19}", inventory_name)){
                    if (!inventory_title.equalsIgnoreCase("<inventory_title>")) {
                        switch (slots) {
                            case "9":
                            case "18":
                            case "27":
                            case "36":
                            case "45":
                            case "54":
                                if (DataMethod.createNewTable(inventory_name, inventory_title, Integer.parseInt(slots))) {
                                    sender.sendMessage(ChatColor.GREEN + "Inventory successfully added.");
                                } else {
                                    sender.sendMessage(ChatColor.RED + "Failed to add inventory.");
                                }
                                break;
                            default:
                                sender.sendMessage(ChatColor.RED + "Please provide a valid inventory size.");
                        }

                    } else {
                        sender.sendMessage(ChatColor.RED + "Please provide an inventory title.");
                    }
                }else{
                    sender.sendMessage(ChatColor.RED+"Inventory name provided can't be used.");
                }
            }else{
                sender.sendMessage(ChatColor.RED+"Please provide an inventory name.");
            }
        }else{
            sender.sendMessage(ChatColor.RED+"Please provide all arguments.");
        }
    }
}
