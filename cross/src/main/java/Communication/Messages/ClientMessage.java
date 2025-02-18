package Communication.Messages;

import Communication.Values;

public class ClientMessage implements Message{
    public String operation;
    public Values values;
    //public Message serverResponse;

    public ClientMessage(String payload,Values values){
        this.operation = payload;
        this.values = values;
    }


    public String toString(){
        return "ClientMessage{code = "+this.operation + " ,values = "+this.values.toString()+"}";
    }
}
