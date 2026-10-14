# payment-system
integrate vnpay to demo a flow of purchasing books order, and also a batch job to migrate all of the bill's picture from completed order to s3 storage

```
ngrok http --url=nonlinear-christel-superimportantly.ngrok-free.dev 5173
```


```java
package com.paymentsystem.ticketservice.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCheckOutCommand {

    @TargetAggregateIdentifier
    private String id;

    private String customerId;
    private String movieId;
    private String idempotencyKey;
    private Double amount;
    private List<String> seats;
    private String paymentMethod;
    private String clientIp;
}

```

```java
package com.paymentsystem.ticketservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckOutCreatedEvent {

    private String id;

    private String customerId;
    private String movieId;
    private String idempotencyKey;
    private Double amount;
    private List<String> seats;
    private String paymentMethod;
    private String clientIp;
}

```

```java
package com.paymentsystem.ticketservice.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Aggregate
public class CheckoutAggregate {

}

```

```java
package com.paymentsystem.ticketservice.saga;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
@Slf4j
public class TicketBookingSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle() {

    }


}

```