package com.github.rlf.cargomanagement.storage;

import com.github.rlf.cargomanagement.model.CargoNetwork;
import com.github.rlf.cargomanagement.model.CargoNode;
import com.github.rlf.cargomanagement.model.ConnectorNode;
import com.github.rlf.cargomanagement.model.InputNode;
import com.github.rlf.cargomanagement.model.OutputNode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public interface BlockStorage {
    void activate(Player player);
    void deactivate(Player player);

    CargoNetwork add(Player player, CargoNode node);
    CargoNetwork add(Player player, ConnectorNode node);
    CargoNetwork add(Player player, InputNode node);
    CargoNetwork add(Player player, OutputNode node);

    CargoNetwork remove(Player player, ConnectorNode node);
    CargoNetwork remove(Player player, InputNode node);
    CargoNetwork remove(Player player, OutputNode node);

    void remove(UUID player);

    CargoNetwork getNetwork(Player player);

    SearchResult findNode(Player player, Location location);

    void tick();

    boolean isDirty();
    void save() throws IOException;
    void load(File file);

    class SearchResult {
        CargoNetwork network;
        CargoNode node;

        public SearchResult(CargoNetwork network, CargoNode node) {
            this.network = network;
            this.node = node;
        }

        public CargoNetwork getNetwork() {
            return network;
        }

        public CargoNode getNode() {
            return node;
        }
    }
}
