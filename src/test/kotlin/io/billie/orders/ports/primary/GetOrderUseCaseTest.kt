package io.billie.orders.ports.primary

import io.billie.orders.domain.Order
import io.billie.orders.ports.secondary.OrderRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class GetOrderUseCaseTest {

    lateinit var openMocks: AutoCloseable

    @Mock
    lateinit var orderRepository: OrderRepository

    private lateinit var testee: GetOrderUseCase

    @BeforeEach
    fun setUp() {
        openMocks = MockitoAnnotations.openMocks(this)
        testee = GetOrderUseCase(orderRepository)
    }

    @AfterEach
    fun tearDown() {
        openMocks.close()
    }

    @Test
    fun shouldGetEmptyOrderFromRepository() {
        //given
        val id = UUID.randomUUID()
        Mockito.`when`(orderRepository.find(id)).thenReturn(Optional.empty())

        //when
        val order = testee.getOrder(GetOrderUseCase.Input(id))

        //then
        assertThat(order).isNotPresent
    }

    @Test
    fun shouldGetOrderFromRepository() {
        //given
        val orderId = UUID.randomUUID()
        val order = Order(orderId, 2, LocalDateTime.now(), Order.OrderStatus.SHIPPED, BigDecimal.TEN, BigDecimal.TEN, UUID.randomUUID())
        Mockito.`when`(orderRepository.find(orderId)).thenReturn(Optional.of(order))

        //when
        val result = testee.getOrder(GetOrderUseCase.Input(orderId))

        //then
        assertThat(result).isPresent
        assertThat(result.get()).isEqualTo(order)
    }
}