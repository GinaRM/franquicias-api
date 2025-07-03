package com.gina.franquicias_api.infrastructure.adapter.out.mongodb.adapter;

import com.gina.franquicias_api.domain.model.Franchise;
import com.gina.franquicias_api.domain.port.out.FranchiseRepository;
import com.gina.franquicias_api.infrastructure.adapter.out.mongodb.mapper.FranchiseMapper;
import com.gina.franquicias_api.infrastructure.adapter.out.mongodb.repository.FranchiseMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class FranchiseMongoAdapter implements FranchiseRepository {
    private final FranchiseMongoRepository repo;
    private final FranchiseMapper mapper;

    public FranchiseMongoAdapter(FranchiseMongoRepository repo, FranchiseMapper mapper) {
        this.repo   = repo;
        this.mapper = mapper;
    }
    @Override
    public Mono<Franchise> save(Franchise f) {
        return repo.save(mapper.toDocument(f))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Flux<Franchise> findAll() {
        return repo.findAll().map(mapper::toDomain);
    }
}
