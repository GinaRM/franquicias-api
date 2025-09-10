package com.gina.franquicias_api.infrastructure.adapter.out.mongodb.mapper;

import com.gina.franquicias_api.domain.model.Branch;
import com.gina.franquicias_api.domain.model.Franchise;
import com.gina.franquicias_api.domain.model.Product;
import com.gina.franquicias_api.infrastructure.adapter.out.mongodb.entity.BranchDocument;
import com.gina.franquicias_api.infrastructure.adapter.out.mongodb.entity.FranchiseDocument;
import com.gina.franquicias_api.infrastructure.adapter.out.mongodb.entity.ProductDocument;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FranchiseMapper {
    public FranchiseDocument toDocument(Franchise f) {
        FranchiseDocument d = new FranchiseDocument();
        d.setId(f.getId());
        d.setName(f.getName());
        d.setBranches(
                f.getBranches().stream()
                        .map(b -> {
                            BranchDocument bd = new BranchDocument();
                            bd.setId(b.id());
                            bd.setName(b.name());
                            bd.setProducts(
                                    b.products().stream()
                                            .map(p -> {
                                                ProductDocument pd = new ProductDocument();
                                                pd.setId(p.getId());
                                                pd.setName(p.getName());
                                                pd.setStock(p.getStock());
                                                return pd;
                                            })
                                            .collect(Collectors.toList())
                            );
                            return bd;
                        })
                        .collect(Collectors.toList())
        );
        return d;
    }

    public Franchise toDomain(FranchiseDocument d) {
        List<Branch> branches = d.getBranches().stream()
                .map(bd -> new Branch(
                        bd.getId(),
                        bd.getName(),
                        bd.getProducts().stream()
                                .map(pd -> new Product(pd.getId(), pd.getName(), pd.getStock()))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return new Franchise(
                d.getId(),
                d.getName(),
                branches
        );
}
}
