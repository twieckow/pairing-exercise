package io.billie.orders.adapters.primary

import com.fasterxml.jackson.annotation.JsonProperty
import io.billie.orders.domain.Order
import io.billie.orders.domain.OrderShipmentException
import io.billie.orders.domain.OrderValidationException
import io.billie.orders.ports.primary.CreateOrderUseCase
import io.billie.orders.ports.primary.GetOrderUseCase
import io.billie.orders.ports.primary.ShipmentNotificationUseCase
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@RestController
@RequestMapping("orders")
class OrdersRestApi(
        val createOrderUseCase: CreateOrderUseCase,
        val getOrderUseCase: GetOrderUseCase,
        val shipmentNotificationUseCase: ShipmentNotificationUseCase,
) {

    data class CreateOrderRequest(
            @field:NotNull @JsonProperty("order_amount") val orderAmount: BigDecimal,
            @field:NotBlank @JsonProperty("merchant_id") val merchantId: String,
    )

    data class ShipmentNotificationRequest(
            @field:NotNull @JsonProperty("shipment_amount_notification") val shipmentAmountNotification: BigDecimal,
    )

    data class OrderResponse(
            val id: UUID,
            val version: Long,
            @JsonProperty("created_datetime") val createdDatetime: LocalDateTime,
            @JsonProperty("order_status") val orderStatus: String,
            @JsonProperty("order_amount") val orderAmount: BigDecimal,
            @JsonProperty("shipped_amount") val shippedAmount: BigDecimal,
            @JsonProperty("merchant_id") val merchantId: UUID,
    ) {
        companion object {
            fun fromDomainModel(order: Order): OrderResponse {
                return OrderResponse(
                        id = order.id,
                        version = order.version,
                        createdDatetime = order.createdDatetime,
                        orderStatus = order.orderStatus.name,
                        orderAmount = order.orderAmount.stripTrailingZeros(),
                        shippedAmount = order.shippedAmount.stripTrailingZeros(),
                        merchantId = order.merchantId)
            }
        }

    }


    @GetMapping("/{orderId}")
    fun findOrder(@PathVariable orderId: String): OrderResponse? =
            getOrderUseCase.getOrder(GetOrderUseCase.Input(UUID.fromString(orderId)))?.let { OrderResponse.fromDomainModel(it) }


    @PostMapping
    fun createOrder(@Valid @RequestBody request: CreateOrderRequest): OrderResponse {
        try {
            val order = createOrderUseCase.createNewOrder(CreateOrderUseCase.Input(request.orderAmount, UUID.fromString(request.merchantId)))
            return OrderResponse.fromDomainModel(order)
        } catch (e: OrderValidationException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        }
    }

    @PatchMapping("/{orderId}")
    fun notifyShipmentOfOrder(@PathVariable orderId: String, @Valid @RequestBody request: ShipmentNotificationRequest): OrderResponse {
        try {
            val updatedOrder = shipmentNotificationUseCase.notifyShipment(ShipmentNotificationUseCase.Input(UUID.fromString(orderId), request.shipmentAmountNotification))
            return OrderResponse.fromDomainModel(updatedOrder)
        } catch (e: OrderShipmentException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        }
    }


}
