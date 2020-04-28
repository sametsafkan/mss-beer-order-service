package com.sametsafkan.beer.order.service.listener;

import com.sametsafkan.beer.order.service.config.JmsConfig;
import com.sametsafkan.beer.order.service.event.BeerOrderValidationResponse;
import com.sametsafkan.beer.order.service.services.BeerOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateBeerOrderListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESULT_QUEUE)
    public void listen(BeerOrderValidationResponse response){
        boolean isValid = response.isValid();
        UUID beerOrderId = response.getBeerOrderId();
        beerOrderManager.processValidationResult(beerOrderId, isValid);
    }
}
