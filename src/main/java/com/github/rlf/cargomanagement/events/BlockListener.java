package com.github.rlf.cargomanagement.events;

import com.github.rlf.cargomanagement.item.CargoNetworkFormatter;
import com.github.rlf.cargomanagement.item.CargoNodeFactory;
import com.github.rlf.cargomanagement.model.CargoNetwork;
import com.github.rlf.cargomanagement.model.CargoNode;
import com.github.rlf.cargomanagement.model.ConnectorNode;
import com.github.rlf.cargomanagement.model.InputNode;
import com.github.rlf.cargomanagement.model.OutputNode;
import com.github.rlf.cargomanagement.storage.BlockStorage;
import dk.lockfuglsang.minecraft.util.ItemStackUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.material.Directional;
import org.bukkit.plugin.java.JavaPlugin;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

public class BlockListener implements Listener {

    private JavaPlugin plugin;
    private BlockStorage storage;
    private CargoNodeFactory nodeFactory;

    public BlockListener(JavaPlugin plugin, BlockStorage storage, CargoNodeFactory nodeFactory) {
        this.plugin = plugin;
        this.storage = storage;
        this.nodeFactory = nodeFactory;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        if (e.isCancelled() || block == null || !nodeFactory.isCargoNodeBlock(block)) {
            return;
        }
        if (!e.getPlayer().hasPermission("cargo.use")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(tr("\u00a7cYou do not have permission to place cargo-nodes"));
            return;
        }
        Location containerLocation = e.getBlockAgainst() != null && e.getBlockAgainst().getLocation() != null ? e.getBlockAgainst().getLocation() : null;
        if (nodeFactory.isConnectorNodeBlock(block)) {
            plugin.getServer().getPluginManager().callEvent(new CargoNodePlacedEvent<>(e.getPlayer(), new ConnectorNode(block.getLocation())));
        } else if (nodeFactory.isInputNodeBlock(block)) {
            plugin.getServer().getPluginManager().callEvent(new CargoNodePlacedEvent<>(e.getPlayer(), new InputNode(block.getLocation(), containerLocation)));
        } else if (nodeFactory.isOutputNodeBlock(block)) {
            plugin.getServer().getPluginManager().callEvent(new CargoNodePlacedEvent<>(e.getPlayer(), new OutputNode(block.getLocation(), containerLocation)));
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (e.isCancelled() || block == null || !nodeFactory.isCargoNodeBlock(block)) {
            return;
        }
        Location containerLocation = null;
        if (block.getBlockData() instanceof Directional) {
            containerLocation = block.getRelative(((Directional) block.getBlockData()).getFacing()).getLocation();
        }
        if (nodeFactory.isConnectorNodeBlock(block)) {
            plugin.getServer().getPluginManager().callEvent(new CargoNodeBreakEvent<>(e.getPlayer(), new ConnectorNode(block.getLocation())));
        } else if (nodeFactory.isInputNodeBlock(block)) {
            plugin.getServer().getPluginManager().callEvent(new CargoNodeBreakEvent<>(e.getPlayer(), new InputNode(block.getLocation(), containerLocation)));
        } else if (nodeFactory.isOutputNodeBlock(block)) {
            plugin.getServer().getPluginManager().callEvent(new CargoNodeBreakEvent<>(e.getPlayer(), new OutputNode(block.getLocation(), containerLocation)));
        }
        e.setDropItems(false);
        if (e.getPlayer().hasPermission("cargo.use")) {
            block.getWorld().dropItemNaturally(block.getLocation(), nodeFactory.asCargoItemStack(block));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (e.isCancelled()
                || block == null
                || !nodeFactory.isCargoNodeBlock(block)
                || e.getAction() != Action.RIGHT_CLICK_BLOCK
                || e.getHand() != EquipmentSlot.HAND
                ) {
            return;
        }
        e.setCancelled(true);
        e.setUseInteractedBlock(Event.Result.DENY);
        if (!e.getPlayer().hasPermission("cargo.use")) {
            e.getPlayer().sendMessage("\u00a7cYou do not have permission to use cargo-nodes!");
            return;
        }
        if (nodeFactory.isOutputNodeBlock(block)) {
            BlockStorage.SearchResult searchResult = storage.findNode(e.getPlayer(), block.getLocation());
            CargoNode node = searchResult != null ? searchResult.getNode() : null;
            if (searchResult != null && node instanceof OutputNode) {
                CargoNetwork network = searchResult.getNetwork();
                String status = CargoNetworkFormatter.getStatus(network, node);
                if (e.getItem() == null) {
                    if (e.getPlayer().isSneaking()) {
                        network.clearFilter((OutputNode) node);
                        e.getPlayer().sendMessage(tr("Cleared filter for output-node - {0}", status));
                    } else {
                        e.getPlayer().sendMessage(tr("Filter for output-node is {0} - {1}", CargoNetworkFormatter.getFilter((OutputNode) node), status));
                    }
                } else if (e.getItem().getType() != null) {
                    network.addFilter((OutputNode) node, e.getItem());
                    e.getPlayer().sendMessage(tr("Added \u00a79{0}\u00a7r to the filter {1} - {2}", ItemStackUtil.getItemName(e.getItem()),
                            CargoNetworkFormatter.getFilter((OutputNode) node), status));
                }
                e.setUseInteractedBlock(Event.Result.DENY);
            } else {
                e.getPlayer().sendMessage(tr("\u00a7cNode is not part of your network"));
            }
        } else if (nodeFactory.isConnectorNodeBlock(block) || nodeFactory.isInputNodeBlock(block)) {
            BlockStorage.SearchResult searchResult = storage.findNode(e.getPlayer(), block.getLocation());
            if (searchResult != null && searchResult.getNode() != null) {
                String status = CargoNetworkFormatter.getStatus(searchResult.getNetwork(), searchResult.getNode());
                e.getPlayer().sendMessage(tr("Node is {0}", status));
            } else {
                e.getPlayer().sendMessage(tr("\u00a7cNode is not part of your network"));
            }
        }
    }
}
