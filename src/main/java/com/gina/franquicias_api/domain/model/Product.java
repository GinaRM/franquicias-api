package com.gina.franquicias_api.domain.model;

public class Product {
    private final String id;
    private final String name;
    private int stock;

    public Product(String id, String name, int stock) {
        this.id    = id;
        this.name  = name;
        this.stock = stock;
    }

    public String getId()   { return id; }
    public String getName() { return name; }
    public int getStock()   { return stock; }

    public void setStock(int stock) { this.stock = stock; }
}
