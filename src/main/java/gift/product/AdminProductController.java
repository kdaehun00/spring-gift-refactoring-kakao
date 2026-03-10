package gift.product;

import gift.category.CategoryService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {
    private static final String VIEW_LIST = "product/list";
    private static final String VIEW_NEW = "product/new";
    private static final String VIEW_EDIT = "product/edit";
    private static final String REDIRECT_LIST = "redirect:/admin/products";

    private static final String ATTR_PRODUCTS = "products";
    private static final String ATTR_PRODUCT = "product";
    private static final String ATTR_CATEGORIES = "categories";
    private static final String ATTR_ERRORS = "errors";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_PRICE = "price";
    private static final String ATTR_IMAGE_URL = "imageUrl";
    private static final String ATTR_CATEGORY_ID = "categoryId";

    private final ProductService productService;
    private final CategoryService categoryService;

    public AdminProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute(ATTR_PRODUCTS, productService.findAll());
        return VIEW_LIST;
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute(ATTR_CATEGORIES, categoryService.findAll());
        return VIEW_NEW;
    }

    @PostMapping
    public String create(
        @RequestParam String name,
        @RequestParam int price,
        @RequestParam String imageUrl,
        @RequestParam Long categoryId,
        Model model
    ) {
        List<String> errors = productService.validateProductName(name, true);
        if (!errors.isEmpty()) {
            populateNewForm(model, errors, name, price, imageUrl, categoryId);
            return VIEW_NEW;
        }

        productService.save(new ProductRequest(name, price, imageUrl, categoryId));
        return REDIRECT_LIST;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute(ATTR_PRODUCT, product);
        model.addAttribute(ATTR_CATEGORIES, categoryService.findAll());
        return VIEW_EDIT;
    }

    @PostMapping("/{id}/edit")
    public String update(
        @PathVariable Long id,
        @RequestParam String name,
        @RequestParam int price,
        @RequestParam String imageUrl,
        @RequestParam Long categoryId,
        Model model
    ) {
        Product product = productService.findById(id);

        List<String> errors = productService.validateProductName(name, true);
        if (!errors.isEmpty()) {
            populateEditForm(model, product, errors, name, price, imageUrl, categoryId);
            return VIEW_EDIT;
        }

        productService.update(id, new ProductRequest(name, price, imageUrl, categoryId));
        return REDIRECT_LIST;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        productService.deleteById(id);
        return REDIRECT_LIST;
    }

    private void populateNewForm(
        Model model,
        List<String> errors,
        String name,
        int price,
        String imageUrl,
        Long categoryId
    ) {
        model.addAttribute(ATTR_ERRORS, errors);
        model.addAttribute(ATTR_NAME, name);
        model.addAttribute(ATTR_PRICE, price);
        model.addAttribute(ATTR_IMAGE_URL, imageUrl);
        model.addAttribute(ATTR_CATEGORY_ID, categoryId);
        model.addAttribute(ATTR_CATEGORIES, categoryService.findAll());
    }

    private void populateEditForm(
        Model model,
        Product product,
        List<String> errors,
        String name,
        int price,
        String imageUrl,
        Long categoryId
    ) {
        model.addAttribute(ATTR_ERRORS, errors);
        model.addAttribute(ATTR_PRODUCT, product);
        model.addAttribute(ATTR_NAME, name);
        model.addAttribute(ATTR_PRICE, price);
        model.addAttribute(ATTR_IMAGE_URL, imageUrl);
        model.addAttribute(ATTR_CATEGORY_ID, categoryId);
        model.addAttribute(ATTR_CATEGORIES, categoryService.findAll());
    }
}
