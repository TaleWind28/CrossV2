package Communication.Messages;

public class UDPMessage implements Message{
    private String data;
    public UDPMessage(String data){
        this.data = data;
    }
    public String getData() {
        return data;
    }
    @Override
    public String toString() {
        return "UDPMessage{Data= '"+this.getData() +"'}";    
    }
}
