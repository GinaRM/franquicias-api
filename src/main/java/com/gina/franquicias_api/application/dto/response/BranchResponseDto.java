package com.gina.franquicias_api.application.dto.response;

import java.util.List;

public class BranchResponseDto {
    private String id;
    private String name;
    private List<ProductResponseDto> products;

    public BranchResponseDto() {}

    public BranchResponseDto(String id, String name, List<ProductResponseDto> products) {
        this.id = id;
        this.name = name;
        this.products = products;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<ProductResponseDto> getProducts() { return products; }
}
