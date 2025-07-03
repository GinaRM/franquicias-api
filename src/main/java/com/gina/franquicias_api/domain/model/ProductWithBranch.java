package com.gina.franquicias_api.domain.model;

public class ProductWithBranch {
    private String branchName;
    private Product product;

    public ProductWithBranch() {
    }

    public ProductWithBranch(String branchName, Product product) {
        this.branchName = branchName;
        this.product = product;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
