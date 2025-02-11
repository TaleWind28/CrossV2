package Utils;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

public class OrderSortingAdapter {

    @ToJson
    public String toJson(OrderSorting orderSorting) {
        // Converte l'oggetto OrderSorting in una rappresentazione stringa, per esempio
        return "Price: " + orderSorting.getPrice() + ", Timestamp: " + orderSorting.getTimestamp()+", OrderId: "+orderSorting.getOrderId();
    }

    @FromJson
    public OrderSorting fromJson(String json) {
        // Recupera i dati dalla stringa e crea un nuovo oggetto OrderSorting (questo dipende dalla tua logica)
        // Esempio: usa una logica di parsing per ottenere i dati da "Price: 100, Timestamp: 2023-01-01T12:00:00Z"
        String[] parts = json.split(", ");
        int price = Integer.parseInt(parts[0].split(": ")[1]);
        String timestamp = parts[1].split(": ")[1];
        int orderId = Integer.parseInt(parts[2].split(": ")[1]);
        return new OrderSorting(timestamp, price,orderId);
    }
}
