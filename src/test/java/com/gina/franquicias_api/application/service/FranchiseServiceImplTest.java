package com.gina.franquicias_api.application.service;

import com.gina.franquicias_api.domain.exception.BusinessException;
import com.gina.franquicias_api.domain.model.Branch;
import com.gina.franquicias_api.domain.model.Franchise;
import com.gina.franquicias_api.domain.model.Product;
import com.gina.franquicias_api.domain.port.out.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
class FranchiseServiceImplTest {

    private FranchiseRepository franchiseRepository;
    private FranchiseServiceImpl franchiseService;

    @BeforeEach
    void setUp() {
        franchiseRepository = Mockito.mock(FranchiseRepository.class);
        franchiseService = new FranchiseServiceImpl(franchiseRepository);
    }

    @Test
    void createFranchise_shouldCreateFranchise_whenNameIsUnique() {
        // Arrange: no hay franquicias con el mismo nombre
        Mockito.when(franchiseRepository.findAll())
                .thenReturn(Flux.empty());

        Mockito.when(franchiseRepository.save(any(Franchise.class)))
                .thenAnswer(invocation -> {
                    Franchise f = invocation.getArgument(0);
                    return Mono.just(f);
                });

        // Act & Assert: crear franquicia con nombre único
        StepVerifier.create(franchiseService.createFranchise("NuevaFranquicia"))
                .expectNextMatches(franchise ->
                        franchise.getName().equals("NuevaFranquicia") &&
                                franchise.getBranches().isEmpty()
                )
                .verifyComplete();

        // Verify: que se llamó a save()
        Mockito.verify(franchiseRepository).save(any(Franchise.class));
    }

    @Test
    void createFranchise_shouldFail_whenNameAlreadyExists() {
        // Arrange: ya existe franquicia con el mismo nombre
        Franchise existing = new Franchise("1", "Duplicada", Collections.emptyList());
        Mockito.when(franchiseRepository.findAll())
                .thenReturn(Flux.just(existing));

        // Act & Assert: lanzar error por nombre duplicado
        StepVerifier.create(franchiseService.createFranchise("Duplicada"))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                throwable.getMessage().equals("Ya existe una franquicia con el mismo nombre"))
                .verify();
    }

    @Test
    void addBranch_shouldAddBranch_whenBranchNameIsUnique() {
        // Arrange: franquicia existente sin sucursales con el mismo nombre
        Franchise existingFranchise = new Franchise("1", "FranquiciaExistente", new ArrayList<>());
        Mockito.when(franchiseRepository.findById("1"))
                .thenReturn(Mono.just(existingFranchise));
        Mockito.when(franchiseRepository.save(any(Franchise.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act & Assert
        StepVerifier.create(franchiseService.addBranch("1", "NuevaSucursal"))
                .expectNextMatches(branch ->
                        branch.name().equals("NuevaSucursal") &&
                                branch.products().isEmpty())
                .verifyComplete();

        Mockito.verify(franchiseRepository).save(any(Franchise.class));
    }

    @Test
    void addBranch_shouldFail_whenBranchNameAlreadyExists() {
        // Arrange: franquicia con una sucursal que ya tiene el mismo nombre
        Branch existingBranch = new Branch("b1", "SucursalDuplicada", new ArrayList<>());
        Franchise existingFranchise = new Franchise("1", "FranquiciaExistente", new ArrayList<>(Collections.singletonList(existingBranch)));
        Mockito.when(franchiseRepository.findById("1"))
                .thenReturn(Mono.just(existingFranchise));

        // Act & Assert
        StepVerifier.create(franchiseService.addBranch("1", "SucursalDuplicada"))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                throwable.getMessage().equals("Ya existe una sucursal con el mismo nombre en la franquicia"))
                .verify();
    }

    @Test
    void addProduct_shouldAddProduct_whenProductNameIsUnique() {
        // Arrange: franquicia con sucursal sin productos con el mismo nombre
        Branch existingBranch = new Branch("b1", "Sucursal", new ArrayList<>());
        Franchise existingFranchise = new Franchise("1", "Franquicia", new ArrayList<>(Collections.singletonList(existingBranch)));

        Mockito.when(franchiseRepository.findById("1"))
                .thenReturn(Mono.just(existingFranchise));
        Mockito.when(franchiseRepository.save(any(Franchise.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act & Assert
        StepVerifier.create(franchiseService.addProduct("1", "b1", "ProductoNuevo", 10))
                .expectNextMatches(product ->
                        product.getName().equals("ProductoNuevo") &&
                                product.getStock() == 10)
                .verifyComplete();

        Mockito.verify(franchiseRepository).save(any(Franchise.class));
    }

    @Test
    void addProduct_shouldFail_whenProductNameAlreadyExists() {
        // Arrange: franquicia con sucursal que ya tiene el producto
        Product existingProduct = new Product("p1", "ProductoDuplicado", 5);
        Branch existingBranch = new Branch("b1", "Sucursal", new ArrayList<>(Collections.singletonList(existingProduct)));
        Franchise existingFranchise = new Franchise("1", "Franquicia", new ArrayList<>(Collections.singletonList(existingBranch)));

        Mockito.when(franchiseRepository.findById("1"))
                .thenReturn(Mono.just(existingFranchise));

        // Act & Assert
        StepVerifier.create(franchiseService.addProduct("1", "b1", "ProductoDuplicado", 10))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                throwable.getMessage().equals("Ya existe un producto con el mismo nombre en esta sucursal"))
                .verify();
    }

    @Test
    void removeProduct_shouldRemoveProduct_whenProductExists() {
        // Arrange: franquicia con sucursal y producto existente
        Product existingProduct = new Product("p1", "ProductoAEliminar", 5);
        Branch existingBranch = new Branch("b1", "Sucursal", new ArrayList<>(Collections.singletonList(existingProduct)));
        Franchise existingFranchise = new Franchise("1", "Franquicia", new ArrayList<>(Collections.singletonList(existingBranch)));

        Mockito.when(franchiseRepository.findById("1"))
                .thenReturn(Mono.just(existingFranchise));
        Mockito.when(franchiseRepository.save(any(Franchise.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act & Assert
        StepVerifier.create(franchiseService.removeProduct("1", "b1", "p1"))
                .expectNextMatches(branch ->
                        branch.products().isEmpty() &&
                                branch.name().equals("Sucursal"))
                .verifyComplete();

        Mockito.verify(franchiseRepository).save(any(Franchise.class));
    }

    @Test
    void removeProduct_shouldFail_whenProductDoesNotExist() {
        // Arrange: franquicia con sucursal sin el producto especificado
        Branch existingBranch = new Branch("b1", "Sucursal", new ArrayList<>());
        Franchise existingFranchise = new Franchise("1", "Franquicia", new ArrayList<>(Collections.singletonList(existingBranch)));

        Mockito.when(franchiseRepository.findById("1"))
                .thenReturn(Mono.just(existingFranchise));

        // Act & Assert
        StepVerifier.create(franchiseService.removeProduct("1", "b1", "pInexistente"))
                .expectErrorMatches(throwable ->
                        throwable instanceof com.gina.franquicias_api.domain.exception.ResourceNotFoundException &&
                                throwable.getMessage().equals("Producto no encontrado en la sucursal"))
                .verify();
    }

    @Test
    void updateStock_shouldUpdateStock_whenProductExists() {
        // Arrange: franquicia con sucursal y producto existente
        Product existingProduct = new Product("p1", "ProductoStock", 5);
        Branch existingBranch = new Branch("b1", "Sucursal", new ArrayList<>(Collections.singletonList(existingProduct)));
        Franchise existingFranchise = new Franchise("1", "Franquicia", new ArrayList<>(Collections.singletonList(existingBranch)));

        Mockito.when(franchiseRepository.findById("1"))
                .thenReturn(Mono.just(existingFranchise));
        Mockito.when(franchiseRepository.save(any(Franchise.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act & Assert
        StepVerifier.create(franchiseService.updateStock("1", "b1", "p1", 50))
                .expectNextMatches(product ->
                        product.getId().equals("p1") &&
                                product.getStock() == 50)
                .verifyComplete();

        Mockito.verify(franchiseRepository).save(any(Franchise.class));
    }

    @Test
    void updateStock_shouldFail_whenProductDoesNotExist() {
        // Arrange: franquicia con sucursal sin el producto
        Branch existingBranch = new Branch("b1", "Sucursal", new ArrayList<>());
        Franchise existingFranchise = new Franchise("1", "Franquicia", new ArrayList<>(Collections.singletonList(existingBranch)));

        Mockito.when(franchiseRepository.findById("1"))
                .thenReturn(Mono.just(existingFranchise));

        // Act & Assert
        StepVerifier.create(franchiseService.updateStock("1", "b1", "pInexistente", 50))
                .expectErrorMatches(throwable ->
                        throwable instanceof com.gina.franquicias_api.domain.exception.ResourceNotFoundException &&
                                throwable.getMessage().equals("Producto no encontrado"))
                .verify();
    }

    @Test
    void findMaxStock_shouldReturnMaxProductPerBranch() {
        // Arrange: franquicia con dos sucursales con productos
        Product p1 = new Product("p1", "Producto1", 10);
        Product p2 = new Product("p2", "Producto2", 5);
        Branch branch1 = new Branch("b1", "Sucursal1", new ArrayList<>(Collections.singletonList(p1)));
        Branch branch2 = new Branch("b2", "Sucursal2", new ArrayList<>(Collections.singletonList(p2)));
        Franchise existingFranchise = new Franchise("1", "Franquicia", new ArrayList<>(List.of(branch1, branch2)));

        Mockito.when(franchiseRepository.findById("1"))
                .thenReturn(Mono.just(existingFranchise));

        // Act & Assert
        StepVerifier.create(franchiseService.findMaxStock("1"))
                .expectNextMatches(pwb -> pwb.getBranchName().equals("Sucursal1") && pwb.getProduct() != null && pwb.getProduct().getId().equals("p1"))
                .expectNextMatches(pwb -> pwb.getBranchName().equals("Sucursal2") && pwb.getProduct() != null && pwb.getProduct().getId().equals("p2"))
                .verifyComplete();
    }

    @Test
    void findMaxStock_shouldFail_whenFranchiseDoesNotExist() {
        // Arrange: franquicia inexistente
        Mockito.when(franchiseRepository.findById("1"))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(franchiseService.findMaxStock("1"))
                .expectErrorMatches(throwable ->
                        throwable instanceof com.gina.franquicias_api.domain.exception.ResourceNotFoundException &&
                                throwable.getMessage().equals("Franquicia no encontrada"))
                .verify();
    }

    @Test
    void updateFranchiseName_shouldUpdateName_whenNewNameIsUnique() {
        // Arrange: franquicia existente con nombre distinto
        Franchise existing = new Franchise("1", "FranquiciaVieja", new ArrayList<>());
        Mockito.when(franchiseRepository.findById("1"))
                .thenReturn(Mono.just(existing));
        Mockito.when(franchiseRepository.findAll())
                .thenReturn(Flux.just(existing)); // No hay otra con el mismo nombre
        Mockito.when(franchiseRepository.save(any(Franchise.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act & Assert
        StepVerifier.create(franchiseService.updateFranchiseName("1", "FranquiciaNueva"))
                .expectNextMatches(fr -> fr.getName().equals("FranquiciaNueva"))
                .verifyComplete();

        Mockito.verify(franchiseRepository).save(any(Franchise.class));
    }

    @Test
    void updateFranchiseName_shouldFail_whenNewNameAlreadyExists() {
        // Arrange: dos franquicias, una con el nombre que queremos usar
        Franchise existing = new Franchise("1", "FranquiciaVieja", new ArrayList<>());
        Franchise other = new Franchise("2", "Duplicada", new ArrayList<>());
        Mockito.when(franchiseRepository.findById("1"))
                .thenReturn(Mono.just(existing));
        Mockito.when(franchiseRepository.findAll())
                .thenReturn(Flux.just(existing, other));

        // Act & Assert
        StepVerifier.create(franchiseService.updateFranchiseName("1", "Duplicada"))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ex.getMessage().equals("Ya existe una franquicia con ese nombre"))
                .verify();
    }

    @Test
    void updateBranchName_shouldUpdateName_whenNewNameIsUnique() {
        // Arrange
        Branch branch = new Branch("b1", "SucursalVieja", new ArrayList<>());
        Franchise franchise = new Franchise("1", "Franquicia", new ArrayList<>(List.of(branch)));

        Mockito.when(franchiseRepository.findById("1"))
                .thenReturn(Mono.just(franchise));
        Mockito.when(franchiseRepository.save(any(Franchise.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act & Assert
        StepVerifier.create(franchiseService.updateBranchName("1", "b1", "SucursalNueva"))
                .expectNextMatches(b -> b.name().equals("SucursalNueva"))
                .verifyComplete();

        Mockito.verify(franchiseRepository).save(any(Franchise.class));
    }

    @Test
    void updateBranchName_shouldFail_whenNewNameAlreadyExists() {
        // Arrange
        Branch branch1 = new Branch("b1", "SucursalVieja", new ArrayList<>());
        Branch branch2 = new Branch("b2", "SucursalDuplicada", new ArrayList<>());
        Franchise franchise = new Franchise("1", "Franquicia", new ArrayList<>(List.of(branch1, branch2)));

        Mockito.when(franchiseRepository.findById("1"))
                .thenReturn(Mono.just(franchise));

        // Act & Assert
        StepVerifier.create(franchiseService.updateBranchName("1", "b1", "SucursalDuplicada"))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ex.getMessage().equals("Ya existe una sucursal con ese nombre"))
                .verify();
    }

    @Test
    void updateProductName_shouldUpdateName_whenNewNameIsUnique() {
        // Arrange
        Product product = new Product("p1", "ProductoViejo", 5);
        Branch branch = new Branch("b1", "Sucursal", new ArrayList<>(List.of(product)));
        Franchise franchise = new Franchise("1", "Franquicia", new ArrayList<>(List.of(branch)));

        Mockito.when(franchiseRepository.findById("1"))
                .thenReturn(Mono.just(franchise));
        Mockito.when(franchiseRepository.save(any(Franchise.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act & Assert
        StepVerifier.create(franchiseService.updateProductName("1", "b1", "p1", "ProductoNuevo"))
                .expectNextMatches(p -> p.getName().equals("ProductoNuevo"))
                .verifyComplete();

        Mockito.verify(franchiseRepository).save(any(Franchise.class));
    }

    @Test
    void updateProductName_shouldFail_whenNewNameAlreadyExists() {
        // Arrange
        Product product1 = new Product("p1", "ProductoViejo", 5);
        Product product2 = new Product("p2", "Duplicado", 10);
        Branch branch = new Branch("b1", "Sucursal", new ArrayList<>(List.of(product1, product2)));
        Franchise franchise = new Franchise("1", "Franquicia", new ArrayList<>(List.of(branch)));

        Mockito.when(franchiseRepository.findById("1"))
                .thenReturn(Mono.just(franchise));

        // Act & Assert
        StepVerifier.create(franchiseService.updateProductName("1", "b1", "p1", "Duplicado"))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ex.getMessage().equals("Ya existe un producto con ese nombre en la sucursal"))
                .verify();
    }



}
