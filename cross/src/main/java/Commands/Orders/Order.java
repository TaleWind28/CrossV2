package Commands.Orders;

public interface Order {
    public String getUser();
    public String getExchangeType();
    public int getPrice();
    public int getSize();
    public int getOrderID();
}
