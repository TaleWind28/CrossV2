package Commands;

import Communication.ServerMessage;
import JsonMemories.JsonAccessedData;

public class ErrorMessage implements Values{
    private String message;
    public ErrorMessage(String message){
        this.message = message;
    }
    @Override
    public ServerMessage execute(JsonAccessedData data,String user) {
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
