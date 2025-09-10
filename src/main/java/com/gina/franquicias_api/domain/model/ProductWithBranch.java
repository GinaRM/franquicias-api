package com.gina.franquicias_api.domain.model;

public class ProductWithBranch {
    private final String branchName;
    private final Product product;

    public ProductWithBranch(String branchName, Product product) {
        this.branchName = branchName;
        this.product = product;
    }

    public String getBranchName() { return branchName; }
    public Product getProduct() { return product; }
}

