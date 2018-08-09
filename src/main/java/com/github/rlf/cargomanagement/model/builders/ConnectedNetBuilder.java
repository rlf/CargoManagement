package com.github.rlf.cargomanagement.model.builders;

import com.github.rlf.cargomanagement.model.CargoNode;
import com.github.rlf.cargomanagement.model.ConnectedNet;
import com.github.rlf.cargomanagement.model.ConnectorNode;
import com.github.rlf.cargomanagement.model.InputNode;
import com.github.rlf.cargomanagement.model.OutputNode;

import java.util.HashSet;
import java.util.Set;

public class ConnectedNetBuilder {
    private Set<InputNode> inputs = new HashSet<>();
    private Set<ConnectorNode> connectors = new HashSet<>();
    private Set<OutputNode> outputs = new HashSet<>();

    public ConnectedNetBuilder(ConnectorNode node) {
        connectors.add(node);
    }

    public boolean isConnected(CargoNode node) {
        return connectors.stream().anyMatch(m -> m.isConnected(node.getLocation()));
    }

    public ConnectedNetBuilder add(ConnectorNode node) {
        connectors.add(node);
        return this;
    }

    public ConnectedNetBuilder add(InputNode node) {
        inputs.add(node);
        return this;
    }

    public ConnectedNetBuilder add(OutputNode node) {
        outputs.add(node);
        return this;
    }

    public ConnectedNet build() {
        return outputs.isEmpty() || inputs.isEmpty() ? null : new ConnectedNet(inputs, connectors, outputs);
    }

}
