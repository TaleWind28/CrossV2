package ClientTask;

import Communication.Messages.UDPMessage;
import Communication.Protocols.UDP;
import Executables.ClientMain;
import Utils.AnsiColors;

public class UDPReceiverTask implements Runnable{
    private UDP UDPUpdater;
    private ClientMain generator;
    public UDPReceiverTask(UDP UDPUpdater, ClientMain generator){
        this.UDPUpdater = UDPUpdater;
        this.generator = generator; 
    }   

    public void run(){
        System.out.println(AnsiColors.BRIGHT_RED+"sono vivo");
        while(true){
                //ricevo il messaggio
                UDPMessage message = (UDPMessage)this.UDPUpdater.receiveMessage();
                //se è destinato a me lo stampo
                if(!message.getInterestedUser().equals(generator.onlineUser)){
                    System.out.println(AnsiColors.BLACK+"diomerda"+AnsiColors.RESET);
                    continue;
                }
                else System.out.println("[ReceiverUDP] Received:\n"+message.tradeNotification());
            }
    }

    public void setUDPUpdater(UDP udpUPdater){
        this.UDPUpdater = udpUPdater;
    }
}
