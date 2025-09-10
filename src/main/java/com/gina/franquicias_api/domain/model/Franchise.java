package com.gina.franquicias_api.domain.model;

import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Franchise {
    private final String id;
    private String name;
    private final List<Branch> branches;

    public Franchise(String id, String name, List<Branch> branches) {
        this.id = id;
        this.name = name;
        this.branches = new ArrayList<>(branches); // copia defensiva
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<Branch> getBranches() {
        return Collections.unmodifiableList(branches);
    }

    // Métodos de dominio
    public void rename(String newName) {
        this.name = newName;
    }

    public void addBranch(Branch branch) {
        this.branches.add(branch);
    }
}
