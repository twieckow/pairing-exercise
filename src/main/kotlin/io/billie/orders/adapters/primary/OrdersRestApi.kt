package io.billie.orders.adapters.primary

import com.fasterxml.jackson.annotation.JsonProperty
import io.billie.orders.domain.OrderValidationException
import io.billie.orders.domain.Order
import io.billie.orders.ports.primary.CreateOrderUseCase
import io.billie.orders.ports.primary.GetOrderUseCase
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
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
class OrdersRestApi(val createOrderUseCase: CreateOrderUseCase, val getOrderUseCase: GetOrderUseCase) {

    data class CreateOrderRequest(
            @field:NotNull @JsonProperty("order_amount") val orderAmount: BigDecimal,
            @field:NotBlank @JsonProperty("merchant_id") val merchantId: String,
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


    @GetMapping("/{id}")
    fun findOrder(@PathVariable id: String): Optional<OrderResponse> =
            getOrderUseCase.getOrder(GetOrderUseCase.Input(UUID.fromString(id)))
                    .map(OrderResponse.Companion::fromDomainModel)


    @PostMapping
    @ApiResponses(
            value = [
                ApiResponse(
                        responseCode = "200",
                        description = "Accepted the new order",
                        content = [
                            (Content(
                                    mediaType = "application/json",
                                    array = (ArraySchema(schema = Schema(implementation = OrderResponse::class)))
                            ))]
                ),
                ApiResponse(responseCode = "400", description = "Bad request", content = [Content()])]
    )
    fun createOrder(@Valid @RequestBody request: CreateOrderRequest): OrderResponse {
        try {
            val order = createOrderUseCase.createNewOrder(CreateOrderUseCase.Input(request.orderAmount, UUID.fromString(request.merchantId)))
            return OrderResponse.fromDomainModel(order)
        } catch (e: OrderValidationException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        }
    }

}
