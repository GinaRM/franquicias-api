package com.gina.franquicias_api.domain.port.in;

import com.gina.franquicias_api.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface FranchiseService {
    Mono<Franchise> createFranchise(String name);
    Mono<Franchise> addBranch(String franchiseId, String branchName);
}
