package Communication.Messages;

import java.net.DatagramPacket;

public class UDPMessage implements Message{
    //private String data;
    private String interestedUser;
    private String notification;
    private String[] trades;

    public UDPMessage(){

    }

    public UDPMessage(String interestedUser, String notiication, String[] trades){
        //this.data = data;
        System.out.println("[UDPMessage-Construct]User="+interestedUser);
        this.interestedUser = interestedUser;
        this.notification = notiication;
        this.trades = trades;
    }
    
    // public String getData() {
    //     return data;
    // }
   
    public String getFullMessage(){
        System.out.println("[UDPMessage] "+this.tradesString());
        return this.interestedUser+";"+this.notification+";"+this.tradesString();
    }

    public String getInterestedUser() {
        return interestedUser;
    }

    public String getNotification() {
        return notification;
    }

    public String[] getTrades() {
        return trades;
    }

    public String tradesString(){
        String tradeString = "";
        for(String trade: this.trades){
            tradeString+=trade;
        }
        return tradeString;
    } 
    
    public String tradeNotification(){
        return  "[Notification]: closedTrades\n[Trades]:{\n"+ this.tradesString()+"\n}" ;
    }

    @Override
    public String toString() {
        return "UDPMessage{InterestedUser= '"+this.getInterestedUser()+", Notify= '"+this.getNotification()+", Trades= '"+this.getTrades() +"'}";    
    }

    public UDPMessage buildFromPackage(DatagramPacket packet){
        String packString = new String(packet.getData(),0,packet.getLength());
        //this.data = packString.split(";")[0];
        this.interestedUser = packString.split(";")[0];
        this.notification = packString.split(";")[1];
        this.trades = packString.split(";")[2].split("");
        //System.out.println("[UDPMessage]"+this.toString());
        return this;
        
    }
}
