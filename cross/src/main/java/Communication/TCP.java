package Communication;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.google.gson.Gson;

public class TCP implements Protocol{
    protected BufferedReader receiver;
    protected PrintWriter sender;
    protected Gson gson = new Gson();

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
                //System.out.println(line);
                Message msg = gson.fromJson(line, Message.class);
                return msg;
            }
            return null;
        }catch(IOException e){
            //System.out.println("Client: "+e.getMessage());
            return null;
        }
    }

    public int sendMessage(Message message){
        try {
            if (this.sender!=null){
                //System.out.println(message.toString());
                String send = this.gson.toJson(message);
                this.sender.println(send);
            }
        } catch (Exception e) {
            System.out.println("TCP: "+ e.getMessage());
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


}
