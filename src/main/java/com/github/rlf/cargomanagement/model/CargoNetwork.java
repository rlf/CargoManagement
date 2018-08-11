package com.github.rlf.cargomanagement.model;

import com.github.rlf.cargomanagement.model.builders.ConnectedNetsBuilder;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a cargo-network (i.e. all the nodes placed by a user, within a world).
 */
public class CargoNetwork {
    private boolean isDirty = true;

    private String world;

    private Set<ConnectorNode> connectors = new HashSet<>();
    private Set<InputNode> inputs = new HashSet<>();
    private Set<OutputNode> outputs = new HashSet<>();

    private Set<ConnectedNet> connectedNets = new HashSet<>();

    public CargoNetwork(String world) {
        this.world = world;
    }

    public Set<ConnectorNode> getConnectors() {
        return Collections.unmodifiableSet(connectors);
    }

    public Set<InputNode> getInputs() {
        return Collections.unmodifiableSet(inputs);
    }

    public Set<OutputNode> getOutputs() {
        return Collections.unmodifiableSet(outputs);
    }

    public void tick() {
        for (ConnectedNet net : getConnectedNets()) {
            net.tick();
        }
    }

    public boolean isConnected(Location location) {
        if (!world.equals(location.getWorld().getName())) {
            return false;
        }
        return connectors.stream().anyMatch(a -> a.isConnected(location));
    }

    public Set<ConnectedNet> getConnectedNets() {
        if (isDirty) {
            recalculateNet();
        }
        return connectedNets;
    }

    public boolean isOnline(CargoNode node) {
        return getConnectedNets().stream().anyMatch(m -> m.contains(node) && m.isOnline());
    }

    public CargoNode findNode(Location location) {
        if (!world.equals(location.getWorld().getName())) {
            return null;
        }
        ArrayList<CargoNode> nodes = new ArrayList<>();
        nodes.addAll(connectors);
        nodes.addAll(inputs);
        nodes.addAll(outputs);
        return nodes.stream().filter(f -> location.equals(f.getLocation())).findFirst().orElse(null);
    }

    public void recalculateNet() {
        connectedNets = new ConnectedNetsBuilder(inputs, connectors, outputs).build();
        isDirty = false;
    }

    public CargoNetwork addFilter(OutputNode node, ItemStack item) {
        node.getFilter().add(item.clone());
        isDirty = true;
        return this;
    }

    public CargoNetwork clearFilter(OutputNode node) {
        node.getFilter().clear();
        isDirty = true;
        return this;
    }

    public CargoNetwork add(ConnectorNode node) {
        connectors.add(node);
        isDirty = true;
        return this;
    }

    public CargoNetwork add(InputNode node) {
        inputs.add(node);
        isDirty = true;
        return this;
    }

    public CargoNetwork add(OutputNode node) {
        outputs.add(node);
        isDirty = true;
        return this;
    }

    public CargoNetwork remove(ConnectorNode node) {
        connectors.remove(node);
        isDirty = true;
        return this;
    }

    public CargoNetwork remove(InputNode node) {
        inputs.remove(node);
        isDirty = true;
        return this;
    }

    public CargoNetwork remove(OutputNode node) {
        outputs.remove(node);
        isDirty = true;
        return this;
    }

}
