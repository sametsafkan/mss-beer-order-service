package com.sametsafkan.beer.order.service.sm.action;

import com.sametsafkan.beer.order.service.config.JmsConfig;
import com.sametsafkan.beer.order.service.domain.BeerOrder;
import com.sametsafkan.beer.order.service.domain.BeerOrderEventEnum;
import com.sametsafkan.beer.order.service.domain.BeerOrderStatusEnum;
import com.sametsafkan.beer.order.service.repositories.BeerOrderRepository;
import com.sametsafkan.beer.order.service.services.BeerOrderManagerImpl;
import com.sametsafkan.beer.order.service.web.mappers.BeerOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateOrderRequestAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final JmsTemplate jmsTemplate;
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
        String id = stateContext.getMessageHeader(BeerOrderManagerImpl.BEER_ORDER_SM_HEADER).toString();
        BeerOrder beerOrder = beerOrderRepository.getOne(UUID.fromString(id));
        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_QUEUE, beerOrderMapper.beerOrderToDto(beerOrder));
        log.debug("Sent validation request to queue for order id : " + id);
    }
}
