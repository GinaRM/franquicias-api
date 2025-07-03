package com.gina.franquicias_api.domain.model;

import java.util.List;

public class Franchise {
    private final String id;
    private final String name;
    private final List<Branch> branches;

    public Franchise(String id, String name, List<Branch> branches) {
        this.id       = id;
        this.name     = name;
        this.branches = branches;
    }

    public String getId()   { return id; }
    public String getName() { return name; }
    public List<Branch> getBranches() { return branches; }

    // Ejemplo de método de dominio
    public Franchise addBranch(Branch b) {
        branches.add(b);
        return this;
    }
}
