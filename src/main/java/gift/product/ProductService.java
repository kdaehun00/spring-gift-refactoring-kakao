package gift.product;

import gift.category.Category;
import gift.category.CategoryRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다. id=" + id));
    }

    @Transactional(readOnly = true)
    public Product findByIdOrNull(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Transactional
    public Product save(String name, int price, String imageUrl, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(
                () -> new NoSuchElementException("카테고리가 존재하지 않습니다. id=" + categoryId));
        return productRepository.save(new Product(name, price, imageUrl, category));
    }

    @Transactional
    public Product update(Long id, String name, int price, String imageUrl, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(
                () -> new NoSuchElementException("카테고리가 존재하지 않습니다. id=" + categoryId));
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다. id=" + id));
        product.update(name, price, imageUrl, category);
        return productRepository.save(product);
    }

    @Transactional
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
