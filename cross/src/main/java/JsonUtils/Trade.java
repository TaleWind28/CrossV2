package JsonUtils;
// Classe Trade
public  class Trade {
    private long timestamp;
    private int orderId;
    private int size;
    private int price;
    private String type;
    private String orderType;

    // Costruttore vuoto necessario per Moshi
    public Trade() {}

    public long getTimestamp() {
        return timestamp;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getSize() {
        return size;
    }

    public int getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public String getOrderType() {
        return orderType;
    }
    
    @Override
    public String toString() {
        return "Trade{timestamp=" + timestamp + 
               ", orderId=" + orderId + 
               ", size=" + size + 
               ", price=" + price + 
               ", type='" + type + '\'' + 
               ", orderType='" + orderType + '\'' + '}';
    }
}