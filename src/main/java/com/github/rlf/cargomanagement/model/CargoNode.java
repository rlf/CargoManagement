package com.github.rlf.cargomanagement.model;

import org.bukkit.Location;

import java.util.Objects;

public abstract class CargoNode {
    private Location location;

    public CargoNode(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void accept(CargoNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CargoNode)) return false;
        CargoNode cargoNode = (CargoNode) o;
        return Objects.equals(location, cargoNode.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "location=" + location +
                '}';
    }
}
