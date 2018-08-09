package com.github.rlf.cargomanagement.command;

import com.github.rlf.cargomanagement.item.CargoNodeFactory;
import dk.lockfuglsang.minecraft.command.CompositeCommand;
import dk.lockfuglsang.minecraft.command.completion.AbstractTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dk.lockfuglsang.minecraft.po.I18nUtil.marktr;
import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

public class GiveCommand extends CompositeCommand {
    private CargoNodeFactory nodeFactory;

    public GiveCommand(CargoNodeFactory nodeFactory) {
        super("give", "cargo.give", "node-type ?amount ?player", marktr("gives the player a cargo-item"));
        this.nodeFactory = nodeFactory;
        addTab("node-type", new AbstractTabCompleter() {
            @Override
            protected List<String> getTabList(CommandSender commandSender, String s) {
                return Arrays.asList("connector", "input", "output");
            }
        });
    }

    @Override
    public boolean execute(CommandSender commandSender, String alias, Map<String, Object> map, String... args) {
        if (!(commandSender instanceof Player) && args.length < 3) {
            commandSender.sendMessage(tr("player-name is required when executing as console"));
            return false;
        }
        String nodeType = args.length > 0 ? args[0].toLowerCase() : "connector";
        int amount = args.length > 1 && args[1].matches("[0-9]+") ? Integer.parseInt(args[1], 10) : 1;
        String playerName = args.length > 2 ? args[2] : commandSender.getName();
        ItemStack itemStack = null;
        switch (nodeType) {
            case "connector":
                itemStack = nodeFactory.createConnectorNode();
                break;
            case "input":
                itemStack = nodeFactory.createInputNode();
                break;
            case "output":
                itemStack = nodeFactory.createOutputNode();
                break;
            default:
                commandSender.sendMessage(tr("Unknown type of node {0}", nodeType));
                return false;
        }
        itemStack.setAmount(amount);
        Player player = Bukkit.getPlayer(playerName);
        if (itemStack != null && player != null && player.isOnline()) {
            HashMap<Integer, ItemStack> rest = player.getInventory().addItem(itemStack);
            if (rest.isEmpty()) {
                commandSender.sendMessage(tr("Gave you a {0}-node", tr(nodeType)));
            } else {
                commandSender.sendMessage(tr("Inventory is full"));
            }
        } else {
            commandSender.sendMessage(tr("Player {0} is not valid or not online", playerName));
        }
        return true;
    }
}
