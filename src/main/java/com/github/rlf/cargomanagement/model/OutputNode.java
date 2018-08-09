package com.github.rlf.cargomanagement.model;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class OutputNode extends IONode {
    private Set<ItemStack> filter = new HashSet<>();

    public OutputNode(Location location, Location containerLocation) {
        super(location, containerLocation);
    }

    public OutputNode(Location location, Location containerLocation, Collection<ItemStack> filter) {
        super(location, containerLocation);
        this.filter.addAll(filter);
    }

    public Set<ItemStack> getFilter() {
        return filter;
    }
}
