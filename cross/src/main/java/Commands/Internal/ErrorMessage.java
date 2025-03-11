package Commands.Internal;

import Communication.Values;
import Communication.Messages.ServerMessage;
import JsonAccessedData.JsonAccessedData;
import ServerTasks.GenericTask;
import Utils.AnsiColors;

public class ErrorMessage implements Values{
    private String message;
    
    public ErrorMessage(String message){
        this.message = AnsiColors.RED+message+AnsiColors.RESET;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task) {
        return new ServerMessage(this.message, 104);
    }
    @Override
    public void setUsername(String user) {
        return;    
    }
    @Override
    public String getUsername() {
        return "unused";
    }
}
