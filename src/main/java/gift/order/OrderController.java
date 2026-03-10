package gift.order;

import gift.global.auth.LoginMember;
import gift.global.common.ApiResponse;
import gift.member.Member;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrders(
        @LoginMember Member member, Pageable pageable
    ) {
        var orders = orderService.findByMemberId(member.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
        @LoginMember Member member,
        @Valid @RequestBody OrderRequest request
    ) {
        OrderResponse response = orderService.createOrder(member.getId(), request);
        return ResponseEntity.created(URI.create("/api/v1/orders/" + response.id()))
            .body(ApiResponse.created(response));
    }
}
