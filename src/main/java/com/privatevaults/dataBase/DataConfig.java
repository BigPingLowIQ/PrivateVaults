package com.privatevaults.dataBase;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import com.privatevaults.PrivateVaults;

import java.util.List;

class DataConfig {

    public static void setup() {
        List<String> table_names = DataBase.getTableList();
        for(String table_name : table_names){
            Bukkit.getPluginManager().addPermission(new Permission(getPermission(table_name)));
        }

    }

    public static String getName(String table_name){
        FileConfiguration fileConfiguration = PrivateVaults.getInstance().getConfig();
        return fileConfiguration.getString(table_name+".name");
    }

    public static int getSlots(String table_name){
        FileConfiguration fileConfiguration = PrivateVaults.getInstance().getConfig();
        return fileConfiguration.getInt(table_name+".slots");
    }

    public static String getPermission(String table_name){
        FileConfiguration fileConfiguration = PrivateVaults.getInstance().getConfig();
        return fileConfiguration.getString(table_name+".permission");
    }

    public static void addConfig(String table_name,String name, int slots){
        FileConfiguration fileConfiguration = PrivateVaults.getInstance().getConfig();

        fileConfiguration.set(table_name+".permission","PrivateVaults.vault."+table_name);
        fileConfiguration.set(table_name+".name",name);
        fileConfiguration.set(table_name+".slots",slots);
        PrivateVaults.getInstance().saveConfig();
    }

    public static void removeConfig(String table_name){
        FileConfiguration fileConfiguration = PrivateVaults.getInstance().getConfig();

        fileConfiguration.set(table_name,null);
        PrivateVaults.getInstance().saveConfig();
    }


}