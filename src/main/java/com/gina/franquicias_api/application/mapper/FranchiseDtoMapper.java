package com.gina.franquicias_api.application.mapper;

import com.gina.franquicias_api.application.dto.BranchResponseDto;
import com.gina.franquicias_api.application.dto.FranchiseRequestDto;
import com.gina.franquicias_api.application.dto.FranchiseResponseDto;
import com.gina.franquicias_api.application.dto.ProductResponseDto;
import com.gina.franquicias_api.domain.model.Branch;
import com.gina.franquicias_api.domain.model.Franchise;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FranchiseDtoMapper {
    public Franchise toDomain(FranchiseRequestDto dto) {
        return new Franchise(null, dto.getName(), List.of());
    }

    public FranchiseResponseDto toResponse(Franchise franchise) {
        List<BranchResponseDto> branchDtos = franchise.getBranches().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new FranchiseResponseDto(franchise.getId(), franchise.getName(), branchDtos);
    }

    public BranchResponseDto toResponse(Branch branch) {
        List<ProductResponseDto> products = branch.getProducts().stream()
                .map(p -> new ProductResponseDto(p.getId(), p.getName(), p.getStock()))
                .collect(Collectors.toList());

        return new BranchResponseDto(branch.getId(), branch.getName(), products);
    }
}
