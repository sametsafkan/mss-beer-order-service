package com.sametsafkan.beer.order.service.sm;

import com.sametsafkan.beer.order.service.domain.BeerOrderEventEnum;
import com.sametsafkan.beer.order.service.domain.BeerOrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static com.sametsafkan.beer.order.service.domain.BeerOrderEventEnum.*;
import static com.sametsafkan.beer.order.service.domain.BeerOrderStatusEnum.*;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class BeerOrderStateMachineConfig extends
        StateMachineConfigurerAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {
    @Override
    public void configure(StateMachineStateConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> states) throws Exception {
        states.withStates()
                .initial(NEW)
                .states(EnumSet.allOf(BeerOrderStatusEnum.class))
                .end(PICKED_UP)
                .end(DELIVERED)
                .end(DELIVERY_EXCEPTION)
                .end(VALIDATION_EXCEPTION)
                .end(ALLOCATION_EXCEPTION);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> transitions) throws Exception {
        transitions.withExternal()
                .source(NEW).target(VALIDATION_PENDING).event(VALIDATE_ORDER)
                .and()
                .withExternal()
                .source(NEW).target(VALIDATED).event(VALIDATION_PASSED)
                .and()
                .withExternal()
                .source(NEW).target(VALIDATION_EXCEPTION).event(VALIDATION_FAILED);
    }
}
