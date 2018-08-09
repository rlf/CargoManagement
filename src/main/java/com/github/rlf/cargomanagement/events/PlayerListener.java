package com.github.rlf.cargomanagement.events;

import com.github.rlf.cargomanagement.item.CargoNodeFactory;
import com.github.rlf.cargomanagement.storage.BlockStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

public class PlayerListener implements Listener {
    private BlockStorage storage;
    private CargoNodeFactory itemFactory;

    public PlayerListener(BlockStorage storage, CargoNodeFactory itemFactory) {
        this.storage = storage;
        this.itemFactory = itemFactory;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        storage.activate(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        storage.deactivate(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerKickEvent e) {
        storage.deactivate(e.getPlayer());
    }

    @EventHandler
    public void onCraftItemEvent(CraftItemEvent e) {
        if (e.isCancelled() || e.getWhoClicked() == null || !(e.getWhoClicked() instanceof Player)) {
            return;
        }
        if (itemFactory.isCargoNodeItem(e.getRecipe().getResult())) {
            CargoNodeFactory.NodeType nodeType = itemFactory.getNodeType(e.getRecipe().getResult());
            if (!e.getWhoClicked().hasPermission("cargo.recipe." + nodeType.name().toLowerCase())) {
                e.setCancelled(true);
                e.getWhoClicked().sendMessage(tr("\u00a7eYou do not have permission to craft this item"));
            }
        }
    }
}
