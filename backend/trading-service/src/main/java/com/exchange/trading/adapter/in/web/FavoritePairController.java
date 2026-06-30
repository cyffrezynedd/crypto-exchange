package com.exchange.trading.adapter.in.web;

import com.exchange.common.web.PageResponse;
import com.exchange.trading.adapter.in.web.dto.AddFavoritePairRequest;
import com.exchange.trading.adapter.in.web.dto.FavoritePairResponse;
import com.exchange.trading.port.in.FavoritePairSearchQuery;
import com.exchange.trading.port.in.FavoritePairUseCase;
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

@RestController
@RequestMapping("/favorite-pairs")
public class FavoritePairController {

    public static final String USER_ID_HEADER = "X-User-Id";

    private final FavoritePairUseCase favoritePairUseCase;

    public FavoritePairController(FavoritePairUseCase favoritePairUseCase) {
        this.favoritePairUseCase = favoritePairUseCase;
    }

    @GetMapping
    public PageResponse<FavoritePairResponse> list(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(required = false) Long tradingPairId,
            @RequestParam(required = false) String symbol,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return map(favoritePairUseCase.search(new FavoritePairSearchQuery(userId, tradingPairId, symbol, page, size)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FavoritePairResponse add(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @Valid @RequestBody AddFavoritePairRequest request
    ) {
        return FavoritePairResponse.from(favoritePairUseCase.add(userId, request.tradingPairId()));
    }

    @DeleteMapping("/{tradingPairId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long tradingPairId
    ) {
        favoritePairUseCase.remove(userId, tradingPairId);
    }

    private static PageResponse<FavoritePairResponse> map(PageResponse<com.exchange.trading.domain.model.FavoritePair> page) {
        return new PageResponse<>(
                page.content().stream().map(FavoritePairResponse::from).toList(),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages()
        );
    }
}
