package com.gina.franquicias_api.infrastructure.adapter.out.mongodb.repository;

import com.gina.franquicias_api.infrastructure.adapter.out.mongodb.entity.FranchiseDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FranchiseMongoRepository extends ReactiveMongoRepository<FranchiseDocument, String> {
}
