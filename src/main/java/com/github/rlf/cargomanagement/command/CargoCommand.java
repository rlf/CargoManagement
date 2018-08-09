package com.github.rlf.cargomanagement.command;

import com.github.rlf.cargomanagement.item.CargoNodeFactory;
import com.github.rlf.cargomanagement.storage.BlockStorage;
import dk.lockfuglsang.minecraft.command.AbstractCommand;
import dk.lockfuglsang.minecraft.command.BaseCommandExecutor;
import dk.lockfuglsang.minecraft.command.DocumentCommand;
import dk.lockfuglsang.minecraft.command.LanguageCommand;
import dk.lockfuglsang.minecraft.file.FileUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import static dk.lockfuglsang.minecraft.po.I18nUtil.marktr;
import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

public class CargoCommand extends BaseCommandExecutor {
    private JavaPlugin plugin;

    public CargoCommand(JavaPlugin plugin, BlockStorage storage, CargoNodeFactory nodeFactory) {
        super("cargo", "cargo.use", marktr("access the cargo command-line"));
        this.plugin = plugin;
        add(new AbstractCommand("reload", "cargo.reload", marktr("reloads cargo configuration")) {
            @Override
            public boolean execute(CommandSender commandSender, String alias, Map<String, Object> map, String... args) {
                plugin.reloadConfig();
                commandSender.sendMessage(tr("CargoManagement configuration reloaded"));
                return true;
            }
        });
        add(new AbstractCommand("save", "cargo.save", marktr("saves the current networks to disk")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... strings) {
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        storage.save();
                        commandSender.sendMessage(tr("Networks saved to file"));
                    } catch (IOException e) {
                        plugin.getLogger().log(Level.WARNING, "Unable to save networks", e);
                    }
                });
                return true;
            }
        });
        add(new AbstractCommand("load", "cargo.load", marktr("loads networks from disk")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... strings) {
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    storage.load(FileUtil.getConfigFile("networks.yml"));
                    commandSender.sendMessage(tr("Networks loaded from file"));
                });
                return true;
            }
        });
        add(new NetworkCommand(storage));
        add(new GiveCommand(nodeFactory));
        add(new DocumentCommand(plugin, "doc", "cargo.doc"));
        add(new LanguageCommand(plugin, "lang", "cargo.lang"));
        addFeaturePermission("cargo.recipe.connector", tr("permission to craft the connector recipe"));
        addFeaturePermission("cargo.recipe.input", tr("permission to craft the input recipe"));
        addFeaturePermission("cargo.recipe.output", tr("permission to craft the output recipe"));
    }
}
