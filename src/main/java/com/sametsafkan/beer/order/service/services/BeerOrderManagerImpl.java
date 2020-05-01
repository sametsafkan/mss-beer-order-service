package com.sametsafkan.beer.order.service.services;

import com.sametsafkan.beer.order.service.domain.BeerOrder;
import com.sametsafkan.beer.order.service.domain.BeerOrderEventEnum;
import com.sametsafkan.beer.order.service.domain.BeerOrderStatusEnum;
import com.sametsafkan.beer.order.service.repositories.BeerOrderRepository;
import com.sametsafkan.beer.order.service.sm.BeerOrderInterceptor;
import com.sametsafkan.brewery.model.BeerOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BeerOrderManagerImpl implements BeerOrderManager {

    public static final String BEER_ORDER_SM_HEADER = "beer-order-id";

    private final BeerOrderRepository repository;
    private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventEnum> factory;
    private final BeerOrderInterceptor interceptor;

    @Transactional
    @Override
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);
        BeerOrder savedBeerOrder = repository.save(beerOrder);
        sendEvent(beerOrder ,BeerOrderEventEnum.VALIDATE_ORDER);
        return savedBeerOrder;
    }

    @Transactional
    @Override
    public void processValidationResult(UUID beerOrderId, boolean isValid) {
        BeerOrder beerOrder = repository.findOneById(beerOrderId);
        if(isValid){
            sendEvent(beerOrder, BeerOrderEventEnum.VALIDATION_PASSED);
            BeerOrder validatedOrder =  repository.findOneById(beerOrderId);
            sendEvent(validatedOrder, BeerOrderEventEnum.ALLOCATE_ORDER);
        }else{
            sendEvent(beerOrder, BeerOrderEventEnum.VALIDATION_FAILED);
        }

    }

    @Transactional
    @Override
    public void processAllocationResult(BeerOrderDto beerOrderDto, boolean isAllocationError, boolean isPendingInventory){
        BeerOrder beerOrder = repository.findOneById(beerOrderDto.getId());
        if(isAllocationError){
            sendEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_FAILED);
        }else if (isPendingInventory){
            sendEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_NO_INVENTORY);
        }else{
            sendEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_SUCCESS);
        }
        updateAllocatedQty(beerOrderDto);
    }

    @Override
    @Transactional
    public void beerOrderPickedUp(UUID id) {
        Optional<BeerOrder> beerOrderOptional = repository.findById(id);
        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            sendEvent(beerOrder, BeerOrderEventEnum.BEER_ORDER_PICKED_UP);
        }, () -> log.error("Beer Order Not Found for given id : " + id));

    }

    @Override
    @Transactional
    public void cancelOrder(UUID beerOrderId) {
        repository.findById(beerOrderId).ifPresentOrElse( beerOrder -> {
            sendEvent(beerOrder, BeerOrderEventEnum.CANCEL_ORDER);
        }, () -> log.error("Order not found for order id : " + beerOrderId));
    }

    private void updateAllocatedQty(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> allocatedOrderOptional = repository.findById(beerOrderDto.getId());

        allocatedOrderOptional.ifPresentOrElse(allocatedOrder -> {
            allocatedOrder.getBeerOrderLines().forEach(beerOrderLine -> {
                beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
                    if(beerOrderLine.getId() .equals(beerOrderLineDto.getId())){
                        beerOrderLine.setQuantityAllocated(beerOrderLineDto.getQuantityAllocated());
                    }
                });
            });

            repository.saveAndFlush(allocatedOrder);
        }, () -> log.error("Order Not Found. Id: " + beerOrderDto.getId()));
    }

    public void sendEvent(BeerOrder beerOrder, BeerOrderEventEnum event){
        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = build(beerOrder);
        Message msg = MessageBuilder.withPayload(event)
                .setHeader(BEER_ORDER_SM_HEADER, beerOrder.getId().toString())
                .build();
        sm.sendEvent(msg);
    }

    public StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> build(BeerOrder order){
        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = factory.getStateMachine(order.getId());
        sm.stop();
        sm.getStateMachineAccessor()
            .doWithRegion(sma -> {
                sma.resetStateMachine(new DefaultStateMachineContext<>(order.getOrderStatus(), null, null, null));
                sma.addStateMachineInterceptor(interceptor);
            });
        sm.start();
        return sm;
    }
}
