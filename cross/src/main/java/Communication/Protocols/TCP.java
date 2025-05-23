package Communication.Protocols;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;

import Commands.Values;
import Commands.Credentials.Disconnect;
import Commands.Credentials.Login;
import Commands.Credentials.Logout;
import Commands.Credentials.Register;
import Commands.Credentials.UpdateCredentials;
import Commands.Internal.ErrorMessage;
import Commands.Internal.Help;
import Commands.Internal.getPriceHistory;
import Commands.Orders.CancelOrder;
import Commands.Orders.Limitorder;
import Commands.Orders.MarketOrder;
import Commands.Orders.ShowOrderBook;
import Commands.Orders.ShowStopOrder;
import Commands.Orders.StopOrder;
import Communication.Messages.ClientMessage;
import Communication.Messages.Message;
import Communication.Messages.OrderResponseMessage;
import Communication.Messages.ServerMessage;

import com.squareup.moshi.Moshi;

public class TCP implements Protocol{
    protected BufferedReader receiver;
    protected PrintWriter sender;
    protected Moshi moshi = new Moshi.Builder().add(PolymorphicJsonAdapterFactory.of(Message.class, "type")
        .withSubtype(ServerMessage.class, "ServerMessage")
        .withSubtype(ClientMessage.class, "ClientMessage")
        .withSubtype(OrderResponseMessage.class, "Confirmation"))
        .add(PolymorphicJsonAdapterFactory.of(Values.class,"type")
        .withSubtype(Limitorder.class, "limitorder")
        .withSubtype(MarketOrder.class,"marketorder")
        .withSubtype(StopOrder.class, "stoporder")
        .withSubtype(ShowOrderBook.class, "showorderbook")
        .withSubtype(ShowStopOrder.class, "showstoporder")
        .withSubtype(CancelOrder.class, "cancelorder")
        .withSubtype(Logout.class, "logout")
        .withSubtype(Register.class, "register")
        .withSubtype(Login.class, "login")
        .withSubtype(UpdateCredentials.class, "updatecredentials")
        .withSubtype(Help.class, "help")
        .withSubtype(getPriceHistory.class, "getpricehistory")
        .withSubtype(ErrorMessage.class, "errormessage")
        .withSubtype(Disconnect.class, "disconnect"))
        .add(PolymorphicJsonAdapterFactory.of(ZonedDateTime.class, "GMT"))
        .build();

    protected JsonAdapter<Message>adapter = moshi.adapter(Message.class);
    
    //implementazione metodi interfaccia
    public void setReceiver(Socket receiver) {
        try{
            this.receiver = new BufferedReader(new InputStreamReader(receiver.getInputStream()));
        }
        catch(IOException e){
            System.out.println("Errore nel settare il receiver");
        }
    }

    public void setSender(Socket sender){
        try{
            this.sender = new PrintWriter(sender.getOutputStream(),true);
        }
        catch(IOException e){
            System.out.println("Errore nel settare il sender");
        }
        
    }

    public Message receiveMessage(){
        try{
            String line = receiver.readLine();
            if (line!=null){
                Message msg = adapter.fromJson(line); 
                return msg;
            }
            return null;
        }catch(IOException e){
            System.out.println("[Message]Unable to receive a message");
            return null;
        }
    }
    
    public int sendMessage(Message message){
        try {
            if (this.sender!=null){
                //creo una stringa json
                String send = this.adapter.toJson(message);
                //mando la stringa json sul socket
                this.sender.println(send);
            }
        } catch (Exception e) {
            System.out.println("[TCP]: "+ e.getMessage());
            return 0;
        }
        return 1;
    }
    
    public void close(){
        this.sender.close();
        try{
            this.receiver.close();
        }
        catch(Exception e){
            System.out.println("[TCP] chiusura: "+e.getMessage());
        }
    }
    //metodi classe concreta

    public BufferedReader getReceiver() {
        return receiver;
    }

    public PrintWriter getSender() {
        return sender;
    }

}
