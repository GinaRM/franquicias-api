package com.gina.franquicias_api.application.dto;

public class BranchRequestDto {
    private String name;

    public BranchRequestDto() {}

    public BranchRequestDto(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
