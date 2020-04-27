package com.sametsafkan.beer.order.service.sm;

import com.sametsafkan.beer.order.service.domain.BeerOrder;
import com.sametsafkan.beer.order.service.domain.BeerOrderEventEnum;
import com.sametsafkan.beer.order.service.domain.BeerOrderStatusEnum;
import com.sametsafkan.beer.order.service.repositories.BeerOrderRepository;
import com.sametsafkan.beer.order.service.services.BeerOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class BeerOrderInterceptor extends StateMachineInterceptorAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final BeerOrderRepository beerOrderRepository;

    @Override
    public void preStateChange(State<BeerOrderStatusEnum, BeerOrderEventEnum> state, Message<BeerOrderEventEnum> message,
                               Transition<BeerOrderStatusEnum, BeerOrderEventEnum> transition,
                               StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachine) {
        Optional.ofNullable(message).ifPresent(m -> {
            Optional.ofNullable(m.getHeaders().get(BeerOrderManagerImpl.BEER_ORDER_SM_HEADER)).ifPresent(id -> {
                log.debug("Saving beer order state for order id : " + id + " status : " + state.getId());
                BeerOrder beer = beerOrderRepository.getOne(UUID.fromString((String) id));
                beer.setOrderStatus(state.getId());
                beerOrderRepository.saveAndFlush(beer);
            });
        });

    }
}
