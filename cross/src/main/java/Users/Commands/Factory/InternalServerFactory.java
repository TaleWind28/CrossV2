package Users.Commands.Factory;

import JsonMemories.JsonAccessedData;
import Users.Commands.InternalCommand;
import Users.Commands.UserCommand;

public class InternalServerFactory implements UserCommandFactory{

    @Override
    public UserCommand createUserCommand(String[] command) {
        if(command[0].toLowerCase().equals("exit")){
            return new InternalCommand("Disconnect");
        }else{
            return new InternalCommand("Help");
        }
    }

    @Override
    public void setJsonDataStructure(JsonAccessedData data) {
        return;
    }

    @Override
    public void additionalInfo(String otherinfo) {
        return;
    }

}
