package Commands.Internal;

import Communication.Values;
import Communication.Messages.ServerMessage;
import JsonAccessedData.JsonAccessedData;
import ServerTasks.GenericTask;
import Utils.AnsiColors;


public class Help implements Values{
    String helpMessage;
    
    public Help(String helpMessage){
        this.helpMessage = AnsiColors.ORANGE+helpMessage+AnsiColors.RESET;
    }

    @Override
    public String toString() {
        return this.helpMessage;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task) {
        //sfrutto l'utente passato come parametro dall'implementazione del metodo dell'interfaccia per avere il messaggio d'errore
        return new ServerMessage(task.getHelpMessage(),200);

    }
    
    @Override
    public void setUsername(String user) {
        this.setHelpMessage(user);
        return;    
    }

    public void setHelpMessage(String helpMessage) {
        this.helpMessage = helpMessage;
    }

    @Override
    public String getUsername() {
        return "unused";
    }
}