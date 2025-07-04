package com.gina.franquicias_api.application.service;

import com.gina.franquicias_api.domain.exception.BusinessException;
import com.gina.franquicias_api.domain.model.Branch;
import com.gina.franquicias_api.domain.model.Franchise;
import com.gina.franquicias_api.domain.port.out.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Collections;
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
                        branch.getName().equals("NuevaSucursal") &&
                                branch.getProducts().isEmpty())
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


}
