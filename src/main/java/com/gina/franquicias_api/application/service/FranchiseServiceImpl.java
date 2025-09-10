package com.gina.franquicias_api.application.service;

import com.gina.franquicias_api.domain.exception.BusinessException;
import com.gina.franquicias_api.domain.exception.ResourceNotFoundException;
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
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FranchiseServiceImpl.class);

    public FranchiseServiceImpl(FranchiseRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Franchise> createFranchise(String name) {
        return repo.findAll()
                .filter(fr -> fr.getName().equalsIgnoreCase(name))
                .hasElements()
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("Intento de crear franquicia duplicada con nombre: {}", name);
                        return Mono.error(new BusinessException("Ya existe una franquicia con el mismo nombre"));
                    }
                    Franchise f = new Franchise(UUID.randomUUID().toString(), name, new ArrayList<>());
                    return repo.save(f)
                            .doOnNext(savedFr -> log.info("Franquicia creada: {} con ID: {}", savedFr.getName(), savedFr.getId()));
                });
    }

    @Override
    public Mono<Branch> addBranch(String franchiseId, String branchName) {
        return repo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada")))
                .flatMap(fr -> {
                    boolean exists = fr.getBranches().stream()
                            .anyMatch(b -> b.getName().equalsIgnoreCase(branchName));
                    if (exists) {
                        return Mono.error(new BusinessException("Ya existe una sucursal con el mismo nombre en la franquicia"));
                    }

                    Branch newBranch = new Branch(UUID.randomUUID().toString(), branchName, new ArrayList<>());
                    fr.addBranch(newBranch);

                    return repo.save(fr).thenReturn(newBranch);
                });
    }

    @Override
    public Mono<Product> addProduct(String franchiseId, String branchId, String productName, int stock) {
        return repo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada")))
                .flatMap(fr -> {
                    Branch branch = fr.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

                    boolean productExists = branch.getProducts().stream()
                            .anyMatch(p -> p.getName().equalsIgnoreCase(productName));
                    if (productExists) {
                        return Mono.error(new BusinessException("Ya existe un producto con el mismo nombre en esta sucursal"));
                    }

                    Product newProduct = new Product(UUID.randomUUID().toString(), productName, stock);
                    branch.addProduct(newProduct);

                    return repo.save(fr).thenReturn(newProduct);
                });
    }

    @Override
    public Mono<Branch> removeProduct(String franchiseId, String branchId, String productId) {
        return repo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada")))
                .flatMap(fr -> {
                    Branch branch = fr.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

                    boolean removed = branch.removeProductById(productId);
                    if (!removed) {
                        return Mono.error(new ResourceNotFoundException("Producto no encontrado en la sucursal"));
                    }

                    return repo.save(fr).thenReturn(branch);
                });
    }

    @Override
    public Mono<Product> updateStock(String franchiseId, String branchId, String productId, int newStock) {
        return repo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada")))
                .flatMap(fr -> {
                    Branch branch = fr.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

                    Product product = branch.getProducts().stream()
                            .filter(p -> p.getId().equals(productId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

                    product.updateStock(newStock);

                    return repo.save(fr).thenReturn(product);
                });
    }

    @Override
    public Flux<ProductWithBranch> findMaxStock(String franchiseId) {
        return repo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada")))
                .flatMapMany(fr ->
                        Flux.fromIterable(fr.getBranches())
                                .map(branch -> {
                                    Product max = branch.getProducts().stream()
                                            .max(Comparator.comparingInt(Product::getStock))
                                            .orElse(null);
                                    return new ProductWithBranch(branch.getName(), max);
                                })
                );
    }

    @Override
    public Mono<Franchise> updateFranchiseName(String franchiseId, String newName) {
        return repo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada")))
                .flatMap(existing -> repo.findAll()
                        .filter(fr -> fr.getName().equalsIgnoreCase(newName) && !fr.getId().equals(franchiseId))
                        .hasElements()
                        .flatMap(dup -> {
                            if (dup) {
                                return Mono.error(new BusinessException("Ya existe una franquicia con ese nombre"));
                            }
                            existing.rename(newName);
                            return repo.save(existing);
                        })
                );
    }

    @Override
    public Mono<Branch> updateBranchName(String franchiseId, String branchId, String newName) {
        return repo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada")))
                .flatMap(fr -> {
                    Branch branch = fr.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

                    boolean exists = fr.getBranches().stream()
                            .anyMatch(b -> b.getName().equalsIgnoreCase(newName) && !b.getId().equals(branchId));
                    if (exists) return Mono.error(new BusinessException("Ya existe una sucursal con ese nombre"));

                    branch.rename(newName);
                    return repo.save(fr).thenReturn(branch);
                });
    }

    @Override
    public Mono<Product> updateProductName(String franchiseId, String branchId, String productId, String newName) {
        return repo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada")))
                .flatMap(fr -> {
                    Branch branch = fr.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

                    boolean dup = branch.getProducts().stream()
                            .anyMatch(p -> p.getName().equalsIgnoreCase(newName) && !p.getId().equals(productId));
                    if (dup) return Mono.error(new BusinessException("Ya existe un producto con ese nombre en la sucursal"));

                    Product product = branch.getProducts().stream()
                            .filter(p -> p.getId().equals(productId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

                    product.rename(newName);
                    return repo.save(fr).thenReturn(product);
                });
    }
}
