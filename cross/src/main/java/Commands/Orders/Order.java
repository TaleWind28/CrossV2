package Commands.Orders;

public abstract class Order {
    private String user;
    private int orderId;
    public abstract String getUser();
    public abstract String getExchangeType();
    public abstract int getPrice();
    public abstract int getSize();
    public abstract int getOrderID();
}
