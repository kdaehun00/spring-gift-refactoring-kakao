package gift.product.service;

import gift.category.Category;
import gift.category.CategoryErrorCode;
import gift.category.CategoryException;
import gift.category.CategoryRepository;
import gift.global.common.NameValidator;
import gift.global.error.CommonErrorCode;
import gift.global.error.CommonException;
import gift.product.Product;
import gift.product.ProductErrorCode;
import gift.product.ProductException;
import gift.product.ProductRepository;
import gift.product.api.ProductRequest;
import gift.product.api.ProductResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public ProductResponse findByIdResponse(Long id) {
        return ProductResponse.from(findById(id));
    }

    private static final int PRODUCT_NAME_MAX_LENGTH = 15;

    public void validateProductName(String name) {
        List<String> errors = NameValidator.validate(name, PRODUCT_NAME_MAX_LENGTH);
        if (!errors.isEmpty()) {
            throw new CommonException(CommonErrorCode.INVALID_REQUEST);
        }
    }

    public List<String> validateProductName(String name, boolean allowKakao) {
        return NameValidator.validate(name, PRODUCT_NAME_MAX_LENGTH, allowKakao);
    }

    @Transactional
    public ProductResponse save(ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND));
        return ProductResponse.from(productRepository.save(
            new Product(request.name(), request.price(), request.imageUrl(), category)));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND));
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));
        product.update(request.name(), request.price(), request.imageUrl(), category);
        return ProductResponse.from(product);
    }

    @Transactional
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
