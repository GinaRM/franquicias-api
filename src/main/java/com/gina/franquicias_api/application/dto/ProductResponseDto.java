package com.gina.franquicias_api.application.dto;

public class ProductResponseDto {
    private String id;
    private String name;
    private int stock;

    public ProductResponseDto() {}

    public ProductResponseDto(String id, String name, int stock) {
        this.id = id;
        this.name = name;
        this.stock = stock;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getStock() { return stock; }
}
