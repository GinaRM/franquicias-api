package com.gina.franquicias_api.application.service;

import com.gina.franquicias_api.domain.model.Branch;
import com.gina.franquicias_api.domain.model.Franchise;
import com.gina.franquicias_api.domain.model.Product;
import com.gina.franquicias_api.domain.model.ProductWithBranch;
import com.gina.franquicias_api.domain.port.in.FranchiseService;
import com.gina.franquicias_api.domain.port.out.FranchiseRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

@Service
public class FranchiseServiceImpl implements FranchiseService {
    private final FranchiseRepository repo;

    public FranchiseServiceImpl(FranchiseRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Franchise> createFranchise(String name) {
        // inicializa sin sucursales
        Franchise f = new Franchise(UUID.randomUUID().toString(), name, new ArrayList<>());
        return repo.save(f);
    }

    @Override
    public Mono<Branch> addBranch(String franchiseId, String branchName) {
        return repo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
                .flatMap(fr -> {
                    Branch b = new Branch(UUID.randomUUID().toString(), branchName, new ArrayList<>());
                    fr.getBranches().add(b);
                    return repo.save(fr)
                            .map(savedFr -> b);   // aquí devolvemos b en vez de savedFr
                });
    }


    @Override
    public Mono<Branch> addProduct(String franchiseId, String branchId, String productName, int stock) {
        return repo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
                .flatMap(fr -> {
                    Branch branch = fr.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));
                    Product p = new Product(UUID.randomUUID().toString(), productName, stock);
                    branch.getProducts().add(p);
                    return repo.save(fr).thenReturn(branch);
                });
    }

    @Override
    public Mono<Branch> removeProduct(String franchiseId, String branchId, String productId) {
        return repo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
                .flatMap(fr -> {
                    Branch branch = fr.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));
                    boolean removed = branch.getProducts().removeIf(p -> p.getId().equals(productId));
                    if (!removed) {
                        return Mono.error(new RuntimeException("Producto no encontrado en la sucursal"));
                    }
                    return repo.save(fr).thenReturn(branch);
                });
    }

    @Override
    public Mono<Product> updateStock(String franchiseId, String branchId, String productId, int newStock) {
        return repo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
                .flatMap(fr -> {
                    Branch branch = fr.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));
                    Product prod = branch.getProducts().stream()
                            .filter(p -> p.getId().equals(productId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                    prod.setStock(newStock);
                    return repo.save(fr).thenReturn(prod);
                });
    }

    @Override
    public Flux<ProductWithBranch> findMaxStock(String franchiseId) {
        return repo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
                .flatMapMany(fr ->
                        Flux.fromIterable(fr.getBranches())
                                .map(br -> {
                                    Product max = br.getProducts().stream()
                                            .max(Comparator.comparingInt(Product::getStock))
                                            .orElse(null);
                                    return new ProductWithBranch(br.getName(), max);
                                })
                );
    }


}
