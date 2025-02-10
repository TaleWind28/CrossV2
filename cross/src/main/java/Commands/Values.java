package Commands;

import Communication.ServerMessage;
import JsonMemories.JsonAccessedData;
import ServerTasks.GenericTask;

public interface Values {
    @Override
    public String toString();
    public ServerMessage execute(JsonAccessedData data,String user, GenericTask genericTask);
    public void setUsername(String user);
    public String getUsername();
}
