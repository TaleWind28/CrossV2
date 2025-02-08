package Commands.Credentials;

import Commands.Values;
import Communication.ServerMessage;
import JsonMemories.JsonAccessedData;

public class Disconnect implements Values{
    private String username;

    public Disconnect(String username){
        this.username = username;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data, String user) {
        return new ServerMessage("Disconnessione avvenuta con successo",408);    
    }

    @Override
    public void setUsername(String user) {
        return;    
    }

    @Override
    public String getUsername() {
        return this.username;    
    }
}
