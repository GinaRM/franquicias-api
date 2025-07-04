package com.gina.franquicias_api.domain.port.in;

import com.gina.franquicias_api.domain.model.Branch;
import com.gina.franquicias_api.domain.model.Franchise;
import com.gina.franquicias_api.domain.model.Product;
import com.gina.franquicias_api.domain.model.ProductWithBranch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseService {
    Mono<Franchise> createFranchise(String name);
    Mono<Branch> addBranch(String franchiseId, String branchName);
    Mono<Branch>  addProduct(String franchiseId, String branchId, String productName, int stock);
    Mono<Branch>  removeProduct(String franchiseId, String branchId, String productId);
    Mono<Product> updateStock(String franchiseId, String branchId, String productId, int newStock);
    Flux<ProductWithBranch> findMaxStock(String franchiseId);


}
