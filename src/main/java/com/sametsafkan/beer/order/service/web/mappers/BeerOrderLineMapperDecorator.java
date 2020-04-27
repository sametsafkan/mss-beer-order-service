package com.sametsafkan.beer.order.service.web.mappers;

import com.sametsafkan.beer.order.service.domain.BeerOrderLine;
import com.sametsafkan.beer.order.service.services.beer.BeerService;
import com.sametsafkan.beer.order.service.services.beer.model.BeerDto;
import com.sametsafkan.brewery.model.BeerOrderLineDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public abstract class BeerOrderLineMapperDecorator implements BeerOrderLineMapper{

    private BeerOrderLineMapper beerOrderLineMapper;
    private BeerService beerService;

    @Autowired
    public void setBeerOrderLineMapper(BeerOrderLineMapper beerOrderLineMapper) {
        this.beerOrderLineMapper = beerOrderLineMapper;
    }

    @Autowired
    public void setBeerService(BeerService beerService) {
        this.beerService = beerService;
    }

    @Override
    public BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line) {
        Optional<BeerDto> beer = beerService.getBeerByUpc(line.getUpc());
        BeerOrderLineDto beerOrderLine = beerOrderLineMapper.beerOrderLineToDto(line);
        beer.ifPresent(beerDto -> beerOrderLine.setBeerName(beerDto.getName()));
        return beerOrderLine;
    }

    @Override
    public BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto) {
        return beerOrderLineMapper.dtoToBeerOrderLine(dto);
    }
}
