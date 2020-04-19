package com.sametsafkan.beer.order.service.services.beer;

import com.sametsafkan.beer.order.service.services.beer.model.BeerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Service
@ConfigurationProperties(prefix = "com.sametsafkan.beerservice", ignoreUnknownFields = false)
@Slf4j
public class BeerServiceImpl implements BeerService {

    private String beerServiceHost;
    private String beerServicePath;

    private final RestTemplate restTemplate;

    public BeerServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public void setBeerServiceHost(String beerServiceHost) {
        this.beerServiceHost = beerServiceHost;
    }

    public void setBeerServicePath(String beerServicePath) {
        this.beerServicePath = beerServicePath;
    }

    @Override
    public Optional<BeerDto> getBeerByUpc(String upc){
        log.info("calling beerservice...");
        return Optional.of(restTemplate.getForObject(beerServiceHost + beerServicePath + upc, BeerDto.class));
    }
}
