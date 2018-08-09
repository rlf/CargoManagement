package com.github.rlf.cargomanagement.storage;

import com.github.rlf.cargomanagement.model.CargoNetwork;
import com.github.rlf.cargomanagement.model.CargoNode;
import com.github.rlf.cargomanagement.model.ConnectorNode;
import com.github.rlf.cargomanagement.model.InputNode;
import com.github.rlf.cargomanagement.model.OutputNode;
import dk.lockfuglsang.minecraft.file.FileUtil;
import dk.lockfuglsang.minecraft.util.ItemStackUtil;
import dk.lockfuglsang.minecraft.util.LocationUtil;
import dk.lockfuglsang.minecraft.yml.YmlConfiguration;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MemoryBlockStorage implements BlockStorage {
    private final Map<UUID, Map<String,CargoNetwork>> storage = new ConcurrentHashMap<>();
    private long lastSavedHash;
    private final Set<UUID> activePlayers = new HashSet<>();
    private File file;

    public MemoryBlockStorage() {
    }

    @Override
    public void activate(Player player) {
        if (player != null && player.isOnline()) {
            activePlayers.add(player.getUniqueId());
        }
    }

    @Override
    public void deactivate(Player player) {
        if (player != null) {
            activePlayers.remove(player.getUniqueId());
        }
    }

    @Override
    public CargoNetwork add(Player player, CargoNode node) {
        if (node instanceof ConnectorNode) {
            return  add(player, (ConnectorNode) node);
        } else if (node instanceof  InputNode) {
            return add(player, (InputNode) node);
        } else if (node instanceof OutputNode) {
            return add(player, (OutputNode) node);
        }
        return null;
    }

    @Override
    public CargoNetwork add(Player player, ConnectorNode node) {
        UUID uniqueId = player.getUniqueId();
        String world = node.getLocation().getWorld().getName();
        CargoNetwork cargoNetwork = storage.computeIfAbsent(uniqueId, f -> new ConcurrentHashMap<>())
                .computeIfAbsent(world, f -> new CargoNetwork(world));
        cargoNetwork.add(node);
        return cargoNetwork;
    }

    @Override
    public CargoNetwork add(Player player, InputNode node) {
        UUID uniqueId = player.getUniqueId();
        String world = node.getLocation().getWorld().getName();
        return storage.computeIfAbsent(uniqueId, f -> new ConcurrentHashMap<>())
                .computeIfAbsent(world, f -> new CargoNetwork(world))
                .add(node);
    }

    @Override
    public CargoNetwork add(Player player, OutputNode node) {
        UUID uniqueId = player.getUniqueId();
        String world = node.getLocation().getWorld().getName();
        return storage.computeIfAbsent(uniqueId, f -> new ConcurrentHashMap<>())
                .computeIfAbsent(world, f -> new CargoNetwork(world))
                .add(node);
    }

    @Override
    public CargoNetwork remove(Player player, ConnectorNode node) {
        UUID uniqueId = player.getUniqueId();
        String world = node.getLocation().getWorld().getName();
        return storage.computeIfAbsent(uniqueId, f -> new ConcurrentHashMap<>())
                .computeIfAbsent(world, f -> new CargoNetwork(world))
                .remove(node);
    }

    @Override
    public CargoNetwork remove(Player player, InputNode node) {
        UUID uniqueId = player.getUniqueId();
        String world = node.getLocation().getWorld().getName();
        return storage.computeIfAbsent(uniqueId, f -> new ConcurrentHashMap<>())
                .computeIfAbsent(world, f -> new CargoNetwork(world))
                .remove(node);
    }

    @Override
    public CargoNetwork remove(Player player, OutputNode node) {
        UUID uniqueId = player.getUniqueId();
        String world = node.getLocation().getWorld().getName();
        return storage.computeIfAbsent(uniqueId, f -> new ConcurrentHashMap<>())
                .computeIfAbsent(world, f -> new CargoNetwork(world))
                .remove(node);
    }

    @Override
    public void remove(UUID playerUUID) {
        storage.remove(playerUUID);
    }

    @Override
    public CargoNetwork getNetwork(Player player) {
        return storage.computeIfAbsent(player.getUniqueId(), f -> new ConcurrentHashMap<>())
                .computeIfAbsent(player.getWorld().getName(), f -> new CargoNetwork(player.getWorld().getName()));
    }

    @Override
    public SearchResult findNode(Player player, Location location) {
        CargoNetwork cargoNetwork = storage.computeIfAbsent(player.getUniqueId(), f -> new ConcurrentHashMap<>())
                .computeIfAbsent(player.getWorld().getName(), f -> new CargoNetwork(player.getWorld().getName()));
        CargoNode node = cargoNetwork.findNode(location);
        if (node != null) {
            return new SearchResult(cargoNetwork, node);
        }
        return null;
    }

    @Override
    public void tick() {
        // TODO: R4zorax - 09-08-2018: If this takes a long time, somehow batch it
        activePlayers.stream().map(m -> storage.get(m))
                .filter(f -> f != null && !f.isEmpty())
                .forEach(c -> c.values().stream().forEach(net -> net.tick()));
    }

    @Override
    public boolean isDirty() {
        return Objects.hashCode(storage) != lastSavedHash;
    }

    @Override
    public void save() throws IOException {
        if (file == null) {
            file = FileUtil.getConfigFile("networks.yml");
        }
        YmlConfiguration config = new YmlConfiguration();
        for (UUID uuid : storage.keySet()) {
            for (String world : storage.get(uuid).keySet()) {
                CargoNetwork network = storage.get(uuid).get(world);

                // Input
                ConfigurationSection section = config.createSection(uuid.toString() + "." + world + ".inputs");
                for (InputNode node : network.getInputs()) {
                    String key = LocationUtil.asKey(node.getLocation());
                    section.set(key + ".container", LocationUtil.asString(node.getContainer()));
                }

                // Connectors
                config.set(uuid.toString() + "." + world + ".connectors", network.getConnectors().stream().map(m -> LocationUtil.asString(m.getLocation())).collect(Collectors.toList()));

                // Outputs
                section = config.createSection(uuid.toString() + "." + world + ".outputs");
                for (OutputNode node : network.getOutputs()) {
                    String key = LocationUtil.asKey(node.getLocation());
                    section.set(key + ".container", LocationUtil.asString(node.getContainer()));
                    // TODO: R4zorax - 08-08-2018: Store lore, display-name etc?
                    section.set(key + ".filter", node.getFilter().stream().map(ItemStackUtil::asString).collect(Collectors.toList()));
                }
            }
        }
        config.save(file);
        lastSavedHash = Objects.hashCode(storage);
    }

    @Override
    public void load(File file) {
        storage.clear();
        this.file = file;
        YmlConfiguration config = new YmlConfiguration();
        FileUtil.readConfig(config, file);
        for (String uuidKey : config.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidKey);
            ConfigurationSection worldSection = config.getConfigurationSection(uuidKey);
            for (String worldName : worldSection.getKeys(false)) {
                CargoNetwork network = storage.computeIfAbsent(uuid, f -> new ConcurrentHashMap<>()).computeIfAbsent(worldName, f -> new CargoNetwork(worldName));
                for (String locString : worldSection.getConfigurationSection(worldName + ".inputs").getKeys(false)) {
                    Location location = LocationUtil.fromString(locString);
                    Location containerLocation = LocationUtil.fromString(worldSection.getString(worldName + ".inputs." + locString + ".container"));
                    network.add(new InputNode(location, containerLocation));
                }
                for (String locString : worldSection.getStringList(worldName + ".connectors")) {
                    Location location = LocationUtil.fromString(locString);
                    network.add(new ConnectorNode(location));
                }
                for (String locString : worldSection.getConfigurationSection(worldName + ".outputs").getKeys(false)) {
                    Location location = LocationUtil.fromString(locString);
                    Location containerLocation = LocationUtil.fromString(worldSection.getString(worldName + ".outputs." + locString + ".container"));
                    List<ItemStack> filter = ItemStackUtil.createItemList(worldSection.getStringList(worldName + ".outputs." + locString + ".filter"));
                    network.add(new OutputNode(location, containerLocation, filter));
                }
            }
        }
    }
}
