package com.gina.franquicias_api.application.dto;

public class ProductRequestDto {
    private String name;
    private int stock;

    public ProductRequestDto() {}

    public ProductRequestDto(String name, int stock) {
        this.name = name;
        this.stock = stock;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
