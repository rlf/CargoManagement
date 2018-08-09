package com.github.rlf.cargomanagement.item;

import com.github.rlf.cargomanagement.model.CargoNetwork;
import com.github.rlf.cargomanagement.model.CargoNode;
import com.github.rlf.cargomanagement.model.OutputNode;
import dk.lockfuglsang.minecraft.util.ItemStackUtil;

import java.util.stream.Collectors;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

public class CargoNetworkFormatter {
    public static String getStatus(CargoNetwork network, CargoNode node) {
        return network != null && network.isOnline(node) ? tr("\u00a72ONLINE") : tr("\u00a74OFFLINE");
    }

    public static String getFilter(OutputNode node) {
        return node.getFilter().stream().map(m -> ItemStackUtil.getItemName(m)).collect(Collectors.toList()).toString();
    }
}
