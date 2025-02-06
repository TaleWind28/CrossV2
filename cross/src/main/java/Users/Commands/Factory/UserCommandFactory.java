package Users.Commands.Factory;

import JsonMemories.JsonAccessedData;
import Users.Commands.UserCommand;

public interface UserCommandFactory {
    public UserCommand createUserCommand(String[] command);
    public void setJsonDataStructure(JsonAccessedData data);
    public void additionalInfo(String otherinfo);
}
