package com.gina.franquicias_api.infrastructure.adapter.out.mogodb.mapper;

import com.gina.franquicias_api.domain.model.Franchise;
import com.gina.franquicias_api.infrastructure.adapter.out.mogodb.entity.FranchiseDocument;
import org.springframework.stereotype.Component;

@Component
public class FranchiseMapper {
    public FranchiseDocument toDocument(Franchise f) { /* mapea campos */ return null;}
    public Franchise toDomain(FranchiseDocument d) { /* mapea recursivamente Branch/Products */ return null; }
}
