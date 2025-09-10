package com.gina.franquicias_api.presentation.web.controller;

import com.gina.franquicias_api.application.dto.request.*;
import com.gina.franquicias_api.application.dto.response.BranchResponseDto;
import com.gina.franquicias_api.application.dto.response.FranchiseResponseDto;
import com.gina.franquicias_api.application.dto.response.ProductResponseDto;
import com.gina.franquicias_api.application.dto.response.ProductWithBranchResponseDto;
import com.gina.franquicias_api.application.mapper.FranchiseDtoMapper;
import com.gina.franquicias_api.domain.model.Branch;
import com.gina.franquicias_api.domain.model.Franchise;
import com.gina.franquicias_api.domain.model.Product;
import com.gina.franquicias_api.domain.model.ProductWithBranch;
import com.gina.franquicias_api.domain.port.in.FranchiseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class FranchiseControllerTest {

    private WebTestClient webTestClient;
    private FranchiseService franchiseService; // mock manual
    private FranchiseDtoMapper mapper; // mock manual

    @BeforeEach
    void setUp() {
        // Crear mocks con Mockito
        franchiseService = Mockito.mock(FranchiseService.class);
        mapper = Mockito.mock(FranchiseDtoMapper.class);

        // Instanciar el controller con los mocks
        FranchiseController controller = new FranchiseController(franchiseService, mapper);

        // Construir el WebTestClient directamente sobre el controller
        this.webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    void createFranchise_shouldReturnCreatedFranchise() {
        // Arrange
        FranchiseRequestDto requestDto = new FranchiseRequestDto("NuevaFranquicia");
        // Crear un Branch vacío para agregar a Franchise
        Branch dummyBranch = new Branch("b1", "SucursalPrincipal", Collections.emptyList());
        Franchise domainFranchise = new Franchise("1", "NuevaFranquicia", Collections.singletonList(dummyBranch));
        FranchiseResponseDto responseDto = new FranchiseResponseDto("1", "NuevaFranquicia", Collections.emptyList());


        when(franchiseService.createFranchise("NuevaFranquicia"))
                .thenReturn(Mono.just(domainFranchise));

        when(mapper.toResponse(any(Franchise.class)))
                .thenReturn(responseDto);

        // Act & Assert
        webTestClient.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.name").isEqualTo("NuevaFranquicia");
    }

    @Test
    void addBranch_shouldReturnCreatedBranch() {
        // Arrange
        String franchiseId = "1";
        BranchRequestDto requestDto = new BranchRequestDto("NuevaSucursal");
        Branch domainBranch = new Branch("b1", "NuevaSucursal", Collections.emptyList());
        BranchResponseDto responseDto = new BranchResponseDto("b1", "NuevaSucursal", Collections.emptyList());

        when(franchiseService.addBranch(franchiseId, "NuevaSucursal"))
                .thenReturn(Mono.just(domainBranch));

        when(mapper.toResponse(any(Branch.class)))
                .thenReturn(responseDto);

        // Act & Assert
        webTestClient.post()
                .uri("/api/franchises/{franchiseId}/branches", franchiseId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("b1")
                .jsonPath("$.name").isEqualTo("NuevaSucursal");
    }

    @Test
    void addProduct_shouldReturnCreatedProduct() {
        // Arrange
        String franchiseId = "1";
        String branchId = "b1";
        ProductRequestDto requestDto = new ProductRequestDto("NuevoProducto", 20);
        Product domainProduct = new Product("p1", "NuevoProducto", 20);
        ProductResponseDto responseDto = new ProductResponseDto("p1", "NuevoProducto", 20);

        when(franchiseService.addProduct(franchiseId, branchId, "NuevoProducto", 20))
                .thenReturn(Mono.just(domainProduct));

        when(mapper.toResponse(any(Product.class)))
                .thenReturn(responseDto);

        // Act & Assert
        webTestClient.post()
                .uri("/api/franchises/{franchiseId}/branches/{branchId}/products", franchiseId, branchId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("p1")
                .jsonPath("$.name").isEqualTo("NuevoProducto")
                .jsonPath("$.stock").isEqualTo(20);
    }

    @Test
    void removeProduct_shouldReturnUpdatedBranch() {
        // Arrange
        String franchiseId = "1";
        String branchId = "b1";
        String productId = "p1";
        Branch domainBranch = new Branch(branchId, "Sucursal", Collections.emptyList());
        BranchResponseDto responseDto = new BranchResponseDto(branchId, "Sucursal", Collections.emptyList());

        when(franchiseService.removeProduct(franchiseId, branchId, productId))
                .thenReturn(Mono.just(domainBranch));

        when(mapper.toResponse(any(Branch.class)))
                .thenReturn(responseDto);

        // Act & Assert
        webTestClient.delete()
                .uri("/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}",
                        franchiseId, branchId, productId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(branchId)
                .jsonPath("$.name").isEqualTo("Sucursal");
    }

    @Test
    void updateStock_shouldReturnUpdatedProduct() {
        // Arrange
        String franchiseId = "1";
        String branchId = "b1";
        String productId = "p1";
        UpdateStockRequestDto requestDto = new UpdateStockRequestDto(50);
        Product domainProduct = new Product(productId, "Producto", 50);
        ProductResponseDto responseDto = new ProductResponseDto(productId, "Producto", 50);

        when(franchiseService.updateStock(franchiseId, branchId, productId, 50))
                .thenReturn(Mono.just(domainProduct));

        when(mapper.toResponse(any(Product.class)))
                .thenReturn(responseDto);

        // Act & Assert
        webTestClient.patch()
                .uri("/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock",
                        franchiseId, branchId, productId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(productId)
                .jsonPath("$.name").isEqualTo("Producto")
                .jsonPath("$.stock").isEqualTo(50);
    }

    @Test
    void getMaxStockPerBranch_shouldReturnProductsWithMaxStock() {
        // Arrange
        String franchiseId = "1";
        ProductWithBranch domainPwb = new ProductWithBranch("Sucursal", new Product("p1", "ProductoMax", 100));
        ProductResponseDto productDto = new ProductResponseDto("p1", "ProductoMax", 100);
        ProductWithBranchResponseDto responseDto = new ProductWithBranchResponseDto("Sucursal", productDto);

        when(franchiseService.findMaxStock(franchiseId))
                .thenReturn(Flux.just(domainPwb));

        when(mapper.toResponse(any(ProductWithBranch.class)))
                .thenReturn(responseDto);

        // Act & Assert
        webTestClient.get()
                .uri("/api/franchises/{franchiseId}/max-stock", franchiseId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].branchName").isEqualTo("Sucursal")
                .jsonPath("$[0].product.id").isEqualTo("p1")
                .jsonPath("$[0].product.name").isEqualTo("ProductoMax")
                .jsonPath("$[0].product.stock").isEqualTo(100);
    }

    @Test
    void renameFranchise_shouldReturnUpdatedFranchise() {
        // Arrange
        String franchiseId = "1";
        UpdateNameRequestDto requestDto = new UpdateNameRequestDto("FranquiciaNueva");
        Franchise domainFranchise = new Franchise(franchiseId, "FranquiciaNueva", Collections.emptyList());
        FranchiseResponseDto responseDto = new FranchiseResponseDto(franchiseId, "FranquiciaNueva", Collections.emptyList());

        when(franchiseService.updateFranchiseName(franchiseId, "FranquiciaNueva"))
                .thenReturn(Mono.just(domainFranchise));

        when(mapper.toResponse(any(Franchise.class)))
                .thenReturn(responseDto);

        // Act & Assert
        webTestClient.patch()
                .uri("/api/franchises/{franchiseId}", franchiseId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(franchiseId)
                .jsonPath("$.name").isEqualTo("FranquiciaNueva");
    }

    @Test
    void renameBranch_shouldReturnUpdatedBranch() {
        // Arrange
        String franchiseId = "1";
        String branchId = "b1";
        UpdateNameRequestDto requestDto = new UpdateNameRequestDto("SucursalNueva");
        Branch domainBranch = new Branch(branchId, "SucursalNueva", Collections.emptyList());
        BranchResponseDto responseDto = new BranchResponseDto(branchId, "SucursalNueva", Collections.emptyList());

        when(franchiseService.updateBranchName(franchiseId, branchId, "SucursalNueva"))
                .thenReturn(Mono.just(domainBranch));

        when(mapper.toResponse(any(Branch.class)))
                .thenReturn(responseDto);

        // Act & Assert
        webTestClient.patch()
                .uri("/api/franchises/{franchiseId}/branches/{branchId}", franchiseId, branchId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(branchId)
                .jsonPath("$.name").isEqualTo("SucursalNueva");
    }

    @Test
    void renameProduct_shouldReturnUpdatedProduct() {
        // Arrange
        String franchiseId = "1";
        String branchId = "b1";
        String productId = "p1";
        UpdateNameRequestDto requestDto = new UpdateNameRequestDto("ProductoNuevo");
        Product domainProduct = new Product(productId, "ProductoNuevo", 10);
        ProductResponseDto responseDto = new ProductResponseDto(productId, "ProductoNuevo", 10);

        when(franchiseService.updateProductName(franchiseId, branchId, productId, "ProductoNuevo"))
                .thenReturn(Mono.just(domainProduct));

        when(mapper.toResponse(any(Product.class)))
                .thenReturn(responseDto);

        // Act & Assert
        webTestClient.patch()
                .uri("/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}",
                        franchiseId, branchId, productId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(productId)
                .jsonPath("$.name").isEqualTo("ProductoNuevo")
                .jsonPath("$.stock").isEqualTo(10);
    }


}

