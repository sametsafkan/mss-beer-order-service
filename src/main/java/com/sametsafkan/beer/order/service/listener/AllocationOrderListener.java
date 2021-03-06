package com.sametsafkan.beer.order.service.listener;

import com.sametsafkan.beer.order.service.config.JmsConfig;
import com.sametsafkan.beer.order.service.services.BeerOrderManager;
import com.sametsafkan.brewery.event.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocationOrderListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESULT_QUEUE)
    public void listen(AllocateOrderResult result){
        beerOrderManager.processAllocationResult(result.getBeerOrderDto(),
                result.getAllocationError(), result.getPendingInventory());
    }
}
