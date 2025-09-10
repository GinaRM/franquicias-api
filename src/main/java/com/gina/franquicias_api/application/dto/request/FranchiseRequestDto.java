package com.gina.franquicias_api.application.dto.request;

public class FranchiseRequestDto {
    private String name;

    public FranchiseRequestDto() {}

    public FranchiseRequestDto(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
