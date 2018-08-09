package com.github.rlf.cargomanagement.model;

import org.bukkit.Location;
import org.bukkit.World;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;

public class ConnectorNodeTest {

    @Test
    public void isConnected_5Blocks() {
        World world = Mockito.mock(World.class);
        Location l = new Location(world, 0, 0,0);
        ConnectorNode connectorNode = new ConnectorNode(l);

        assertThat(connectorNode.isConnected(l.clone().add(5,0,0)), Matchers.is(true));
        assertThat(connectorNode.isConnected(l.clone().add(-5,0,0)), Matchers.is(true));
        assertThat(connectorNode.isConnected(l.clone().add(0,5,0)), Matchers.is(true));
        assertThat(connectorNode.isConnected(l.clone().add(0,-5,0)), Matchers.is(true));
        assertThat(connectorNode.isConnected(l.clone().add(0,0, 5)), Matchers.is(true));
        assertThat(connectorNode.isConnected(l.clone().add(0,0, -5)), Matchers.is(true));

        assertThat(connectorNode.isConnected(l.clone().add(0,0, -6)), Matchers.is(false));
        assertThat(connectorNode.isConnected(l.clone().add(1,1, 1)), Matchers.is(false));
    }
}