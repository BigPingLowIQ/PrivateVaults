package com.privatevaults;


import com.privatevaults.commands.AdminPVAdd;
import com.privatevaults.commands.AdminPVRemove;
import com.privatevaults.commands.AdminPVReset;
import com.privatevaults.commands.OpenVault;
import com.privatevaults.dataBase.DataEventLoader;
import com.privatevaults.dataBase.DataMethod;
import com.privatevaults.dataBase.DataSetup;

import org.bukkit.plugin.java.JavaPlugin;


public final class PrivateVaults extends JavaPlugin {
    private static PrivateVaults instance;


    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        new UpdateChecker(this,94151);

        int pluginId = 12089;
        Metrics metrics = new Metrics(instance,pluginId);

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        DataSetup.setup();


        getCommand("PrivateVaultsAdd").setExecutor(new AdminPVAdd());
        getCommand("PrivateVaultsRemove").setExecutor(new AdminPVRemove());
        getCommand("PrivateVaultsReset").setExecutor(new AdminPVReset());
        getCommand("open").setExecutor(new OpenVault());

        getServer().getPluginManager().registerEvents(new DataEventLoader(),instance);




    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        DataMethod.cancelScheduler();
        DataMethod.saveAllData();

    }

    public static PrivateVaults getInstance(){ return instance; }
}
