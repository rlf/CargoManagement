package com.github.rlf.cargomanagement.model;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class ConnectorNode extends CargoNode {
    public static int RANGE = 5;
    private static final Vector X_AXIS = new Vector(1, 0, 0);
    private static final Vector Y_AXIS = new Vector(0, 1, 0);
    private static final Vector Z_AXIS = new Vector(0, 0, 1);

    private Set<Location> connectedLocations = new HashSet<>();

    public ConnectorNode(Location location) {
        super(location);
        for (int i = 1; i <= RANGE; i++) {
            for (int sgn = -1; sgn <= 1; sgn += 2) {
                connectedLocations.add(location.clone().add(X_AXIS.clone().multiply(sgn*i)));
                connectedLocations.add(location.clone().add(Y_AXIS.clone().multiply(sgn*i)));
                connectedLocations.add(location.clone().add(Z_AXIS.clone().multiply(sgn*i)));
            }
        }
    }

    public boolean isConnected(Location location) {
        return connectedLocations.contains(location);
    }
}
