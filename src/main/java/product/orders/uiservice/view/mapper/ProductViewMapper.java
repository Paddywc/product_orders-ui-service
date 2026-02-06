package product.orders.uiservice.view.mapper;

import org.springframework.stereotype.Component;
import product.orders.uiservice.client.dto.ProductDto;
import product.orders.uiservice.util.MoneyFormatter;
import product.orders.uiservice.view.ProductCardViewModel;
import product.orders.uiservice.view.ProductDetailViewModel;
import product.orders.uiservice.view.ProductListViewModel;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ProductViewMapper {

    private final MoneyFormatter moneyFormatter;

    public ProductViewMapper(MoneyFormatter moneyFormatter) {
        this.moneyFormatter = moneyFormatter;
    }


    private boolean productIsActive(ProductDto productDto){
        return Objects.equals(productDto.status(), "ACTIVE");
    }
    public ProductDetailViewModel toDetailsView(ProductDto product){
        return new ProductDetailViewModel(
                product.productId(),
                product.name(),
                product.description(),
                moneyFormatter.formatUsd(product.priceUSDCents()),
                product.category(),
                productIsActive(product));
    }

    public ProductCardViewModel toCardView(ProductDto product){
        return new ProductCardViewModel(
                product.productId(),
                product.name(),
                moneyFormatter.formatUsd(product.priceUSDCents()));
    }

    public ProductListViewModel toListView(List<ProductDto> products){
        return new ProductListViewModel(
                products.stream()
                        .map(this::toCardView)
                        .collect(Collectors.toList()));
    }

}
