package com.gina.franquicias_api.presentation.web.controller;

import com.gina.franquicias_api.application.dto.FranchiseRequestDto;
import com.gina.franquicias_api.application.dto.FranchiseResponseDto;
import com.gina.franquicias_api.application.mapper.FranchiseDtoMapper;
import com.gina.franquicias_api.domain.model.Branch;
import com.gina.franquicias_api.domain.model.Franchise;
import com.gina.franquicias_api.domain.port.in.FranchiseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class FranchiseControllerUnitTest {

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
}
