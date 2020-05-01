package com.sametsafkan.beer.order.service;

import com.sametsafkan.beer.order.service.domain.Customer;
import com.sametsafkan.beer.order.service.repositories.CustomerRepository;
import com.sametsafkan.beer.order.service.web.mappers.CustomerMapper;
import com.sametsafkan.brewery.model.CustomerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDto> listCustomers() {
        List<Customer> customers = customerRepository.findAll();

        return customers.stream().map(customerMapper::customerToCustomerDto)
                .collect(Collectors.toList());
    }
}
