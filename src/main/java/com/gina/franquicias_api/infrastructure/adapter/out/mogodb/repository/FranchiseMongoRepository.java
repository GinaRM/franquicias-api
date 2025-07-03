package com.gina.franquicias_api.infrastructure.adapter.out.mogodb.repository;

import com.gina.franquicias_api.infrastructure.adapter.out.mogodb.entity.FranchiseDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FranchiseMongoRepository extends ReactiveMongoRepository<FranchiseDocument, String> {
}
