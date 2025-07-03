package com.gina.franquicias_api.infrastructure.adapter.out.mongodb.mapper;

import com.gina.franquicias_api.domain.model.Franchise;
import com.gina.franquicias_api.infrastructure.adapter.out.mongodb.entity.FranchiseDocument;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class FranchiseMapper {
    public FranchiseDocument toDocument(Franchise f) {
        FranchiseDocument d = new FranchiseDocument();
        d.setId(f.getId());
        d.setName(f.getName());

        d.setBranches(Collections.emptyList());
        return d;
    }
    public Franchise toDomain(FranchiseDocument d) {
        return new Franchise(
                d.getId(),
                d.getName(),
                Collections.emptyList()
        );
    }
}
