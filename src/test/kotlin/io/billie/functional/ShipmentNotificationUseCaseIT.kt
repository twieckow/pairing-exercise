package io.billie.functional

import com.fasterxml.jackson.databind.ObjectMapper
import io.billie.orders.adapters.primary.OrdersRestApi
import io.billie.orders.adapters.secondary.JpaOrderRepository
import io.billie.orders.domain.Order
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.util.*

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = DEFINED_PORT)
class ShipmentNotificationUseCaseIT {

    @LocalServerPort
    private val port = 8080

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var orderRepository: JpaOrderRepository

    val merchantId = UUID.randomUUID()
    val orderTotalAmount = BigDecimal.valueOf(123)

    @Test
    fun shouldFailShipmentNotificationWhenOrderNotExist() {
        //given
        val shipmentNotificationJson = //language=JSON
                """{
                      "shipment_amount_notification": 100.1
                    }"""

        //when
        mockMvc.perform(patch("/orders/${UUID.randomUUID()}").contentType(APPLICATION_JSON).content(shipmentNotificationJson))

                //then
                .andExpect(status().isBadRequest)
    }


    @Test
    fun shouldCreateOrderAndNotifyAboutShipment() {
        //given
        val orderCreationRequestJson = //language=JSON
                """{
                      "order_amount": ${orderTotalAmount},
                      "merchant_id": "${merchantId}"
                    }"""
        val firstShipmentNotificationJson = //language=JSON
                """{
                      "shipment_amount_notification": 100.1
                    }"""

        //when
        val creationResult = mockMvc.perform(post("/orders").contentType(APPLICATION_JSON).content(orderCreationRequestJson))
                //then
                .andExpect(status().isOk).andReturn()

        val response = mapper.readValue(creationResult.response.contentAsString, OrdersRestApi.OrderResponse::class.java)

        assertThat(response.orderAmount).isEqualTo(orderTotalAmount)
        assertThat(response.shippedAmount).isEqualTo(BigDecimal.ZERO)
        assertThat(response.orderStatus).isEqualTo(Order.OrderStatus.CREATED.name)
        assertThat(response.version).isEqualTo(1)


        //when
        val firstShipmentNotificationResult = mockMvc.perform(patch("/orders/${response.id}").contentType(APPLICATION_JSON).content(firstShipmentNotificationJson))
                //then
                .andExpect(status().isOk).andReturn()

        val firstShipmentNotificationResponse = mapper.readValue(firstShipmentNotificationResult.response.contentAsString, OrdersRestApi.OrderResponse::class.java)

        assertThat(firstShipmentNotificationResponse.orderAmount).isEqualTo(orderTotalAmount)
        assertThat(firstShipmentNotificationResponse.shippedAmount).isEqualTo(BigDecimal.valueOf(100.1))
        assertThat(firstShipmentNotificationResponse.orderStatus).isEqualTo(Order.OrderStatus.SHIPPED_PARTIALLY.name)
        assertThat(firstShipmentNotificationResponse.version).isEqualTo(2)

    }

    @Test
    fun shouldFailShipmentNotificationWhenOrderAlreadyFullyShipped() {
        //given
        val orderCreationRequestJson = //language=JSON
                """{
                      "order_amount": ${orderTotalAmount},
                      "merchant_id": "${merchantId}"
                    }"""
        val firstShipmentNotificationJson = //language=JSON
                """{
                      "shipment_amount_notification": 100.1
                    }"""
        val secondShipmentNotificationJson = //language=JSON
                """{
                      "shipment_amount_notification": 22.9
                    }"""
        val thirdShipmentNotificationJson = //language=JSON
                """{
                      "shipment_amount_notification": 0.1
                    }"""

        //when
        val creationResult = mockMvc.perform(post("/orders").contentType(APPLICATION_JSON).content(orderCreationRequestJson))
                //then
                .andExpect(status().isOk).andReturn()
        val response = mapper.readValue(creationResult.response.contentAsString, OrdersRestApi.OrderResponse::class.java)
        assertThat(response.orderAmount).isEqualTo(orderTotalAmount)
        assertThat(response.shippedAmount).isEqualTo(BigDecimal.ZERO)
        assertThat(response.orderStatus).isEqualTo(Order.OrderStatus.CREATED.name)
        assertThat(response.version).isEqualTo(1)


        //when
        val firstShipmentResult = mockMvc.perform(patch("/orders/${response.id}").contentType(APPLICATION_JSON).content(firstShipmentNotificationJson))
                //then
                .andExpect(status().isOk).andReturn()
        val firstShipmentResponse = mapper.readValue(firstShipmentResult.response.contentAsString, OrdersRestApi.OrderResponse::class.java)
        assertThat(firstShipmentResponse.orderAmount).isEqualTo(orderTotalAmount)
        assertThat(firstShipmentResponse.shippedAmount).isEqualTo(BigDecimal.valueOf(100.1))
        assertThat(firstShipmentResponse.orderStatus).isEqualTo(Order.OrderStatus.SHIPPED_PARTIALLY.name)
        assertThat(firstShipmentResponse.version).isEqualTo(2)

        //when
        val secondShipmentResult = mockMvc.perform(patch("/orders/${response.id}").contentType(APPLICATION_JSON).content(secondShipmentNotificationJson))
                //then
                .andExpect(status().isOk).andReturn()
        val secondShipmentResponse = mapper.readValue(secondShipmentResult.response.contentAsString, OrdersRestApi.OrderResponse::class.java)
        assertThat(secondShipmentResponse.orderAmount).isEqualTo(orderTotalAmount)
        assertThat(secondShipmentResponse.shippedAmount).isEqualTo(orderTotalAmount)
        assertThat(secondShipmentResponse.orderStatus).isEqualTo(Order.OrderStatus.SHIPPED.name)
        assertThat(secondShipmentResponse.version).isEqualTo(3)

        //when
        mockMvc.perform(patch("/orders/${response.id}").contentType(APPLICATION_JSON).content(thirdShipmentNotificationJson))
                //then
                .andExpect(status().isBadRequest)
        val orderFromDb = orderRepository.find(secondShipmentResponse.id)
        assertThat(orderFromDb).isNotNull
        assertThat(secondShipmentResponse).isEqualTo(OrdersRestApi.OrderResponse.fromDomainModel(orderFromDb!!)) //check order not changed after bad request
    }

    @Test
    fun shouldFailShipmentNotificationWhenShipmentAmountExceedsTotalOrderAmount() {
        //given
        val orderCreationRequestJson = //language=JSON
                """{
                      "order_amount": ${orderTotalAmount},
                      "merchant_id": "${merchantId}"
                    }"""
        val firstShipmentNotificationJson = //language=JSON
                """{
                      "shipment_amount_notification": 125
                    }"""

        //when
        val creationResult = mockMvc.perform(post("/orders").contentType(APPLICATION_JSON).content(orderCreationRequestJson))

                //then
                .andExpect(status().isOk).andReturn()
        val response = mapper.readValue(creationResult.response.contentAsString, OrdersRestApi.OrderResponse::class.java)
        assertThat(response.orderAmount).isEqualTo(orderTotalAmount)
        assertThat(response.shippedAmount).isEqualTo(BigDecimal.ZERO)
        assertThat(response.orderStatus).isEqualTo(Order.OrderStatus.CREATED.name)
        assertThat(response.version).isEqualTo(1)

        //when
        mockMvc.perform(patch("/orders/${response.id}").contentType(APPLICATION_JSON).content(firstShipmentNotificationJson))

                //then
                .andExpect(status().isBadRequest)
        val orderFromDb = orderRepository.find(response.id)
        assertThat(orderFromDb).isNotNull
        assertThat(response).isEqualTo(OrdersRestApi.OrderResponse.fromDomainModel(orderFromDb!!)) //check order not changed after bad request

    }

}
