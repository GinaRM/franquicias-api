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
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
                            .doOnNext(savedFr -> log.info("Franquicia creada: {} con ID: {}", savedFr.getName(), savedFr.getId()))
                            .doOnError(err -> log.error("Error al crear franquicia: {}", err.getMessage()))
                            .doOnSuccess(savedFr -> log.info("Proceso de creación de franquicia completado"));
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
                        log.warn("Intento de crear sucursal duplicada con nombre: {} en franquicia: {}", branchName, fr.getName());
                        return Mono.error(new BusinessException("Ya existe una sucursal con el mismo nombre en la franquicia"));
                    }

                    Branch b = new Branch(UUID.randomUUID().toString(), branchName, new ArrayList<>());
                    fr.getBranches().add(b);

                    return repo.save(fr)
                            .doOnNext(savedFr -> log.info("Sucursal agregada: {} a franquicia: {}", b.getName(), fr.getName()))
                            .doOnError(err -> log.error("Error al agregar sucursal: {}", err.getMessage()))
                            .doOnSuccess(savedFr -> log.info("Proceso de agregar sucursal completado"))
                            .thenReturn(b);
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
                        log.warn("Intento de crear producto duplicado con nombre: {} en sucursal: {} de franquicia: {}", productName, branch.getName(), fr.getName());
                        return Mono.error(new BusinessException("Ya existe un producto con el mismo nombre en esta sucursal"));
                    }

                    Product p = new Product(UUID.randomUUID().toString(), productName, stock);
                    branch.getProducts().add(p);

                    return repo.save(fr)
                            .doOnNext(savedFr -> log.info("Producto agregado: {} a sucursal: {}", p.getName(), branch.getName()))
                            .doOnError(err -> log.error("Error al agregar producto: {}", err.getMessage()))
                            .doOnSuccess(savedFr -> log.info("Proceso de agregar producto completado"))
                            .thenReturn(p);
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
