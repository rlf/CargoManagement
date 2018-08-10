package com.github.rlf.cargomanagement.item;

import com.github.rlf.cargomanagement.model.CargoNetwork;
import com.github.rlf.cargomanagement.model.CargoNode;
import com.github.rlf.cargomanagement.model.IONode;
import com.github.rlf.cargomanagement.model.OutputNode;
import dk.lockfuglsang.minecraft.util.ItemStackUtil;

import java.util.stream.Collectors;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

public class CargoNetworkFormatter {

    public static String getStatus(CargoNetwork network, CargoNode node) {
        String connectionStatus = network != null && network.isOnline(node) ? tr("\u00a72connected") : tr("\u00a78disconnected");
        String ioStatus = getIOStatus(node);
        return (connectionStatus + " " + ioStatus).trim();
    }

    public static String getIOStatus(CargoNode node) {
        return node instanceof IONode ? ((IONode) node).getInventory() != null ? tr("\u00a72chest") : tr("\u00a78no chest") : "";
    }

    public static String getFilter(OutputNode node) {
        return node.getFilter().stream().map(m -> ItemStackUtil.getItemName(m)).collect(Collectors.toList()).toString();
    }
}
