package com.sametsafkan.beer.order.service.testcomponent;

import com.sametsafkan.beer.order.service.config.JmsConfig;
import com.sametsafkan.beer.order.service.domain.BeerOrder;
import com.sametsafkan.brewery.event.BeerOrderValidationRequest;
import com.sametsafkan.brewery.event.BeerOrderValidationResponse;
import com.sametsafkan.beer.order.service.repositories.BeerOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BeerOrderValidationListener {

    private final JmsTemplate jmsTemplate;
    private final BeerOrderRepository repository;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void listen(BeerOrderValidationRequest request){
        boolean isValid = true;
        boolean sendResponse = true;
        BeerOrder beerOrder = repository.findById(request.getBeerOrderDto().getId()).get();
        if(beerOrder.getCustomerRef().equals("fail-validation")){
            isValid = false;
        }else if (beerOrder.getCustomerRef().equals("dont-validate")){
            sendResponse = false;
        }
        if (sendResponse) {
            jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESULT_QUEUE,
                    BeerOrderValidationResponse.builder().beerOrderId(request.getBeerOrderDto().getId()).isValid(isValid).build());
        }
    }
}
