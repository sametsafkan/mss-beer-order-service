package com.sametsafkan.beer.order.service.web.mappers;

import com.sametsafkan.beer.order.service.domain.BeerOrderLine;
import com.sametsafkan.beer.order.service.web.model.BeerOrderLineDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface BeerOrderLineMapper {
    BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line);

    BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto);
}
