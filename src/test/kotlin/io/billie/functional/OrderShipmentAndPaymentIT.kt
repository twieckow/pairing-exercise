package io.billie.functional

import io.billie.functional.tools.AllEventsCollectingListener
import io.billie.orders.domain.event.OrderCreatedEvent
import io.billie.orders.domain.event.OrderShippedEvent
import io.billie.orders.ports.primary.CreateOrderUseCase
import io.billie.orders.ports.primary.ShipmentNotificationUseCase
import io.billie.payments.domain.events.PaidToMerchantEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*


@SpringBootTest
@Import(TestComponents::class)
@ActiveProfiles("test")
class OrderShipmentAndPaymentIT {

    @Autowired
    private lateinit var createOrderUseCase: CreateOrderUseCase

    @Autowired
    private lateinit var shipmentNotificationUseCase: ShipmentNotificationUseCase

    @Autowired
    private lateinit var testListener: AllEventsCollectingListener

    @BeforeEach
    fun setUp() {
        testListener.clearEvents()
    }


    @Test
    fun shouldMakePaymentToMerchantWhenOrderShipped() {
        // given
        val createOrderInput: CreateOrderUseCase.Input = CreateOrderUseCase.Input(
                BigDecimal.TEN,
                UUID.randomUUID(),
        )

        // when
        val order = createOrderUseCase.execute(createOrderInput)
        shipmentNotificationUseCase.execute(ShipmentNotificationUseCase.Input(order.id, BigDecimal.TEN))

        // then
        val domainEvents = testListener.allEvents()
        assertThat(domainEvents).anyMatch { event -> event is OrderCreatedEvent }
        assertThat(domainEvents).anyMatch { event -> event is OrderShippedEvent }
        assertThat(domainEvents).anyMatch { event -> event is PaidToMerchantEvent }
    }

}
