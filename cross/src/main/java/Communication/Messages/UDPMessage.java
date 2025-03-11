package Communication.Messages;

import java.net.DatagramPacket;

import Utils.AnsiColors;

public class UDPMessage implements Message{
    //private String data;
    private String interestedUser;
    private String notification;
    private String[] trades;
    private String color;
    public UDPMessage(){

    }

    public UDPMessage(String interestedUser, String notification, String[] trades){
        this.interestedUser = interestedUser;
        this.notification = notification;
        this.trades = trades;
        this.setMessageColor(AnsiColors.MAGENTA);
    }
   
    public String getFullMessage(){
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
        return  this.color+"[Notification]: closedTrades\n[Trades]:{\n"+ this.tradesString()+"\n}"+AnsiColors.RESET ;
    }   

    @Override
    public String toString() {
        return "UDPMessage{InterestedUser= '"+this.getInterestedUser()+", Notify= '"+this.getNotification()+", Trades= '"+this.getTrades() +"'}";    
    }

    public UDPMessage buildFromPackage(DatagramPacket packet){
        String packString = new String(packet.getData(),0,packet.getLength());
        this.interestedUser = packString.split(";")[0];
        this.notification = packString.split(";")[1];
        this.trades = packString.split(";")[2].split("");
        return this;
        
    }

    @Override
    public String getMessageColor() {
        return this.color;    
    }

    @Override
    public void setMessageColor(String color) {
        this.color = color;    
    }
}
