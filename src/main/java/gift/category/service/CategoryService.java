package gift.category.service;

import gift.category.Category;
import gift.category.CategoryErrorCode;
import gift.category.CategoryException;
import gift.category.CategoryRepository;
import gift.category.api.CategoryRequest;
import gift.category.api.CategoryResponse;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllResponses() {
        return categoryRepository.findAll().stream()
            .map(CategoryResponse::from)
            .toList();
    }

    @Transactional
    public CategoryResponse save(CategoryRequest request) {
        Category category = Category.create(request.name(), request.color(), request.imageUrl(), request.description());
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findById(id);
        category.update(request.name(), request.color(), request.imageUrl(), request.description());
        return CategoryResponse.from(category);
    }

    @Transactional
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
