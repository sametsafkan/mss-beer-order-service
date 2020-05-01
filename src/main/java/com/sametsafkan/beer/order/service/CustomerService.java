package com.sametsafkan.beer.order.service;

import com.sametsafkan.brewery.model.CustomerDto;

import java.util.List;

public interface CustomerService {

    List<CustomerDto> listCustomers();
}
