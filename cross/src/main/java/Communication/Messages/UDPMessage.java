package Communication.Messages;

import java.net.DatagramPacket;

public class UDPMessage implements Message{
    private String data;
    private String interestedUser;
    
    public UDPMessage(){

    }
    
    public UDPMessage(String data,String interestedUser){
        this.data = data;
        this.interestedUser = interestedUser;
    }
    
    public String getData() {
        return data;
    }
    
    public String getFullMessage(){
        return this.interestedUser+":"+this.data;
    }

    public String getInterestedUser() {
        return interestedUser;
    }

    @Override
    public String toString() {
        return "UDPMessage{InterestedUser= '"+this.getInterestedUser()+", Data= '"+this.getData() +"'}";    
    }

    public UDPMessage buildFromPackage(DatagramPacket packet){
        String packString = new String(packet.getData(),0,packet.getLength());
        this.interestedUser = packString.split(":")[0];
        this.data = packString.split(":")[1];
        //System.out.println("[UDPMessage]"+this.toString());
        return this;
        
    }
}
