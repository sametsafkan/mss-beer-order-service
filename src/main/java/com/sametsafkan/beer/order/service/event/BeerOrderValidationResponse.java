package com.sametsafkan.beer.order.service.event;

import lombok.Data;

import java.util.UUID;

@Data
public class BeerOrderValidationResponse {
    private UUID beerOrderId;
    private boolean isValid;
}
