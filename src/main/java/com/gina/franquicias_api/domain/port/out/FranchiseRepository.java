package com.gina.franquicias_api.domain.port.out;

import com.gina.franquicias_api.domain.model.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {
    //outbound port: define lo que la lógica del negocio necesita del exterior para funcionar (como guardar datos).
    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(String id);
    Flux<Franchise> findAll();
}
