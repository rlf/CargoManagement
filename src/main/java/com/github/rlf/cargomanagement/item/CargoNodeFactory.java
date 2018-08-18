package com.github.rlf.cargomanagement.item;

import com.github.rlf.cargomanagement.model.CargoNode;
import com.github.rlf.cargomanagement.model.ConnectorNode;
import com.github.rlf.cargomanagement.model.InputNode;
import com.github.rlf.cargomanagement.model.OutputNode;
import dk.lockfuglsang.minecraft.nbt.NBTUtil;
import dk.lockfuglsang.minecraft.util.ItemStackUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Directional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

public class CargoNodeFactory {
    // {SkullOwner:{Id:"ffffffff-86a5-926a-ffff-ffffc0f903f4",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMDdiN2VmNmZkNzg2NDg2NWMzMWMxZGM4N2JlZDI0YWI1OTczNTc5ZjVjNjYzOGZlY2I4ZGVkZWI0NDNmZjAifX19"}]}},display:{Lore:["Connects other cargo-nodes to","the network"],Name:"{\"text\":\"Connector\"}"}}
    private static final Pattern UUID_IN_NBT = Pattern.compile(".*Id:\"(?<uuid>[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})\".*", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    public enum NodeType { UNKNOWN, CONNECTOR, INPUT, OUTPUT }

    private final String CONNECTOR_NODE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMDdiN2VmNmZkNzg2NDg2NWMzMWMxZGM4N2JlZDI0YWI1OTczNTc5ZjVjNjYzOGZlY2I4ZGVkZWI0NDNmZjAifX19";
    private final String INPUT_NODE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZkMWMxYTY5YTNkZTlmZWM5NjJhNzdiZjNiMmUzNzZkZDI1Yzg3M2EzZDhmMTRmMWRkMzQ1ZGFlNGM0In19fQ==";
    private final String OUTPUT_NODE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTViMjFmZDQ4MGMxYzQzYmYzYjlmODQyYzg2OWJkYzNiYzVhY2MyNTk5YmYyZWI2YjhhMWM5NWRjZTk3OGYifX19";

    private final ItemStack connectorItem;
    private final ItemStack inputItem;
    private final ItemStack outputItem;
    private final List<ItemStack> cargoNodes;
    private final UUID connectorUUID;
    private final UUID inputUUID;
    private final UUID outputUUID;
    private final List<UUID> uuids;

    public CargoNodeFactory(FileConfiguration config) {
        connectorItem = ItemStackUtil.createItemStackSkull(config.getString("cargonodes.connector.texture", CONNECTOR_NODE_TEXTURE),
                tr(config.getString("cargonodes.connector.name", "Connector")),
                tr(config.getString("carognodes.connector.description", "Connects other cargo-nodes to the network")));
        inputItem = ItemStackUtil.createItemStackSkull(config.getString("cargonodes.input.texture", INPUT_NODE_TEXTURE),
                tr(config.getString("carognodes.input.description", "Input")),
                tr(config.getString("carognodes.input.description", "Pulls items out of an inventory and into the cargo-network")));
        outputItem = ItemStackUtil.createItemStackSkull(config.getString("cargonodes.output.texture", OUTPUT_NODE_TEXTURE),
                tr(config.getString("carognodes.output.description", "Output")),
                tr(config.getString("carognodes.output.description", "Delivers items from the network to an attached inventory")));
        cargoNodes = Arrays.asList(connectorItem, inputItem, outputItem);

        connectorUUID = getUUID(connectorItem);
        inputUUID = getUUID(inputItem);
        outputUUID = getUUID(outputItem);
        uuids = Arrays.asList(connectorUUID, inputUUID, outputUUID);
    }

    private UUID getUUID(ItemStack item) {
        String nbtTag = NBTUtil.getNBTTag(item);
        Matcher matcher = UUID_IN_NBT.matcher(nbtTag);
        if (matcher.matches()) {
            return UUID.fromString(matcher.group("uuid"));
        }
        throw new IllegalArgumentException("Unable to locate UUID in " + nbtTag);
    }

    public ItemStack createCargoNode(String name) {
        switch (name) {
            case "connector": return createConnectorNode();
            case "input": return createInputNode();
            case "output": return createOutputNode();
        }
        throw new IllegalArgumentException("Unsuported cargo-node " + name);
    }

    public ItemStack createConnectorNode() {
        return connectorItem.clone();
    }

    public ItemStack createInputNode() {
        return inputItem.clone();
    }

    public ItemStack createOutputNode() {
        return outputItem.clone();
    }

    public NodeType getNodeType(ItemStack item) {
        if (item.getType() == Material.PLAYER_HEAD && item.getItemMeta() instanceof SkullMeta) {
            UUID uniqueId = getUUID(item);
            NodeType x = getNodeType(uniqueId);
            if (x != null) return x;
        }
        return NodeType.UNKNOWN;
    }

    private NodeType getNodeType(UUID uniqueId) {
        if (connectorUUID.equals(uniqueId)) {
            return NodeType.CONNECTOR;
        } else if (inputUUID.equals(uniqueId)) {
            return NodeType.INPUT;
        } else if (outputUUID.equals(uniqueId)) {
            return NodeType.OUTPUT;
        }
        return null;
    }

    public boolean isCargoNodeItem(ItemStack item) {
        if (item.getType() == Material.PLAYER_HEAD && item.getItemMeta() instanceof SkullMeta) {
            return uuids.contains(getUUID(item));
        }
        return false;
    }

    public boolean isCargoNodeBlock(Block block) {
        if (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.PLAYER_WALL_HEAD) {
            BlockState state = block.getState();
            if (state instanceof Skull) {
                Skull skull = (Skull) state;
                UUID uniqueId = skull.getOwningPlayer().getUniqueId();
                return uuids.contains(uniqueId);
            }
        }
        return false;
    }

    public boolean isConnectorNodeBlock(Block block) {
        if (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.PLAYER_WALL_HEAD) {
            BlockState state = block.getState();
            if (state instanceof Skull) {
                Skull skull = (Skull) state;
                UUID uniqueId = skull.getOwningPlayer().getUniqueId();
                return connectorUUID.equals(uniqueId);
            }
        }
        return false;
    }

    public boolean isInputNodeBlock(Block block) {
        if (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.PLAYER_WALL_HEAD) {
            BlockState state = block.getState();
            if (state instanceof Skull) {
                Skull skull = (Skull) state;
                UUID uniqueId = skull.getOwningPlayer().getUniqueId();
                return inputUUID.equals(uniqueId);
            }
        }
        return false;
    }

    public boolean isOutputNodeBlock(Block block) {
        if (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.PLAYER_WALL_HEAD) {
            BlockState state = block.getState();
            if (state instanceof Skull) {
                Skull skull = (Skull) state;
                UUID uniqueId = skull.getOwningPlayer().getUniqueId();
                return outputUUID.equals(uniqueId);
            }
        }
        return false;
    }

    public CargoNode asCargoNode(Block block) {
        if (isConnectorNodeBlock(block)) {
            return new ConnectorNode(block.getLocation());
        } else if ((isInputNodeBlock(block) || isOutputNodeBlock(block)) && block.getBlockData() instanceof Directional) {
            Location containerLocation = block.getRelative(((Directional) block.getBlockData()).getFacing()).getLocation();
            if (containerLocation == null) {
                return null;
            }
            if (isInputNodeBlock(block)) {
                return new InputNode(block.getLocation(), containerLocation);
            } else {
                return new OutputNode(block.getLocation(), containerLocation);
            }
        }
        return null;
    }

    public ItemStack asCargoItemStack(Block block) {
        if (isConnectorNodeBlock(block)) {
            return createConnectorNode();
        } else if (isInputNodeBlock(block)) {
            return createInputNode();
        } else if (isOutputNodeBlock(block)) {
            return createOutputNode();
        }
        return new ItemStack(block.getType(), 1);
    }
}
