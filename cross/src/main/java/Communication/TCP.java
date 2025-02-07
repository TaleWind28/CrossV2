package Communication;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;

import Commands.Help;
import Commands.Values;
import Commands.Credentials.Login;
import Commands.Credentials.Logout;
import Commands.Credentials.Register;
import Commands.Credentials.UpdateCredentials;
import Commands.Orders.CancelOrder;
import Commands.Orders.Limitorder;
import Commands.Orders.MarketOrder;
import Commands.Orders.ShowOrderBook;
import Commands.Orders.StopOrder;

import com.squareup.moshi.Moshi;

public class TCP implements Protocol{
    protected BufferedReader receiver;
    protected PrintWriter sender;
    //protected Gson gson = new Gson();
    protected Moshi moshi = new Moshi.Builder().add(PolymorphicJsonAdapterFactory.of(Message.class, "type")
        .withSubtype(ServerMessage.class, "ServerMessage")
        .withSubtype(ClientMessage.class, "ClientMessage"))
        .add(PolymorphicJsonAdapterFactory.of(Values.class,"type")
        .withSubtype(Limitorder.class, "limitorder")
        .withSubtype(MarketOrder.class,"marketorder")
        .withSubtype(StopOrder.class, "stoporder")
        .withSubtype(ShowOrderBook.class, "showorderbook")
        .withSubtype(CancelOrder.class, "cancelorder")
        .withSubtype(Logout.class, "logout")
        .withSubtype(Register.class, "register")
        .withSubtype(Login.class, "login")
        .withSubtype(UpdateCredentials.class, "updatecredentials")
        .withSubtype(Help.class, "help")
        )
        .build();

    protected JsonAdapter<Message>adapter = moshi.adapter(Message.class);
    //implementazione metodi interfaccia
    public void setReceiver(Socket receiver) {
        try{
            this.receiver = new BufferedReader(new InputStreamReader(receiver.getInputStream()));
            //this.receiver.useDelimiter(this.delimiter);
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
                    //System.out.println("Client: "+e.getMessage());
            return null;
        }
    }
    // public ClientMessage receiveClientMessage(){
    //     try{
    //         String line = receiver.readLine();
    //         if (line!=null){
    //             //System.out.println(line);
    //             ClientMessage msg = gson.fromJson(line, ClientMessage.class);
    //             return msg;
    //         }
    //         return null;
    //     }catch(IOException e){
    //         //System.out.println("Client: "+e.getMessage());
    //         return null;
    //     }
    // }

    // public ServerMessage receiveServerMessage(){
    //     try{
    //         String line = receiver.readLine();
    //         if (line!=null){
    //             //System.out.println(line);
    //             ServerMessage msg = gson.fromJson(line, ServerMessage.class);
    //             return msg;
    //         }
    //         return null;
    //     }catch(IOException e){
    //         //System.out.println("Client: "+e.getMessage());
    //         return null;
    //     }
    // }

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
    
    //metodi classe concreta

    public BufferedReader getReceiver() {
        return receiver;
    }

    public PrintWriter getSender() {
        return sender;
    }

    // public void setGson(Gson gson) {
    //     this.gson = gson;
    // }

}
