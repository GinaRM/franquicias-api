package com.gina.franquicias_api.domain.port.in;

import com.gina.franquicias_api.domain.model.Branch;
import com.gina.franquicias_api.domain.model.Franchise;
import com.gina.franquicias_api.domain.model.Product;
import com.gina.franquicias_api.domain.model.ProductWithBranch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseService {
    //inbound port: define las operaciones que el mundo externo puede hacer con la lógica del negocio
    Mono<Franchise> createFranchise(String name);
    Mono<Branch> addBranch(String franchiseId, String branchName);
    Mono<Product>  addProduct(String franchiseId, String branchId, String productName, int stock);
    Mono<Branch>  removeProduct(String franchiseId, String branchId, String productId);
    Mono<Product> updateStock(String franchiseId, String branchId, String productId, int newStock);
    Flux<ProductWithBranch> findMaxStock(String franchiseId);
    Mono<Franchise> updateFranchiseName(String franchiseId, String newName);
    Mono<Branch> updateBranchName(String franchiseId, String branchId, String newName);
    Mono<Product> updateProductName(String franchiseId, String branchId, String productId, String newName);



}
