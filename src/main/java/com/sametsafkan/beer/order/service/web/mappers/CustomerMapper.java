package com.sametsafkan.beer.order.service.web.mappers;

import com.sametsafkan.beer.order.service.domain.Customer;
import com.sametsafkan.brewery.model.CustomerDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class, BeerOrderMapper.class})
public interface CustomerMapper {

    Customer customerDtoToCustomer(CustomerDto customerDto);

    CustomerDto customerToCustomerDto(Customer customer);
}
