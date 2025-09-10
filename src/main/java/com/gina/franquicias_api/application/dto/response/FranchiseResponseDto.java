package com.gina.franquicias_api.application.dto.response;

import java.util.List;

public class FranchiseResponseDto {
    private String id;
    private String name;
    private List<BranchResponseDto> branches;

    public FranchiseResponseDto() {}

    public FranchiseResponseDto(String id, String name, List<BranchResponseDto> branches) {
        this.id = id;
        this.name = name;
        this.branches = branches;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<BranchResponseDto> getBranches() { return branches; }
}
