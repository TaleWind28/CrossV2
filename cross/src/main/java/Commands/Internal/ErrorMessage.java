package Commands.Internal;

import Communication.Values;
import Communication.Messages.OrderResponseMessage;
import Communication.Messages.ServerMessage;
import JsonAccessedData.JsonAccessedData;
import ServerTasks.GenericTask;
import Utils.AnsiColors;

public class ErrorMessage implements Values{
    private String message;
    private String type;
    private int size;
    private int price;

    public ErrorMessage(String message){
        this.message = AnsiColors.RED+message+AnsiColors.RESET;
    }

    public ErrorMessage(String message, String type,int size, int price){
        this.message = message;
        this.type = type;
        this.size = size;
        this.price = price;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task) {
        ServerMessage mess;
        if(size == -1) mess = new OrderResponseMessage(-1,this.message);
        else mess = new ServerMessage(this.message,101);
        mess.setMessageColor(AnsiColors.RED);
        return mess;
    }
    @Override
    public void setUsername(String user) {
        return;    
    }
    @Override
    public String getUsername() {
        return "unused";
    }

    public String toString(){
        
        return "ErrorMessage{message='"+this.message+"', type='"+this.type+"', size='"+this.size+"', price='"+this.price+"'}";
    }
}
