package gift.wish.service;

import gift.global.error.CommonErrorCode;
import gift.global.error.CommonException;
import gift.product.Product;
import gift.product.ProductRepository;
import gift.wish.Wish;
import gift.wish.WishErrorCode;
import gift.wish.WishException;
import gift.wish.WishRepository;
import gift.wish.api.WishResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class WishService {
    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<WishResponse> findByMemberId(Long memberId, Pageable pageable) {
        return wishRepository.findByMemberId(memberId, pageable).map(WishResponse::from);
    }

    @Transactional
    public WishResponse addWish(Long memberId, Long productId) {
        var existing = wishRepository.findByMemberIdAndProductId(memberId, productId);
        if (existing.isPresent()) {
            return WishResponse.from(existing.get());
        }
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new WishException(WishErrorCode.PRODUCT_NOT_FOUND));
        Wish saved = wishRepository.save(new Wish(memberId, product));
        return WishResponse.from(saved);
    }

    @Transactional
    public void deleteByIdAndMemberId(Long wishId, Long memberId) {
        Wish wish = wishRepository.findById(wishId)
            .orElseThrow(() -> new WishException(WishErrorCode.WISH_NOT_FOUND));
        if (!wish.getMemberId().equals(memberId)) {
            throw new CommonException(CommonErrorCode.FORBIDDEN);
        }
        wishRepository.delete(wish);
    }

    @Transactional(readOnly = true)
    public void findByMemberIdAndProductId(Long memberId, Long optionId) {
        wishRepository.findByMemberIdAndProductId(memberId, optionId)
                .ifPresent(wishRepository::delete);
    }
}
