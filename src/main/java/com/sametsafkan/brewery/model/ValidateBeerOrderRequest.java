package com.sametsafkan.brewery.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidateBeerOrderRequest {

    private BeerOrderDto beerOrderDto;
}
