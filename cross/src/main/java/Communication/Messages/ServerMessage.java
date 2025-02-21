package Communication.Messages;

public class ServerMessage implements Message{
    public String errorMessage;
    public int response;

    public ServerMessage(){

    }
    
    public ServerMessage(String message, int response){
        this.errorMessage = message;
        this.response = response; 
    }

    public String toString(){
        return "response: "+this.response+",errorMessage:\n"+this.errorMessage.toString();
    }
}
