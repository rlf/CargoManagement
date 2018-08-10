package com.github.rlf.cargomanagement;

import com.github.rlf.cargomanagement.command.CargoCommand;
import com.github.rlf.cargomanagement.events.BlockListener;
import com.github.rlf.cargomanagement.events.CargoNodePlaceListener;
import com.github.rlf.cargomanagement.events.PlayerListener;
import com.github.rlf.cargomanagement.item.CargoNodeFactory;
import com.github.rlf.cargomanagement.item.CargoNodeRecipeFactory;
import com.github.rlf.cargomanagement.model.ConnectorNode;
import com.github.rlf.cargomanagement.storage.MemoryBlockStorage;
import dk.lockfuglsang.minecraft.file.FileUtil;
import dk.lockfuglsang.minecraft.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class CargoManagement extends JavaPlugin {
    private MemoryBlockStorage storage;
    private BukkitTask tickTask;
    private CargoNodeFactory nodeFactory;

    @Override
    public void onEnable() {
        FileUtil.setDataFolder(getDataFolder());
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        initializePlugin();
    }

    private void initializePlugin() {
        FileConfiguration config = getConfig();
        ConnectorNode.RANGE = config.getInt("cargonodes.connector.range", 8);
        PluginManager pluginManager = getServer().getPluginManager();
        nodeFactory = new CargoNodeFactory(config);
        storage = new MemoryBlockStorage();
        storage.load(FileUtil.getConfigFile("networks.yml"));
        Bukkit.getOnlinePlayers().stream().forEach(p -> storage.activate(p));
        pluginManager.registerEvents(new PlayerListener(storage, nodeFactory), this);
        pluginManager.registerEvents(new BlockListener(this, storage, nodeFactory), this);
        pluginManager.registerEvents(new CargoNodePlaceListener(storage), this);
        getCommand("cargo").setExecutor(new CargoCommand(this, storage, nodeFactory));
        configureTickTimer(config);
        configureRecipes(config, nodeFactory);
    }

    private void configureRecipes(FileConfiguration config, CargoNodeFactory nodeFactory) {
        if (config.getBoolean("recipes.enabled", true)) {
            getServer().addRecipe(CargoNodeRecipeFactory.createRecipe(this, config.getConfigurationSection("recipes.connector"), nodeFactory));
            getServer().addRecipe(CargoNodeRecipeFactory.createRecipe(this, config.getConfigurationSection("recipes.input"), nodeFactory));
            getServer().addRecipe(CargoNodeRecipeFactory.createRecipe(this, config.getConfigurationSection("recipes.output"), nodeFactory));
        }
    }

    private void configureTickTimer(FileConfiguration config) {
        long tickTimerPeriod = TimeUtil.millisAsTicks(config.getInt("tickTimer.everyMs", 2000));
        if (config.getBoolean("tickTimer.enabled", true)) {
            tickTask = getServer().getScheduler().runTaskTimer(this, () -> {
                storage.tick();
                if (storage.isDirty()) {
                    getServer().getScheduler().runTaskAsynchronously(this, () -> {
                        try {
                            storage.save();
                        } catch (IOException e) {
                            getLogger().log(Level.WARNING, "Error saving network", e);
                        }
                    });
                }
            }, tickTimerPeriod, tickTimerPeriod);
        } else {
            tickTask = null;
        }
    }

    @Override
    public void onDisable() {
        if (tickTask != null) {
            tickTask.cancel();
        }
        tickTask = null;
        if (storage != null) {
            try {
                storage.save();
            } catch (IOException e) {
                getLogger().log(Level.WARNING, "Unable to save networks", e);
            }
        }
        unregisterRecipes();
        nodeFactory = null;
    }

    private void unregisterRecipes() {
        for (Iterator<Recipe> it = getServer().recipeIterator(); it.hasNext(); ) {
            Recipe recipe = it.next();
            if (recipe instanceof ShapedRecipe && ((ShapedRecipe) recipe).getKey().getNamespace().equals(getName().toLowerCase())) {
                it.remove();
            }
        }
    }

    @Override
    public void reloadConfig() {
        FileUtil.reload();
        onDisable();
        initializePlugin();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return command.execute(sender, label, args);
    }

    @Override
    public FileConfiguration getConfig() {
        return FileUtil.getYmlConfiguration("config.yml");
    }
}
