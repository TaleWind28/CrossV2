package Commands.Orders;

public class PriceHystory extends Order{
    private String type;
    private String orderType;
    

    public PriceHystory(int orderId, String exchangeType, String orderType, int size, int price, long timestamp){
        this.setGmt(timestamp);
        this.setOrderId(orderId);
        this.setSize(size);
        this.setPrice(price);
        this.type = exchangeType;
        this.orderType = orderType;
    }

    @Override
    public String getExchangeType() {
        return this.type;
    }    

    public String getOrderType() {
        return orderType;
    }

    @Override
    public String toString() {
        return "{" +
        "orderId=" + this.getOrderId() +
        ", exchangeType='" + this.getExchangeType() + '\'' +
        ", orderType='" + this.orderType + '\'' +
        ", size=" + this.getSize() +
        ", price=" + this.getPrice() +
        ", timestamp=" + this.getGmt()+
        '}';
    }
}
