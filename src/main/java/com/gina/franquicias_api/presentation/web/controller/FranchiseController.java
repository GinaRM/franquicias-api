package com.gina.franquicias_api.presentation.web.controller;

import com.gina.franquicias_api.domain.model.Branch;
import com.gina.franquicias_api.domain.model.Franchise;
import com.gina.franquicias_api.domain.port.in.FranchiseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/franchises")
public class FranchiseController {
    private final FranchiseService svc;

    public FranchiseController(FranchiseService svc) {
        this.svc = svc;
    }

    @PostMapping
    public Mono<ResponseEntity<Franchise>> create(@RequestBody Map<String, String> body) {
        return svc.createFranchise(body.get("name"))
                .map(f -> ResponseEntity.status(HttpStatus.CREATED).body(f));
    }

    @PostMapping("/{franchiseId}/branches")
    public Mono<ResponseEntity<Branch>> addBranch(
            @PathVariable String franchiseId,
            @RequestBody Map<String, String> body) {

        return svc.addBranch(franchiseId, body.get("name"))
                .map(b -> ResponseEntity.status(HttpStatus.CREATED).body(b));
    }

}
