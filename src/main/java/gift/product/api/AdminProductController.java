package gift.product.api;

import gift.category.service.CategoryService;
import gift.product.Product;
import gift.product.service.ProductService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String create(ProductRequest request, Model model) {
        List<String> errors = productService.adminSave(request);
        if (!errors.isEmpty()) {
            model.addAttribute(ATTR_ERRORS, errors);
            model.addAttribute(ATTR_CATEGORIES, categoryService.findAll());
            return VIEW_NEW;
        }
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
    public String update(@PathVariable Long id, ProductRequest request, Model model) {
        List<String> errors = productService.adminUpdate(id, request);
        if (!errors.isEmpty()) {
            model.addAttribute(ATTR_ERRORS, errors);
            model.addAttribute(ATTR_PRODUCT, productService.findById(id));
            model.addAttribute(ATTR_CATEGORIES, categoryService.findAll());
            return VIEW_EDIT;
        }
        return REDIRECT_LIST;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        productService.deleteById(id);
        return REDIRECT_LIST;
    }

}
