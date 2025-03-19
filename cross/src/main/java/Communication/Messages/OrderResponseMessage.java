package Communication.Messages;

import Utils.AnsiColors;

public class OrderResponseMessage extends ServerMessage{
    private int orderId;
    private String message;
    public OrderResponseMessage(int orderId,String message){
        super(message,-1000);
        this.orderId = orderId;
        this.message = message;
        this.setMessageColor(AnsiColors.BLUE_DEEP);
    }

    @Override
    public String toString() {
        return this.getMessageColor()+"OrderResponseMessage{\norderId='"+this.orderId+"'\nMessage='"+this.message+"'\n}"+AnsiColors.RESET;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getMessage() {
        return message;
    }



}
