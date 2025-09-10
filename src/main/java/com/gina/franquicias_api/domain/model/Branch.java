package com.gina.franquicias_api.domain.model;

import java.util.List;

public record Branch(String id, String name, List<Product> products) {


}
