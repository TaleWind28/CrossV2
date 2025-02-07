package Communication;

import Commands.Values;

public class ClientMessage extends Message{
    public String operation;
    public Values values;

    public ClientMessage(String payload,Values values){
        this.operation = payload;
        this.values = values;
    }


    public String toString(){
        return "ClientMessage{code = "+this.operation + " ,values = "+this.values.toString()+"}";
    }
}
