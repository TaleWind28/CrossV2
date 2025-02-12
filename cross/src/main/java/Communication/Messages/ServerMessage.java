package Communication.Messages;

public class ServerMessage implements Message{
    public String errorMessage;
    public int response;

    public ServerMessage(String message, int response){
        this.errorMessage = message;
        this.response = response; 
    }

    public String toString(){
        return "ServerMessage{response: "+this.response+"errorMessage: "+this.errorMessage+"}";
    }
}
