package Commands;

import Communication.ServerMessage;
import JsonMemories.JsonAccessedData;

public interface Values {
    @Override
    public String toString();
    public ServerMessage execute(JsonAccessedData data,String user);
    public void setUsername(String user);
    public String getUsername();
}
