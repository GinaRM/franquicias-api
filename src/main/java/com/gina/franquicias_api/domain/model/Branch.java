package com.gina.franquicias_api.domain.model;

import java.util.List;

public class Branch {
    private final String id;
    private final String name;
    private final List<Product> products;

    public Branch(String id, String name, List<Product> products) {
        this.id       = id;
        this.name     = name;
        this.products = products;
    }

    public String getId()   { return id; }
    public String getName() { return name; }
    public List<Product> getProducts() { return products; }

    public Branch addProduct(Product p) {
        products.add(p);
        return this;
    }
}
