package ClientTask;

import Communication.Messages.UDPMessage;
import Communication.Protocols.UDP;
import Executables.ClientClass;

public class UDPReceiverTask implements Runnable{
    private UDP UDPUpdater;
    private ClientClass generator;
    
    public UDPReceiverTask(UDP UDPUpdater, ClientClass generator){
        this.UDPUpdater = UDPUpdater;
        this.generator = generator; 
    }   

    public void run(){
        while(true){
            //ricevo il messaggio
            UDPMessage message = (UDPMessage)this.UDPUpdater.receiveMessage();
            //se è destinato a me lo stampo
            if(message.getInterestedUser().equals(generator.onlineUser))System.out.println("[ReceiverUDP] Received:\n"+message.tradeNotification());
        }
    }

    public void setUDPUpdater(UDP udpUPdater){
        this.UDPUpdater = udpUPdater;
    }
}
