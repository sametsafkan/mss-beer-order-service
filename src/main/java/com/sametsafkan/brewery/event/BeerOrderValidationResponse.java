package com.sametsafkan.brewery.event;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BeerOrderValidationResponse {
    private UUID beerOrderId;
    private boolean isValid;
}
