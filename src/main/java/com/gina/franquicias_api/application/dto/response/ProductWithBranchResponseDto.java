package com.gina.franquicias_api.application.dto.response;

public class ProductWithBranchResponseDto {
    private String branchName;
    private ProductResponseDto product;

    public ProductWithBranchResponseDto() {}

    public ProductWithBranchResponseDto(String branchName, ProductResponseDto product) {
        this.branchName = branchName;
        this.product = product;
    }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public ProductResponseDto getProduct() { return product; }
    public void setProduct(ProductResponseDto product) { this.product = product; }
}
