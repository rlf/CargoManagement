package com.github.rlf.cargomanagement.events;

import com.github.rlf.cargomanagement.model.CargoNode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CargoNodePlacedEvent<T extends CargoNode> extends Event {
    protected static final HandlerList handlers = new HandlerList();

    private Player player;
    private T node;

    public CargoNodePlacedEvent(Player player, T node) {
        this.player = player;
        this.node = node;
    }

    public Player getPlayer() {
        return player;
    }

    public T getNode() {
        return node;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
