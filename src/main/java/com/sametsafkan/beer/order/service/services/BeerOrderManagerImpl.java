package com.sametsafkan.beer.order.service.services;

import com.sametsafkan.beer.order.service.domain.BeerOrder;
import com.sametsafkan.beer.order.service.domain.BeerOrderEventEnum;
import com.sametsafkan.beer.order.service.domain.BeerOrderStatusEnum;
import com.sametsafkan.beer.order.service.repositories.BeerOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class BeerOrderManagerImpl implements BeerOrderManager {

    private final BeerOrderRepository repository;
    private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventEnum> factory;

    @Transactional
    @Override
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);
        BeerOrder savedBeerOrder = repository.save(beerOrder);
        sendEvent(beerOrder ,BeerOrderEventEnum.VALIDATE_ORDER);
        return savedBeerOrder;
    }

    public void sendEvent(BeerOrder beerOrder, BeerOrderEventEnum event){
        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = build(beerOrder);
        Message msg = MessageBuilder.withPayload(event)
                .build();
        sm.sendEvent(msg);
    }

    public StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> build(BeerOrder order){
        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = factory.getStateMachine(order.getId());
        sm.stop();
        sm.getStateMachineAccessor()
            .doWithRegion(sma -> sma.resetStateMachine(new DefaultStateMachineContext<>(order.getOrderStatus(), null, null, null)));
        sm.start();
        return sm;
    }
}
