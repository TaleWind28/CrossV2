package Commands;

import Communication.Messages.ServerMessage;
import JsonAccessedData.JsonAccessedData;
import Server.ServerTasks.GenericTask;

public interface Values {
    @Override
    public String toString();
    public ServerMessage execute(JsonAccessedData data,String user, GenericTask genericTask)throws ClassCastException;
    public void setUsername(String user);
    public String getUsername();
}
