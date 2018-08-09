package com.github.rlf.cargomanagement.events;

import com.github.rlf.cargomanagement.item.CargoNetworkFormatter;
import com.github.rlf.cargomanagement.model.CargoNetwork;
import com.github.rlf.cargomanagement.model.ConnectorNode;
import com.github.rlf.cargomanagement.model.InputNode;
import com.github.rlf.cargomanagement.model.OutputNode;
import com.github.rlf.cargomanagement.storage.BlockStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Logger;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

public class CargoNodePlaceListener implements Listener {
    private static Logger logger = Logger.getLogger("CargoManagement");
    private BlockStorage storage;

    public CargoNodePlaceListener(BlockStorage storage) {
        this.storage = storage;
    }

    @EventHandler
    public void onCargoNodePlace(CargoNodePlacedEvent<?> e) {
        if (e.getNode() instanceof ConnectorNode) {
            CargoNetwork network = storage.add(e.getPlayer(), (ConnectorNode) e.getNode());
            e.getPlayer().sendMessage(tr("Placed connector node - {0}", CargoNetworkFormatter.getStatus(network, e.getNode())));
        } else if (e.getNode() instanceof OutputNode) {
            CargoNetwork network = storage.add(e.getPlayer(), (OutputNode) e.getNode());
            e.getPlayer().sendMessage(tr("Placed output-node - {0}", CargoNetworkFormatter.getStatus(network, e.getNode())));
        } else if (e.getNode() instanceof InputNode) {
            CargoNetwork network = storage.add(e.getPlayer(), (InputNode) e.getNode());
            e.getPlayer().sendMessage(tr("Placed input-node - {0}", CargoNetworkFormatter.getStatus(network, e.getNode())));
        } else {
            logger.warning("Unsupported type of cargo-node " + e.getNode());
        }
    }

    @EventHandler
    public void onCargoNodeBreak(CargoNodeBreakEvent<?> e) {
        if (e.getNode() instanceof ConnectorNode) {
            storage.remove(e.getPlayer(), (ConnectorNode) e.getNode());
        } else if (e.getNode() instanceof OutputNode) {
            storage.remove(e.getPlayer(), (OutputNode) e.getNode());
        } else if (e.getNode() instanceof InputNode) {
            storage.remove(e.getPlayer(), (InputNode) e.getNode());
        } else {
            logger.warning("Unsupported type of cargo-node " + e.getNode());
        }
    }
}
