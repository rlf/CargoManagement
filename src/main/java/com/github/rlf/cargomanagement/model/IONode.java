package com.github.rlf.cargomanagement.model;

import org.bukkit.Location;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

import java.util.Objects;

public abstract class IONode extends CargoNode {
    private Location container;

    public IONode(Location location, Location container) {
        super(location);
        this.container = container;
    }

    public Location getContainer() {
        return container;
    }

    public Inventory getInventory() {
        if (container.getChunk().isLoaded()) {
            if (container.getBlock().getState() instanceof Container) {
                return ((Container) container.getBlock().getState()).getInventory();
            } else {
                // try looking one block further
                Vector vector = container.clone().subtract(getLocation()).toVector();
                Location secondary = container.clone().add(vector);
                if (secondary.getBlock().getState() instanceof Container) {
                    return ((Container) container.getBlock().getState()).getInventory();
                }
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IONode)) return false;
        if (!super.equals(o)) return false;
        IONode ioNode = (IONode) o;
        return Objects.equals(container, ioNode.container);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), container);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "location=" + getLocation() +
                ", container=" + container +
                '}';
    }
}
