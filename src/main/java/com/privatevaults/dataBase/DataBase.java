package com.privatevaults.dataBase;

import com.privatevaults.PrivateVaults;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import javax.xml.crypto.Data;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

final class DataBase {
    private static String url;
    private static ScheduledFuture<?> dataBaseSaverScheduler = null;
    private static final HashMap<String,HashMap<String,Inventory>> inventory_tables = new HashMap<>();
    private static List<String> table_names = new ArrayList<>();


    public static void setup(){
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:sqlite:");
        String path = Bukkit.getServer().getPluginManager().getPlugin("PrivateVaults").getDataFolder().getAbsolutePath();
        sb.append(path);
        sb.append(File.separator).append("privateVaults.db");
        url = sb.toString();

        setupScheduler();
        refreshTableList();
        loadTables();
        loadDataForCurrentPlayers();


    }

    //Saves all data on server shutdown
    public static boolean saveAllData(){
        Set<String> table_names = inventory_tables.keySet();
        for(String table_name : table_names){
            HashMap<String, Inventory> table = inventory_tables.get(table_name);
            Set<String> keys = table.keySet();
            for(String key : keys){
                upsert(table.get(key),key,table_name);
            }
        }
        return true;
    }

    private static void loadDataForCurrentPlayers(){
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for(Player player : players){
            loadPlayerData(player);
        }

    }


    //Saves a player's data whenever he leaves the server
    public static void savePlayerData(Player player){
        List<String> table_names = getTableList();
        List<HumanEntity> allHumans = new ArrayList<>();




        for(String table_name : table_names){
            Inventory inventory = getInventory(table_name,player);
            if(inventory==null){
                inventory = Bukkit.createInventory(null,DataConfig.getSlots(table_name),DataConfig.getName(table_name));
            }
            allHumans.addAll(inventory.getViewers());
            upsert(inventory,player.getUniqueId().toString(),table_name);
            inventory_tables.get(table_name).remove(player.getUniqueId().toString());
        }
        for(HumanEntity e : allHumans){
            e.closeInventory();
        }



    }
    //Loads a player's data whenever he joins the server

    public static boolean loadPlayerData(Player player){
        List<String> table_names = getTableList();

        for(String table_name : table_names){
            try {
                Connection connection = connect();
                String sql = "SELECT inventory FROM \""+table_name+"\" WHERE uuid = ?"; //ON CONFLICT (uuid) DO INSERT INTO "+table_name+" (inventory,uuid) values(?,?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1,player.getUniqueId().toString());
                ResultSet rs = statement.executeQuery();
                Inventory inventory;
                if(rs.next()) {
                    inventory = decode(rs.getString(1),table_name);
                }else{
                    inventory = Bukkit.createInventory(null,DataConfig.getSlots(table_name),DataConfig.getName(table_name));
                }
                inventory_tables.get(table_name).put(player.getUniqueId().toString(),inventory);

                connection.close();
            } catch (SQLException e) { e.printStackTrace(); }

        }
        return true;
    }


    // Tries to insert new data in the database if it fails to because it already exists then it tries to update it

    private static void upsert(Inventory inventory, String uuid, String table_name){
        Connection connection = connect();
        if(connection!=null){
            boolean hasInserted = true;
            String sql = "INSERT INTO \""+table_name+"\"(inventory,uuid) values(?,?)"; //ON CONFLICT (uuid) DO UPDATE SET inventory = excluded.inventory";

            String encodedInventory = encode(inventory);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {


                statement.setObject(1,encodedInventory);
                statement.setString(2,uuid);

                statement.execute();


            } catch (SQLException e) { hasInserted = false;}

            if(!hasInserted){
                sql = "UPDATE \""+table_name+"\" SET inventory = ? WHERE uuid = ?";

                try (PreparedStatement statement = connection.prepareStatement(sql)) {


                    statement.setObject(1,encodedInventory);
                    statement.setString(2,uuid);

                    statement.execute();


                } catch (SQLException e) { e.printStackTrace();}
            }

        }

    }

    private static void refreshTableList(){
        Connection connection = connect();
        if(connection!=null){
            String sql = "SELECT name FROM sqlite_master " +
                    "WHERE type='table' " +
                    "ORDER BY name;";
            try(Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)){
                List<String> tables=new ArrayList<>();
                while(rs.next()){
                    tables.add(rs.getString(1));
                }
                table_names=tables;

            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public static List<String> getTableList(){ return table_names; }

    public static boolean resetTableData(String table_name){
        String inventory_name = DataConfig.getName(table_name);
        int slots = DataConfig.getSlots(table_name);
        if(removeTable(table_name) && createNewTable(table_name,inventory_name,slots)) {
            return true;
        }

        return false;
    }

    public static boolean removeTable(String table_name) {
        if(table_names.contains(table_name)){
            String sql = "DROP TABLE IF EXISTS \""+table_name.toLowerCase()+"\"";
            try(Connection connection = connect();
                Statement statement = connection.createStatement()
                ){
                statement.execute(sql);
            }catch (SQLException e){ e.printStackTrace();}
            refreshTableList();
            Bukkit.getPluginManager().removePermission(DataConfig.getPermission(table_name));
            DataConfig.removeConfig(table_name);
            inventory_tables.remove(table_name);
            return true;
        }
        return false;
    }

    public static boolean createNewTable(String table_name,String inventory_name,int slots){
        Connection connection = connect();
        if(connection!=null && !table_names.contains(table_name.toLowerCase())){
            String sql = "CREATE TABLE IF NOT EXISTS \""+table_name.toLowerCase()+"\" (uuid String NOT NULL PRIMARY KEY, inventory Object NOT NULL);";
            Statement statement = null;
            try {
                statement = connection.createStatement();
                statement.execute(sql);

            } catch (SQLException e) { e.printStackTrace(); }
            refreshTableList();
            loadTables();

            DataConfig.addConfig(table_name.toLowerCase(),inventory_name.replace("_"," "),slots);
            Bukkit.getPluginManager().addPermission(new Permission(DataMethod.getPermission(table_name.toLowerCase())));



            Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            inventory_tables.put(table_name.toLowerCase().toLowerCase(),new HashMap<>());
            for(Player player : players){
                inventory_tables.get(table_name.toLowerCase()).put(player.getUniqueId().toString(),Bukkit.createInventory(null,slots,inventory_name.replace("_"," ")));
            }

            return true;
        }
        return false;
    }

    private static void loadTables() {
        List<String> table_names = getTableList();
        for(String table_name : table_names){
            inventory_tables.putIfAbsent(table_name,new HashMap<>());
        }
    }

    public static Inventory getInventory(String table_name,Player player){
        return inventory_tables.get(table_name).get(player.getUniqueId().toString());
    }

    //Establishes a connection with the database
    private static Connection connect(){
        Connection conn = null;
        try{

            conn = DriverManager.getConnection(url);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return conn;
    }

    //Encodes the player inventory data into Base64 encoded byte array
    private static String encode(Inventory inventory) {
        try {
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
            List<ItemStack> items = Arrays.asList(inventory.getContents());
            os.writeInt(items.size());
            for(int i = 0 ; i < items.size() ; i++){
                os.writeObject(items.get(i));
            }
            os.flush();

            byte[] rawData = io.toByteArray();
            String encodedData = Base64.getEncoder().encodeToString(rawData);

            os.close();

            return encodedData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    //Decodes the player data into inventory
    private static Inventory decode(String encodedData,String table_name) {

        List<ItemStack> items = new ArrayList<>();
        Inventory inventory = Bukkit.createInventory(null,DataConfig.getSlots(table_name),DataConfig.getName(table_name));

        if(!encodedData.isEmpty()){
            byte[] rawData = Base64.getDecoder().decode(encodedData);
            try {
                ByteArrayInputStream io = new ByteArrayInputStream(rawData);
                BukkitObjectInputStream in = new BukkitObjectInputStream(io);

                int itemCount = in.readInt();

                for(int i = 0 ; i < itemCount ; i++){
                    items.add((ItemStack) in.readObject());
                }

                in.close();

                inventory.setContents(items.toArray(new ItemStack[0]));
                return inventory;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return inventory;
    }

    private static void setupScheduler(){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable saveData = DataBase::saveAllData;
        dataBaseSaverScheduler = scheduler.scheduleAtFixedRate(saveData, 15,15, TimeUnit.MINUTES);
    }


    public static void cancelScheduler(){
        if(dataBaseSaverScheduler!=null) {
            dataBaseSaverScheduler.cancel(false);
        }
    }


}
