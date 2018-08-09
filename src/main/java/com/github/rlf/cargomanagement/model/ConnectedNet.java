package com.github.rlf.cargomanagement.model;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ConnectedNet {
    private Set<ConnectorNode> connectors;
    private Set<InputNode> inputs;
    private Set<OutputNode> outputs;

    public ConnectedNet(Set<InputNode> inputs, Set<ConnectorNode> connectors, Set<OutputNode> outputs) {
        this.connectors = connectors != null ? connectors : Collections.emptySet();
        this.inputs = inputs != null ? inputs : Collections.emptySet();
        this.outputs = outputs != null ? outputs : Collections.emptySet();
    }

    public void tick() {
        Map<ItemStack, List<Inventory>> outputMap = new HashMap<>();
        outputs.stream().filter(f -> f.getInventory() != null && !f.getFilter().isEmpty()).forEach(m ->
                m.getFilter().forEach(f -> outputMap.computeIfAbsent(f, t -> new ArrayList<>()).add(m.getInventory())));
        inputs.stream().map(IONode::getInventory).filter(f -> f != null).forEach(srcInventory -> {
            outputMap.forEach((key, value) -> {
                HashMap<Integer, ? extends ItemStack> content = srcInventory.all(key.getType());
                int totalItems = content.values().stream().mapToInt(ItemStack::getAmount).sum();
                if (totalItems > 0) {
                    ItemStack stackToRemove = key.clone();
                    stackToRemove.setAmount(totalItems);
                    int inventoriesLeft = value.size();
                    HashMap<Integer, ItemStack> missingRemoves = srcInventory.removeItem(stackToRemove.clone());
                    if (missingRemoves.isEmpty()) {
                        ItemStack clone = stackToRemove.clone();
                        for (Inventory tgtInventory : value) {
                            clone.setAmount(totalItems / inventoriesLeft);
                            if (clone.getAmount() > 0) {
                                totalItems -= clone.getAmount();
                                HashMap<Integer, ItemStack> rest = tgtInventory.addItem(clone.clone());
                                totalItems += rest.values().stream().mapToInt(ItemStack::getAmount).sum();
                            }
                            inventoriesLeft--;
                        }
                        if (totalItems > 0) {
                            clone.setAmount(totalItems);
                            HashMap<Integer, ItemStack> missingReadds = srcInventory.addItem(clone.clone());
                            int failedItems = missingReadds.values().stream().mapToInt(m -> m.getAmount()).sum();
                            if (failedItems > 0) {
                                System.out.println("Unable to handle " + failedItems + " items");
                            }
                        }
                    } else {
                        System.out.println("Unable to extract items from source-inventory: " + missingRemoves);
                    }
                }
            });
        });
    }

    public boolean contains(CargoNode node) {
        return connectors.contains(node) || inputs.contains(node) || outputs.contains(node);
    }

    public boolean isOnline() {
        return !outputs.isEmpty() && !inputs.isEmpty() && inputs.stream().anyMatch(p -> p.getInventory() != null) && outputs.stream().anyMatch(p -> p.getInventory() != null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectedNet)) return false;
        ConnectedNet that = (ConnectedNet) o;
        return Objects.equals(connectors, that.connectors) &&
                Objects.equals(inputs, that.inputs) &&
                Objects.equals(outputs, that.outputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectors, inputs, outputs);
    }

    @Override
    public String toString() {
        return "ConnectedNet{" +
                "connectors=" + connectors +
                ", inputs=" + inputs +
                ", outputs=" + outputs +
                '}';
    }
}
