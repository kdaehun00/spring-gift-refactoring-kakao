package gift.option.service;

import gift.global.error.CommonErrorCode;
import gift.global.error.CommonException;
import gift.option.Option;
import gift.option.OptionErrorCode;
import gift.option.OptionException;
import gift.option.OptionRepository;
import gift.option.api.OptionRequest;
import gift.option.api.OptionResponse;
import gift.order.OrderErrorCode;
import gift.order.OrderException;
import gift.product.Product;
import gift.product.ProductErrorCode;
import gift.product.ProductException;
import gift.product.ProductRepository;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OptionService {
    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<OptionResponse> findByProductId(Long productId) {
        productRepository.findById(productId)
            .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));
        return optionRepository.findByProductId(productId).stream()
            .map(OptionResponse::from)
            .toList();
    }

    @Transactional
    public Option getOptionForUpdate(Long optionId) {
        return optionRepository.findByIdForUpdate(optionId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.OPTION_NOT_FOUND));
    }

    @Transactional
    public OptionResponse createOption(Long productId, OptionRequest request) {
        validateName(request.name());
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        if (optionRepository.existsByProductIdAndName(productId, request.name())) {
            throw new OptionException(OptionErrorCode.DUPLICATE_OPTION_NAME);
        }

        return OptionResponse.from(optionRepository.save(new Option(product, request.name(), request.quantity())));
    }

    private void validateName(String name) {
        List<String> errors = OptionNameValidator.validate(name);
        if (!errors.isEmpty()) {
            throw new CommonException(CommonErrorCode.INVALID_REQUEST);
        }
    }

    @Transactional
    public void deleteOption(Long productId, Long optionId) {
        productRepository.findById(productId)
            .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        List<Option> options = optionRepository.findByProductId(productId);
        if (options.size() <= 1) {
            throw new OptionException(OptionErrorCode.CANNOT_DELETE_LAST_OPTION);
        }

        Option option = optionRepository.findById(optionId).orElse(null);
        if (option == null || !option.getProduct().getId().equals(productId)) {
            throw new OptionException(OptionErrorCode.OPTION_NOT_FOUND);
        }

        optionRepository.delete(option);
    }
}
