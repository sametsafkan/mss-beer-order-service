package com.sametsafkan.beer.order.service.testcomponent;

import com.sametsafkan.beer.order.service.config.JmsConfig;
import com.sametsafkan.brewery.model.AllocateOrderRequest;
import com.sametsafkan.brewery.model.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BeerOrderAllocationOrderListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(AllocateOrderRequest request){
        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESULT_QUEUE,
                AllocateOrderResult.builder().allocationError(false).pendingInventory(false).beerOrderDto(request.getBeerOrderDto()).build());
    }
}
