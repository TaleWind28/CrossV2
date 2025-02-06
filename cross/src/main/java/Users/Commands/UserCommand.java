package Users.Commands;

import Communication.Message;
import JsonMemories.JsonAccessedData;
import ServerTasks.GenericTask;

public interface UserCommand {
    public Message execute(GenericTask context);
    public String[] getInfo();
    public String getType();
    public JsonAccessedData getJsonAccessedData();
    public int getUnicode();
    //public int validateCommand(UserCommand cmd);
    public void setUser(String user);
}
