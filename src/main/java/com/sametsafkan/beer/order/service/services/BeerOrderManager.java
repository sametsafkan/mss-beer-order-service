package com.sametsafkan.beer.order.service.services;

import com.sametsafkan.beer.order.service.domain.BeerOrder;
import com.sametsafkan.brewery.model.BeerOrderDto;

import java.util.UUID;

public interface BeerOrderManager {

    BeerOrder newBeerOrder(BeerOrder beerOrder);

    void processValidationResult(UUID beerOrderId, boolean isValid);

    void processAllocationResult(BeerOrderDto beerOrderDto, boolean isAllocationError, boolean isPendingInventory);

    void beerOrderPickedUp(UUID id);

    void cancelOrder(UUID uuid);
}
