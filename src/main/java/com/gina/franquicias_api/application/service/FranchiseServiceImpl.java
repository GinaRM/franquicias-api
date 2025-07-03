package com.gina.franquicias_api.application.service;

import com.gina.franquicias_api.domain.model.Branch;
import com.gina.franquicias_api.domain.model.Franchise;
import com.gina.franquicias_api.domain.port.in.FranchiseService;
import com.gina.franquicias_api.domain.port.out.FranchiseRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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
    public Mono<Franchise> addBranch(String franchiseId, String branchName) {
        return repo.findById(franchiseId)
                .flatMap(f -> {
                    Branch b = new Branch(UUID.randomUUID().toString(), branchName, new ArrayList<>());
                    f.addBranch(b);     // muta la lista interna
                    return repo.save(f); // persiste con branch nuevo
                });
    }

}
