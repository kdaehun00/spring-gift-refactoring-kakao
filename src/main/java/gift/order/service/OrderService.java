package gift.order.service;

import gift.global.error.CommonErrorCode;
import gift.global.error.CommonException;
import gift.member.Member;
import gift.member.MemberRepository;
import gift.member.MemberService;
import gift.order.Order;
import gift.order.OrderRepository;
import gift.order.api.OrderRequest;
import gift.order.api.OrderResponse;
import gift.option.Option;
import gift.option.OptionRepository;
import gift.option.service.OptionService;
import gift.product.Product;
import gift.wish.WishRepository;
import gift.wish.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final OptionService optionService;
    private final WishService wishService;
    private final MessageClient messageClient;

    @Transactional(readOnly = true)
    public Page<OrderResponse> findByMemberId(Long memberId, Pageable pageable) {
        return orderRepository.findByMemberId(memberId, pageable)
            .map(OrderResponse::from);
    }

    @Transactional
    public OrderResponse createOrder(Long memberId, OrderRequest request) {
        Member member = memberService.findById(memberId);
        Option option = optionService.getOptionForUpdate(request.optionId());

        option.subtractQuantity(request.quantity());

        Order order = Order.create(option, memberId, request.quantity(), request.message());
        member.deductPoint(order.getTotalPrice());

        Order saved = orderRepository.save(order);

        wishService.findByMemberIdAndProductId(memberId, request.optionId());

        sendMessageIfPossible(member, saved, option);
        return OrderResponse.from(saved);
    }

    private void sendMessageIfPossible(Member member, Order order, Option option) {
        if (member.getKakaoAccessToken() == null) {
            return;
        }
        try {
            Product product = option.getProduct();
            messageClient.sendToMe(member.getKakaoAccessToken(), order, product);
        } catch (Exception ignored) {
            // best-effort: 메시지 전송 실패는 무시
        }
    }
}
