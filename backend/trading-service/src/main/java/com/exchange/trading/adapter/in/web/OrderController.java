package com.exchange.trading.adapter.in.web;

import com.exchange.common.web.PageResponse;
import com.exchange.trading.adapter.in.web.dto.OrderResponse;
import com.exchange.trading.adapter.in.web.dto.PlaceOrderRequest;
import com.exchange.trading.domain.model.OrderSide;
import com.exchange.trading.domain.model.OrderStatus;
import com.exchange.trading.port.in.CancelOrderCommand;
import com.exchange.trading.port.in.OrderSearchQuery;
import com.exchange.trading.port.in.OrderUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USERNAME_HEADER = "X-Username";

    private final OrderUseCase orderUseCase;

    public OrderController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse place(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestHeader(value = USERNAME_HEADER, required = false) String username,
            @Valid @RequestBody PlaceOrderRequest request
    ) {
        return OrderResponse.from(orderUseCase.placeOrder(request.toCommand(userId, username)));
    }

    @DeleteMapping("/{id}")
    public OrderResponse cancel(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable UUID id
    ) {
        return OrderResponse.from(orderUseCase.cancelOrder(new CancelOrderCommand(userId, id)));
    }

    @GetMapping
    public PageResponse<OrderResponse> list(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(required = false) OrderSide side,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long tradingPairId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<com.exchange.trading.domain.model.Order> result = orderUseCase.searchOrders(
                new OrderSearchQuery(userId, side, status, tradingPairId, page, size)
        );
        return new PageResponse<>(
                result.content().stream().map(OrderResponse::from).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    @GetMapping("/{id}")
    public OrderResponse get(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable UUID id
    ) {
        return OrderResponse.from(orderUseCase.getOrder(userId, id));
    }
}
