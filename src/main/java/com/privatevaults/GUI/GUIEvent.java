package com.privatevaults.GUI;

import com.privatevaults.PrivateVaults;
import com.privatevaults.dataBase.DataMethod;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class GUIEvent implements Listener {

    @EventHandler
    public static void InventoryClickEvent(InventoryClickEvent e){
        if(e!=null) {
            String title = e.getView().getTitle();
            if (title.equals(ChatColor.RED + "Private Vaults")) {
                e.setCancelled(true);
                ItemStack clickedItem = e.getCurrentItem();
                if(clickedItem!=null) {
                    String table_name;
                    PersistentDataContainer pdc = clickedItem.getItemMeta().getPersistentDataContainer();
                    NamespacedKey key = new NamespacedKey(PrivateVaults.getInstance(), "table_name");
                    table_name = pdc.get(key, PersistentDataType.STRING);


                    Player player = (Player) e.getWhoClicked();
                    if (table_name != null) {
                        if (isVault(table_name)) {

                            if (player.hasPermission(DataMethod.getPermission(table_name)) || player.hasPermission("PrivateVaults.Open.invsee")) {

                                player.openInventory(DataMethod.getInventory(table_name, player));
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.5f, 0.8f);

                            } else {

                                player.sendMessage(ChatColor.RED + "You don't have permission to do this.");
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.3f, 0.7f);
                                player.closeInventory();

                            }
                        } else if (table_name.equalsIgnoreCase(ChatColor.GRAY + "Unsused")) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.3f, 0.7f);
                        }
                    }
                }
            }
        }
    }

    private static boolean isVault(String name){
        List<String> names = DataMethod.getTableList();
        return names.contains(name);
    }

}
