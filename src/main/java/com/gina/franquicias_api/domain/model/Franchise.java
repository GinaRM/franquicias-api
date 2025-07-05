package com.gina.franquicias_api.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Franchise {
    private final String id;
    private final String name;
    private List<Branch> branches;

    public Franchise(String id, String name, List<Branch> branches) {
        this.id       = id;
        this.name     = name;
        this.branches = new ArrayList<>(branches);
    }

    public String getId()   { return id; }
    public String getName() { return name; }
    public List<Branch> getBranches() { return branches; }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }


}
