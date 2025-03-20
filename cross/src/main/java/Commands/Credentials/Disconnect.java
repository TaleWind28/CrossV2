package Commands.Credentials;

import Communication.Values;
import Communication.Messages.ServerMessage;
import JsonAccessedData.JsonAccessedData;
import JsonAccessedData.Users.Userbook;
import ServerTasks.GenericTask;

public class Disconnect implements Values{
    private String username;

    public Disconnect(String username){
        this.username = username;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data, String user,GenericTask task) throws ClassCastException{
        if(task.onlineUser.equals(""))return new ServerMessage("Disconnessione avvenuta con successo",100);    
        Userbook userbook = (Userbook)data;
        userbook.getUserMap().get(task.onlineUser).setLogged(false);
        userbook.dataFlush();
        return new ServerMessage("[Server]Disconnessione avvenuta con successo", 100);
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
