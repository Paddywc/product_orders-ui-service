package product.orders.uiservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import product.orders.uiservice.product.model.ProductCategory;
import product.orders.uiservice.product.service.ProductService;
import product.orders.uiservice.product.view.mapper.ProductViewMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Controller for product related endpoints. Handles listing and viewing products.
 */
@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    private final ProductViewMapper productViewMapper;


    public ProductController(ProductService productService, ProductViewMapper productViewMapper) {
        this.productService = productService;
        this.productViewMapper = productViewMapper;
    }

    @GetMapping
    public String listProducts(@RequestParam(name = "category", required = false) String productCategory,
                               Model model) {
        model.addAttribute("products", productViewMapper.toListView(productService.getActiveProducts(productCategory)));

        List<ProductCategory> categories = new ArrayList<>(List.of(ProductCategory.values()));
        categories.sort(Comparator.comparing(Enum::name));
        model.addAttribute("categories", categories);

        return "products/list";
    }

    @GetMapping("/{id}")
    public String productDetails(@PathVariable("id") UUID productId, Model model) {
        model.addAttribute("product", productViewMapper.toDetailsView(productService.getProduct(productId)));
        return "products/details";
    }
}
