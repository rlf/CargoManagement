package com.github.rlf.cargomanagement.model.builders;

import com.github.rlf.cargomanagement.model.ConnectedNet;
import com.github.rlf.cargomanagement.model.ConnectorNode;
import com.github.rlf.cargomanagement.model.InputNode;
import com.github.rlf.cargomanagement.model.OutputNode;
import org.bukkit.Location;
import org.bukkit.World;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ConnectedNetsBuilderTest {

    @Test
    public void build_Simple() {
        World world = Mockito.mock(World.class);
        Location l = new Location(world, 0, 0,0);
        Set<InputNode> inputs = new HashSet<>(Arrays.asList(new InputNode(l.clone().add(-5, 0, 0), l.clone().add(-5, 0, 1))));
        Set<ConnectorNode> connectors = new HashSet<>(Arrays.asList(new ConnectorNode(l.clone())));
        Set<OutputNode> outputs = new HashSet<>(Arrays.asList(new OutputNode(l.clone().add(5, 0,0), l.clone().add(5, 0, 1))));
        Set<ConnectedNet> actual = new ConnectedNetsBuilder(inputs, connectors, outputs).build();

        assertThat(actual, is(Collections.singleton(new ConnectedNet(inputs, connectors, outputs))));
    }

    @Test
    public void build_SimpleTooFar() {
        World world = Mockito.mock(World.class);
        Location l = new Location(world, 0, 0,0);
        Set<InputNode> inputs = new HashSet<>(Arrays.asList(new InputNode(l.clone().add(-6, 0, 0), l.clone().add(-6, 0, 1))));
        Set<ConnectorNode> connectors = new HashSet<>(Arrays.asList(new ConnectorNode(l.clone())));
        Set<OutputNode> outputs = new HashSet<>(Arrays.asList(new OutputNode(l.clone().add(6, 0,0), l.clone().add(6, 0, 1))));
        Set<ConnectedNet> actual = new ConnectedNetsBuilder(inputs, connectors, outputs).build();

        assertThat(actual, Matchers.empty());
    }

    @Test
    public void build_Multiple() {
        World world = Mockito.mock(World.class);
        Location l = new Location(world, 0, 0,0);

        Set<InputNode> inputs = new HashSet<>(Arrays.asList(
                new InputNode(l.clone().add(-5, 0, 0), l.clone().add(-5, 0, 1)),
                new InputNode(l.clone().add(-5, 1, 1), l.clone().add(-5, 1, 2))
        ));
        Set<ConnectorNode> connectors = new HashSet<>(Arrays.asList(new ConnectorNode(l.clone()), new ConnectorNode(l.clone().add(0, 1, 1))));
        Set<OutputNode> outputs = new HashSet<>(Arrays.asList(
                new OutputNode(l.clone().add(5, 0,0), l.clone().add(5, 0, 1)),
                new OutputNode(l.clone().add(5, 1,1), l.clone().add(5, 1, 2))
        ));

        Set<ConnectedNet> actual = new ConnectedNetsBuilder(inputs, connectors, outputs).build();

        assertThat(actual.size(), is(2));
    }

}