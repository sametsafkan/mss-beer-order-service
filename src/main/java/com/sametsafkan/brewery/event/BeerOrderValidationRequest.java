package com.sametsafkan.brewery.event;

import com.sametsafkan.brewery.model.BeerOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeerOrderValidationRequest {
    private BeerOrderDto beerOrderDto;
}
