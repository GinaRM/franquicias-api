package com.gina.franquicias_api.infrastructure.adapter.out.mongodb.entity;

import java.util.ArrayList;
import java.util.List;

public class BranchDocument {
    private String id;
    private String name;
    private List<ProductDocument> products = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductDocument> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDocument> products) {
        this.products = products;
    }
}
