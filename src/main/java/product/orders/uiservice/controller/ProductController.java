package product.orders.uiservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import product.orders.uiservice.client.ProductAPiClient;
import product.orders.uiservice.view.mapper.ProductViewMapper;

import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductAPiClient productAPiClient;

    private final ProductViewMapper productViewMapper;


    public ProductController(ProductAPiClient productAPiClient, ProductViewMapper productViewMapper) {
        this.productAPiClient = productAPiClient;
        this.productViewMapper = productViewMapper;
    }

    @GetMapping
    public String listProducts(@RequestParam(name = "category", required = false) String productCategory,
                               Model model) {
        model.addAttribute("products", productViewMapper.toListView(productAPiClient.getActiveProducts(productCategory)));
        return "products/list";
    }

    @GetMapping("/{id}")
    public String productDetails(@PathVariable("id") UUID productId, Model model) {
        model.addAttribute("product", productViewMapper.toDetailsView(productAPiClient.getProduct(productId)));
        return "products/details";
    }
}
