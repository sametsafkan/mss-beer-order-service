package com.sametsafkan.beer.order.service.services.beer;

import com.sametsafkan.beer.order.service.services.beer.model.BeerDto;

import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    Optional<BeerDto> getBeerByUpc(String upc);
}
