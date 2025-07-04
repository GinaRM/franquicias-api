package com.gina.franquicias_api.presentation.web.controller;

import com.gina.franquicias_api.application.dto.*;
import com.gina.franquicias_api.application.mapper.FranchiseDtoMapper;
import com.gina.franquicias_api.domain.model.Branch;
import com.gina.franquicias_api.domain.port.in.FranchiseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/franchises")
public class FranchiseController {
    private final FranchiseService svc;
    private final FranchiseDtoMapper mapper;

    public FranchiseController(FranchiseService svc, FranchiseDtoMapper mapper) {
        this.svc = svc;
        this.mapper = mapper;
    }

    @PostMapping
    public Mono<ResponseEntity<FranchiseResponseDto>> create(@RequestBody FranchiseRequestDto request) {
        return svc.createFranchise(request.getName())
                .map(mapper::toResponse)
                .map(f -> ResponseEntity.status(HttpStatus.CREATED).body(f));
    }

    @PostMapping("/{franchiseId}/branches")
    public Mono<ResponseEntity<BranchResponseDto>> addBranch(
            @PathVariable String franchiseId,
            @RequestBody BranchRequestDto request) {

        return svc.addBranch(franchiseId, request.getName())
                .map(mapper::toResponse)
                .map(b -> ResponseEntity.status(HttpStatus.CREATED).body(b));
    }

    @PostMapping("/{franchiseId}/branches/{branchId}/products")
    public Mono<ResponseEntity<ProductResponseDto>> addProduct(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @RequestBody ProductRequestDto request) {

        return svc.addProduct(franchiseId, branchId, request.getName(), request.getStock())
                .map(mapper::toResponse)
                .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(p));
    }

    @DeleteMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    public Mono<ResponseEntity<BranchResponseDto>> removeProduct(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @PathVariable String productId) {

        return svc.removeProduct(franchiseId, branchId, productId)
                .map(mapper::toResponse)
                .map(b -> ResponseEntity.ok().body(b));
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/stock")
    public Mono<ResponseEntity<ProductResponseDto>> updateStock(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @PathVariable String productId,
            @RequestBody UpdateStockRequestDto request) {

        return svc.updateStock(franchiseId, branchId, productId, request.getStock())
                .map(mapper::toResponse)
                .map(p -> ResponseEntity.ok().body(p));
    }

    @GetMapping("/{franchiseId}/max-stock")
    public Flux<ProductWithBranchResponseDto> getMaxStockPerBranch(
            @PathVariable String franchiseId) {

        return svc.findMaxStock(franchiseId)
                .map(mapper::toResponse);
    }


}
