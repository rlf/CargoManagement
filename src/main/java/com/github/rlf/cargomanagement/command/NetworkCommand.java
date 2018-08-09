package com.github.rlf.cargomanagement.command;

import com.github.rlf.cargomanagement.model.CargoNetwork;
import com.github.rlf.cargomanagement.storage.BlockStorage;
import dk.lockfuglsang.minecraft.command.AbstractCommand;
import dk.lockfuglsang.minecraft.command.CompositeCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static dk.lockfuglsang.minecraft.po.I18nUtil.marktr;
import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

public class NetworkCommand extends CompositeCommand {
    public NetworkCommand(BlockStorage storage) {
        super("network", "cargo.network", marktr("shows information about your networks"));
        add(new AbstractCommand("list|l", "cargo.network.list", marktr("lists the networks")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... strings) {
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(tr("This command can only be executed in-game"));
                    return false;
                }
                Player player = (Player) commandSender;
                CargoNetwork network = storage.getNetwork(player);
                List<String> messages = new ArrayList<>();
                messages.add(tr("Networks:"));
                messages.addAll(network.getConnectedNets().stream().map(m -> tr("{0} - {1}", m.isOnline() ? tr("\u00a72ONLINE") : tr("\u00a4OFFLINE"), m.toString())).collect(Collectors.toList()));
                player.sendMessage(messages.toArray(new String[0]));
                return true;
            }
        });
        add(new AbstractCommand("tick", "cargo.network.tick", marktr("ticks the current players networks")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... strings) {
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(tr("This command can only be executed in-game"));
                    return false;
                }
                Player player = (Player) commandSender;
                CargoNetwork network = storage.getNetwork(player);
                network.tick();
                player.sendMessage(tr("Ticked network"));
                return true;
            }
        });
        add(new AbstractCommand("tickall", "cargo.network.tickall", marktr("ticks all active networks")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... strings) {
                storage.tick();
                commandSender.sendMessage(tr("Ticked all active networks"));
                return true;
            }
        });
        add(new AbstractCommand("clear", "cargo.network.clear", "?player", marktr("clears all the networks for this player")) {
            @Override
            public boolean execute(CommandSender commandSender, String alias, Map<String, Object> map, String... args) {
                String playerName = args.length > 0 ? args[0] : commandSender.getName();
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
                if (offlinePlayer != null) {
                    UUID playerId = offlinePlayer.getUniqueId();
                    if (playerId != null) {
                        storage.remove(playerId);
                        commandSender.sendMessage(tr("Cleared all networks for {0}", playerName));
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
