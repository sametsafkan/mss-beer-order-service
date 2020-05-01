package com.sametsafkan.beer.order.service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.sametsafkan.beer.order.service.domain.BeerOrder;
import com.sametsafkan.beer.order.service.domain.BeerOrderLine;
import com.sametsafkan.beer.order.service.domain.BeerOrderStatusEnum;
import com.sametsafkan.beer.order.service.domain.Customer;
import com.sametsafkan.beer.order.service.repositories.BeerOrderRepository;
import com.sametsafkan.beer.order.service.repositories.CustomerRepository;
import com.sametsafkan.beer.order.service.services.beer.model.BeerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(WireMockExtension.class)
class BeerOrderManagerImplIT {

    @Autowired
    BeerOrderManager beerOrderManager;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    BeerOrderRepository beerOrderRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WireMockServer wireMockServer;
    Customer testCustomer;
    UUID beerId = UUID.randomUUID();
    private String beerServicePath = "/api/v1/beer/findByUpc/";

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.save(Customer.builder().customerName("Samet").build());
    }

    @TestConfiguration
    static class WireMockProvider{

        @Bean(destroyMethod = "stop")
        public WireMockServer getWireMockServer(){
            WireMockServer server = new WireMockServer((wireMockConfig().port(8083)));
            server.start();
            return server;
        }
    }

    @Test
    void testNewToAllocated() throws JsonProcessingException, InterruptedException {
        BeerOrder beerOrder = createBeerOrder();
        beerOrder.setCustomerRef("new-to-allocated");
        BeerDto beer = BeerDto.builder().id(beerId).upc("12345").build();
        wireMockServer.stubFor(get(beerServicePath + "12345")
                                .willReturn(okJson(objectMapper.writeValueAsString(beer))));
        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
        await().untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
        });
        await().untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            BeerOrderLine line = foundOrder.getBeerOrderLines().iterator().next();
            assertEquals(line.getOrderQuantity(), line.getQuantityAllocated());
        });
        BeerOrder newBeerOrder = beerOrderRepository.findById(beerOrder.getId()).get();
        assertNotNull(savedBeerOrder);
        assertEquals(BeerOrderStatusEnum.ALLOCATED, newBeerOrder.getOrderStatus());
    }

    @Test
    void testNewToPickedUp() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();

        wireMockServer.stubFor(get(beerServicePath + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();
        beerOrder.setCustomerRef("picked-up");
        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        await().untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
        });

        beerOrderManager.beerOrderPickedUp(savedBeerOrder.getId());

        await().untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.PICKED_UP, foundOrder.getOrderStatus());
        });

        BeerOrder pickedUpOrder = beerOrderRepository.findById(savedBeerOrder.getId()).get();

        assertEquals(BeerOrderStatusEnum.PICKED_UP, pickedUpOrder.getOrderStatus());
    }

    @Test
    void testFailedValidation() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();

        wireMockServer.stubFor(get(beerServicePath + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();
        beerOrder.setCustomerRef("fail-validation");
        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        await().untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.VALIDATION_EXCEPTION, foundOrder.getOrderStatus());
        });

    }

    public BeerOrder createBeerOrder(){
        BeerOrder beerOrder = BeerOrder.builder().customer(testCustomer).build();
        Set<BeerOrderLine> beerOrderLines = new HashSet<>();
        beerOrderLines.add(BeerOrderLine.builder().beerId(beerId).upc("12345").orderQuantity(1).beerOrder(beerOrder).build());

        beerOrder.setBeerOrderLines(beerOrderLines);
        return beerOrder;
    }
}