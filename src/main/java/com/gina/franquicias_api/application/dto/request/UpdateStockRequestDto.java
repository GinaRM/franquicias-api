package com.gina.franquicias_api.application.dto.request;

public class UpdateStockRequestDto {
    private int stock;

    public UpdateStockRequestDto() {}

    public UpdateStockRequestDto(int stock) {
        this.stock = stock;
    }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
