package product.orders.uiservice.cart.service;

import org.springframework.stereotype.Service;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.cart.model.CartItem;
import product.orders.uiservice.cart.repository.CartRepository;
import product.orders.uiservice.product.dto.ProductDto;
import product.orders.uiservice.product.service.ProductService;

import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;

    public CartServiceImpl(CartRepository cartRepository,
                           ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cart getCart() {
        return cartRepository.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addItem(UUID productId, int quantity) {
        ProductDto product = productService.getProduct(productId);

        Cart cart = cartRepository.get();
        cart.addItem(new CartItem(
                product.productId(),
                product.name(),
                product.priceUSDCents(),
                quantity
        ));

        cartRepository.save(cart);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeItem(UUID productId) {
        Cart cart = cartRepository.get();
        cart.removeItem(productId);
        cartRepository.save(cart);
    }

    @Override
    public void clear() {
        cartRepository.clear();
    }
}
