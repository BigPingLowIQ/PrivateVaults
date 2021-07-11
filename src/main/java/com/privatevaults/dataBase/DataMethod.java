package com.privatevaults.dataBase;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class DataMethod {

    public static List<String> getTableList(){
        return DataBase.getTableList();
    }

    public static boolean resetTableData(String table_name){
        return DataBase.resetTableData(table_name);
    }

    public static void cancelScheduler(){
        DataBase.cancelScheduler();
    }

    public static boolean removeTable(String table_name){
        return DataBase.removeTable(table_name);
    }

    public static boolean loadPlayerData(Player player){
        return DataBase.loadPlayerData(player);
    }

    public static boolean createNewTable(String table_name,String inventory_name,int slots){
        return DataBase.createNewTable(table_name,inventory_name,slots);
    }

    public static Inventory getInventory(String table_name,Player player){
        return DataBase.getInventory(table_name,player);
    }

    public static boolean saveAllData(){
        return DataBase.saveAllData();
    }

    public static void savePlayerData(Player player){
        DataBase.savePlayerData(player);
    }

    public static String getPermission(String table_name){
        return DataConfig.getPermission(table_name);
    }
}
