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
                            .anyMatch(b -> b.name().equalsIgnoreCase(branchName));
                    if (exists) {
                        log.warn("Intento de crear sucursal duplicada con nombre: {} en franquicia: {}", branchName, fr.getName());
                        return Mono.error(new BusinessException("Ya existe una sucursal con el mismo nombre en la franquicia"));
                    }

                    Branch b = new Branch(UUID.randomUUID().toString(), branchName, new ArrayList<>());
                    fr.getBranches().add(b);

                    return repo.save(fr)
                            .doOnNext(savedFr -> log.info("Sucursal agregada: {} a franquicia: {}", b.name(), fr.getName()))
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
                            .filter(b -> b.id().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

                    boolean productExists = branch.products().stream()
                            .anyMatch(p -> p.getName().equalsIgnoreCase(productName));
                    if (productExists) {
                        log.warn("Intento de crear producto duplicado con nombre: {} en sucursal: {} de franquicia: {}", productName, branch.name(), fr.getName());
                        return Mono.error(new BusinessException("Ya existe un producto con el mismo nombre en esta sucursal"));
                    }

                    Product p = new Product(UUID.randomUUID().toString(), productName, stock);
                    branch.products().add(p);

                    return repo.save(fr)
                            .doOnNext(savedFr -> log.info("Producto agregado: {} a sucursal: {}", p.getName(), branch.name()))
                            .doOnError(err -> log.error("Error al agregar producto: {}", err.getMessage()))
                            .doOnSuccess(savedFr -> log.info("Proceso de agregar producto completado"))
                            .thenReturn(p);
                });
    }




    @Override
    public Mono<Branch> removeProduct(String franchiseId, String branchId, String productId) {
        return repo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada")))
                .flatMap(fr -> {
                    Branch branch = fr.getBranches().stream()
                            .filter(b -> b.id().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

                    boolean removed = branch.products().removeIf(p -> p.getId().equals(productId));
                    if (!removed) {
                        return Mono.error(new ResourceNotFoundException("Producto no encontrado en la sucursal"));
                    }

                    return repo.save(fr)
                            .doOnNext(savedFr -> log.info("Producto eliminado: {} de sucursal: {}", productId, branch.name()))
                            .doOnError(err -> log.error("Error al eliminar producto: {}", err.getMessage()))
                            .doOnSuccess(savedFr -> log.info("Proceso de eliminación de producto completado"))
                            .thenReturn(branch);
                });
    }


    @Override
    public Mono<Product> updateStock(String franchiseId, String branchId, String productId, int newStock) {
        return repo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada")))
                .flatMap(fr -> {
                    Branch branch = fr.getBranches().stream()
                            .filter(b -> b.id().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

                    Product product = branch.products().stream()
                            .filter(p -> p.getId().equals(productId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

                    product.setStock(newStock);

                    return repo.save(fr)
                            .doOnNext(savedFr -> log.info("Stock actualizado para producto: {} en sucursal: {}. Nuevo stock: {}", product.getName(), branch.name(), newStock))
                            .doOnError(err -> log.error("Error al actualizar stock: {}", err.getMessage()))
                            .doOnSuccess(savedFr -> log.info("Proceso de actualización de stock completado"))
                            .thenReturn(product);
                });
    }


    @Override
    public Flux<ProductWithBranch> findMaxStock(String franchiseId) {
        return repo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada")))
                .flatMapMany(fr ->
                        Flux.fromIterable(fr.getBranches())
                                .map(branch -> {
                                    Product max = branch.products().stream()
                                            .max(Comparator.comparingInt(Product::getStock))
                                            .orElse(null);
                                    return new ProductWithBranch(branch.name(), max);
                                })
                )
                .doOnNext(pwb -> log.info("Producto máximo encontrado en sucursal: {}", pwb.getBranchName()))
                .doOnError(err -> log.error("Error al obtener productos con mayor stock: {}", err.getMessage()))
                .doOnComplete(() -> log.info("Proceso de obtención de productos con mayor stock completado"));
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
                            Franchise updated = new Franchise(existing.getId(), newName, existing.getBranches());
                            return repo.save(updated);
                        })
                );
    }

    @Override
    public Mono<Branch> updateBranchName(String franchiseId, String branchId, String newName) {
        return repo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada")))
                .flatMap(fr -> {
                    int idx = -1;
                    for (int i = 0; i < fr.getBranches().size(); i++) {
                        if (fr.getBranches().get(i).id().equals(branchId)) { idx = i; break; }
                    }
                    if (idx == -1) return Mono.error(new ResourceNotFoundException("Sucursal no encontrada"));

                    boolean exists = fr.getBranches().stream()
                            .anyMatch(b -> b.name().equalsIgnoreCase(newName) && !b.id().equals(branchId));
                    if (exists) return Mono.error(new BusinessException("Ya existe una sucursal con ese nombre"));

                    Branch current = fr.getBranches().get(idx);
                    Branch renamed = new Branch(current.id(), newName, current.products());
                    fr.getBranches().set(idx, renamed);

                    return repo.save(fr).thenReturn(renamed);
                });
    }

    @Override
    public Mono<Product> updateProductName(String franchiseId, String branchId, String productId, String newName) {
        return repo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada")))
                .flatMap(fr -> {
                    Branch branch = fr.getBranches().stream()
                            .filter(b -> b.id().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

                    boolean dup = branch.products().stream()
                            .anyMatch(p -> p.getName().equalsIgnoreCase(newName) && !p.getId().equals(productId));
                    if (dup) return Mono.error(new BusinessException("Ya existe un producto con ese nombre en la sucursal"));

                    Product product = branch.products().stream()
                            .filter(p -> p.getId().equals(productId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

                    product.setName(newName);
                    return repo.save(fr).thenReturn(product);
                });
    }



}
