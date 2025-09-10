package com.gina.franquicias_api.application.mapper;

import com.gina.franquicias_api.application.dto.response.BranchResponseDto;
import com.gina.franquicias_api.application.dto.response.FranchiseResponseDto;
import com.gina.franquicias_api.application.dto.response.ProductResponseDto;
import com.gina.franquicias_api.application.dto.response.ProductWithBranchResponseDto;
import com.gina.franquicias_api.domain.model.Branch;
import com.gina.franquicias_api.domain.model.Franchise;
import com.gina.franquicias_api.domain.model.Product;
import com.gina.franquicias_api.domain.model.ProductWithBranch;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FranchiseDtoMapper {


    public FranchiseResponseDto toResponse(Franchise franchise) {
        List<BranchResponseDto> branchDtos = franchise.getBranches().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new FranchiseResponseDto(franchise.getId(), franchise.getName(), branchDtos);
    }

    public BranchResponseDto toResponse(Branch branch) {
        List<ProductResponseDto> products = branch.products().stream()
                .map(p -> new ProductResponseDto(p.getId(), p.getName(), p.getStock()))
                .collect(Collectors.toList());

        return new BranchResponseDto(branch.id(), branch.name(), products);
    }

    public ProductResponseDto toResponse(Product product) {
        return new ProductResponseDto(product.getId(), product.getName(), product.getStock());
    }

    public ProductWithBranchResponseDto toResponse(ProductWithBranch pwb) {
        ProductResponseDto productDto = null;
        if (pwb.getProduct() != null) {
            productDto = new ProductResponseDto(
                    pwb.getProduct().getId(),
                    pwb.getProduct().getName(),
                    pwb.getProduct().getStock()
            );
        }
        return new ProductWithBranchResponseDto(pwb.getBranchName(), productDto);
    }

}
