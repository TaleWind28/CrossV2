package Commands.Internal;

import Commands.Values;
import Communication.ServerMessage;
import JsonUtils.JsonAccessedData;
import ServerTasks.GenericTask;

public class ErrorMessage implements Values{
    private String message;
    public ErrorMessage(String message){
        this.message = message;
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
