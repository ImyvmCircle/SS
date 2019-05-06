package com.imyvm.ss;

import com.meowj.langutils.lang.LanguageHelper;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huang on 2017/8/11.
 */
public class Main extends JavaPlugin{


    FileConfiguration config = getConfig();

    @Override
    //方法 - 插件启动时.
    public void onEnable() {
        config.addDefault("message", "{player} 正在展示 &b[&r {itemName} &e*&r {amount} &b]&r");
        config.addDefault("noiteminhandmessage", "{player} 正在展示 &eTA的难看的手");
        config.options().copyDefaults(true);
        saveConfig();
    }

    @Override
    //方法 - 插件关闭时.
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ss")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("You are not a player!");
                return false;
            }
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item != null && !item.getType().equals(Material.AIR)) {
                String Message = config.getString("message");
                Message = ChatColor.translateAlternateColorCodes('&', Message);
                Message = Message.replace("{player}", sender.getName());
                String name = LanguageHelper.getItemName(item, player);
                sendItemTooltipMessage(Message, name, item);
            }else {
                String Message2 = config.getString("noiteminhandmessage");
                Message2 = ChatColor.translateAlternateColorCodes('&', Message2);
                Message2 = Message2.replace("{player}", sender.getName());
                Bukkit.broadcastMessage(Message2);
            }
            return true;
        }

        //默认返回false
        return false;
    }

    public void sendItemTooltipMessage(String message, String name, ItemStack item) {
        String itemJson = convertItemStackToJson(item);

        // Prepare a BaseComponent array with the itemJson as a text component
        BaseComponent[] hoverEventComponents = new BaseComponent[]{
                new TextComponent(itemJson) // The only element of the hover events basecomponents is the item json
        };

        // Create the hover event
        HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);

        /* And now we create the text component (this is the actual text that the player sees)
         * and set it's hover event to the item event */
        Map<String, BaseComponent> varMap = new HashMap<>();
        TextComponent component_name = new TextComponent(name);
        component_name.setHoverEvent(event);

        int amount = item.getAmount();
        String message1 = message.substring(0, message.indexOf("{itemName}"));
        String message2 = message.substring(message.indexOf("{amount}") + "{amount}".length());
        TextComponent SendMessage = new TextComponent(message1);
        if (amount == 1) {
            SendMessage.addExtra(component_name);
            SendMessage.addExtra(message2);
        } else {
            SendMessage.addExtra(component_name);
            SendMessage.addExtra("*" + amount);
            SendMessage.addExtra(message2);
        }
        // Finally, broadcast the message
        getServer().spigot().broadcast(SendMessage);
    }

    private String convertItemStackToJson(ItemStack itemStack) {
        // First we convert the item stack into an NMS itemstack
        net.minecraft.server.v1_14_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        net.minecraft.server.v1_14_R1.NBTTagCompound compound = new NBTTagCompound();
        compound = nmsItemStack.save(compound);

        return compound.toString();
    }


}