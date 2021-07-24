package com.privatevaults.GUI;

import com.privatevaults.PrivateVaults;
import com.privatevaults.dataBase.DataMethod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class VaultGUI {
    private static final List<Integer> slots = new ArrayList<>();
    private static final List<Integer> outSlots = new ArrayList<>();
    private static final List<String> noPermLore = new ArrayList<>();
    public static void setup(){
        for (int column = 1; column  < 8; column++) {
            for (int row = 1; row < 4; row++) {
                int slot = (row * 9) + column;
                slots.add(slot);
            }
        }

        slots.sort((o1, o2) -> {
            if(o1<o2){
                return -1;
            }else if(o1.equals(o2)){
                return 0;
            }else{
                return 1;
            }
        });

        for(int i = 0; i<45 ; i++){
            outSlots.add(i);
        }
        outSlots.removeAll(slots);

        noPermLore.add(ChatColor.RED+"You don't have permission to open vault.");

    }

    public static void openGUI(Player player){

        Inventory inventory = Bukkit.createInventory(player,45, ChatColor.RED+"Private Vaults");
        List<String> tableList = DataMethod.getTableList();

        List<HasPerm> perms = new ArrayList<>();

        for(String table_name : tableList){
            boolean hasPerm = player.hasPermission(DataMethod.getPermission(table_name));
            HasPerm table_perm = new HasPerm(table_name,hasPerm);
            perms.add(table_perm);
        }

        perms.sort((o1, o2) -> {

            Boolean y1 = o1.hasPerm();
            Boolean y2 = o2.hasPerm();
            int bComp = y2.compareTo(y1);

            if (bComp != 0) {
                return bComp;
            }

            String x1 = o1.getTable_name();
            String x2 = o2.getTable_name();
            return x1.compareTo(x2);

        });

        for(int i = 0 ; i < 21 ; i++){

            try {
                HasPerm perm = perms.get(i);
                String tableName = perm.getTable_name();
                if(player.hasPermission(DataMethod.getPermission(tableName))) {
                    inventory.setItem(slots.get(i), createItem(tableName, Material.CHEST_MINECART));
                }else{
                    inventory.setItem(slots.get(i), createItem(tableName, Material.TNT_MINECART,noPermLore));
                }
            }catch (IndexOutOfBoundsException e){

                inventory.setItem(slots.get(i),
                        createItem(ChatColor.GRAY+"Unsused", Material.MINECART));

            }
        }


        //Adds the glass panes
        for(Integer i : outSlots){
            ItemStack pane = createItem(" ",Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            inventory.setItem(i,pane.clone());
        }




        player.openInventory(inventory);

    }

    private static ItemStack createItem(String title,Material type, List<String> lore){
        ItemStack item = createItem(title,type);
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createItem(String title, Material type){
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA+title);

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(PrivateVaults.getInstance(),"table_name");
        pdc.set(key,PersistentDataType.STRING,title);


        item.setItemMeta(meta);
        return item;
    }

    static class HasPerm {
        private final String table_name;
        private final boolean hasPerm;
        public HasPerm(String table_name, boolean hasPerm) {
            this.table_name = table_name;
            this.hasPerm = hasPerm;
        }

        public String getTable_name() {
            return table_name;
        }

        public boolean hasPerm() {
            return hasPerm;
        }

    }
}
