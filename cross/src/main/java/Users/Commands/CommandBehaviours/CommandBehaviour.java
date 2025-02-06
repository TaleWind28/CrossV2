package Users.Commands.CommandBehaviours;
import Communication.Message;
import ServerTasks.GenericTask;
import Users.Commands.UserCommand;

public interface CommandBehaviour {
    public  Message executeOrder(UserCommand cmd,GenericTask context);
    public int getUnicode();
    
}
