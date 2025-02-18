package Communication.Messages;

public class OrderResponseMessage implements Message{
    private int orderId;
    
    public OrderResponseMessage(int orderId){
        this.orderId = orderId;
        this.toString();
    }

    @Override
    public String toString() {
        return "OrderResponseMessage{\norderId='"+this.orderId+"'\n}";
    }

}
