package Communication.Messages;

import Commands.Values;
import Utils.AnsiColors;

public class ClientMessage implements Message{
    public String operation;
    public Values values;
    private String color;
    //public Message serverResponse;

    public ClientMessage(String payload,Values values){
        this.operation = payload;
        this.values = values;
        setMessageColor(AnsiColors.ORANGE);
    }


    public String toString(){
        return this.color+"ClientMessage{operation = "+this.operation + " ,values = "+this.values.toString()+"}"+AnsiColors.RESET;
    }

    public String getMessageColor() {
        return color;
    }

    public void setMessageColor(String color) {
        this.color = color;
    }
}
