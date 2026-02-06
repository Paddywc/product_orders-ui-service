package product.orders.uiservice.util;

import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

@Component
public class MoneyFormatter {

    private static final Currency USD = Currency.getInstance("USD");

    /**
     * Formats USD cents into a display-friendly string.
     *
     * Example:
     *   12345 → "$123.45"
     */
    public String formatUsd(long priceUsdCents) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        formatter.setCurrency(USD);

        return formatter.format(priceUsdCents / 100.0);
    }
}