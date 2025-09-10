package com.gina.franquicias_api.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Branch {
    private final String id;
    private String name;
    private final List<Product> products;

    public Branch(String id, String name, List<Product> products) {
        this.id = id;
        this.name = name;
        this.products = new ArrayList<>(products); // copia defensiva
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }


    public void rename(String newName) {
        this.name = newName;
    }

    public void addProduct(Product product) {
        this.products.add(product);
    }

    public boolean removeProductById(String productId) {
        return this.products.removeIf(p -> p.getId().equals(productId));
    }
}
