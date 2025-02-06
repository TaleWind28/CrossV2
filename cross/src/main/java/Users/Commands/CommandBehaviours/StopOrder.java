package Users.Commands.CommandBehaviours;
import Communication.Message;
import ServerTasks.GenericTask;
import Users.Commands.UserCommand;

public class StopOrder implements CommandBehaviour{
    
    public Message executeOrder(UserCommand cmd,GenericTask context){
        if(context.onlineUser.equals(""))return new Message("401: Per effettuare ordini bisogna creare un account o accedervi",401);
        return new Message("stoporder",200);
    }
    
    @Override
    public int getUnicode() {
        return 101;    
    }

}
