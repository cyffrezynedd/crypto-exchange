package com.exchange.trading.adapter.in.web;

import com.exchange.trading.adapter.in.web.dto.OrderResponse;
import com.exchange.trading.adapter.in.web.dto.PlaceOrderRequest;
import com.exchange.trading.port.in.CancelOrderCommand;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    public static final String USER_ID_HEADER = "X-User-Id";

    private final OrderUseCase orderUseCase;

    public OrderController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse place(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @Valid @RequestBody PlaceOrderRequest request
    ) {
        return OrderResponse.from(orderUseCase.placeOrder(request.toCommand(userId)));
    }

    @DeleteMapping("/{id}")
    public OrderResponse cancel(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable UUID id
    ) {
        return OrderResponse.from(orderUseCase.cancelOrder(new CancelOrderCommand(userId, id)));
    }

    @GetMapping
    public List<OrderResponse> list(@RequestHeader(USER_ID_HEADER) Long userId) {
        return orderUseCase.listOrders(userId).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public OrderResponse get(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable UUID id
    ) {
        return OrderResponse.from(orderUseCase.getOrder(userId, id));
    }
}
