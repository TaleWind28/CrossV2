package Commands;

import Communication.ServerMessage;
import Communication.Values;
import JsonMemories.JsonAccessedData;


public class Help implements Values{
    String helpMessage;
    public Help(String helpMessage){
        this.helpMessage = helpMessage;
    }
    @Override
    public ServerMessage execute(JsonAccessedData data) {
        //if (!context.onlineUser.equals(""))setHelpMessage(loggedUserMessage);
        //else setHelpMessage(nonLoggedUserMessage);
        
        return new ServerMessage(this.helpMessage,200);

    }
}