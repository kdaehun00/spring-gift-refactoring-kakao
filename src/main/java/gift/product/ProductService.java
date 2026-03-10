package gift.product;

import gift.category.Category;
import gift.category.CategoryErrorCode;
import gift.category.CategoryException;
import gift.category.CategoryRepository;
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

    @Transactional
    public ProductResponse save(String name, int price, String imageUrl, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND));
        return ProductResponse.from(productRepository.save(new Product(name, price, imageUrl, category)));
    }

    @Transactional
    public ProductResponse update(Long id, String name, int price, String imageUrl, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND));
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));
        product.update(name, price, imageUrl, category);
        return ProductResponse.from(product);
    }

    @Transactional
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
