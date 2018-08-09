package com.github.rlf.cargomanagement.model;

public interface CargoNodeVisitor {
    void visit(CargoNode node);
    void visit(ConnectorNode node);
    void visit(InputNode node);
    void visit(OutputNode node);
}
