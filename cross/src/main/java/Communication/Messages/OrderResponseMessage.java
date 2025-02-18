package Communication.Messages;

public class OrderResponseMessage extends ServerMessage{
    private int orderId;
    private String message;
    public OrderResponseMessage(int orderId,String message){
        super(message,-1000);
        this.orderId = orderId;
        this.message = message;
        this.toString();
    }

    @Override
    public String toString() {
        return "OrderResponseMessage{\norderId='"+this.orderId+"'\nMessage='"+this.message+"'\n}";
    }

}
