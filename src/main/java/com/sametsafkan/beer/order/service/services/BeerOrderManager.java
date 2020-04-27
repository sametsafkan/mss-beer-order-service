package com.sametsafkan.beer.order.service.services;

import com.sametsafkan.beer.order.service.domain.BeerOrder;

public interface BeerOrderManager {

    BeerOrder newBeerOrder(BeerOrder beerOrder);
}
