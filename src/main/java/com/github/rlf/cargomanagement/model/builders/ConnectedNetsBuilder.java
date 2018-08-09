package com.github.rlf.cargomanagement.model.builders;

import com.github.rlf.cargomanagement.model.ConnectedNet;
import com.github.rlf.cargomanagement.model.ConnectorNode;
import com.github.rlf.cargomanagement.model.InputNode;
import com.github.rlf.cargomanagement.model.OutputNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConnectedNetsBuilder {
    private final Set<InputNode> inputs;
    private final Set<ConnectorNode> connectors;
    private final Set<OutputNode> outputs;

    public ConnectedNetsBuilder(Set<InputNode> inputs, Set<ConnectorNode> connectors, Set<OutputNode> outputs) {
        this.connectors = connectors;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public Set<ConnectedNet> build() {
        Set<ConnectedNetBuilder> builders = new HashSet<>();
        List<ConnectorNode> copy = new ArrayList<>(connectors);
        while (!copy.isEmpty()) {
            ConnectorNode connector = copy.remove(0);
            ConnectedNetBuilder builder = new ConnectedNetBuilder(connector);
            boolean foundOne = false;
            do {
                foundOne = false;
                for (Iterator<ConnectorNode> it = copy.iterator(); it.hasNext();) {
                    ConnectorNode next = it.next();
                    if (builder.isConnected(next)) {
                        builder.add(next);
                        it.remove();
                        foundOne = true;
                    }
                }
            } while (foundOne);
            builders.add(builder);
        }
        for (OutputNode output : outputs) {
            builders.stream().filter(p -> p.isConnected(output)).forEach(c -> c.add(output));
        }
        for (InputNode input : inputs) {
            builders.stream().filter(p -> p.isConnected(input)).forEach(c -> c.add(input));
        }
        return builders.stream().map(ConnectedNetBuilder::build)
                .filter(f -> f != null)
                .collect(Collectors.toSet());
    }
}
