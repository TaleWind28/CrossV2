package Commands;

import Communication.ServerMessage;
import JsonMemories.JsonAccessedData;
import ServerTasks.GenericTask;


public class Help implements Values{
    String helpMessage;
    
    public Help(String helpMessage){
        this.helpMessage = helpMessage;
    }

    @Override
    public String toString() {
        return this.helpMessage;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task) {
        //sfrutto l'utente passato come parametro dall'implementazione del metodo dell'interfaccia per avere il messaggio d'errore
        setHelpMessage(user);
        return new ServerMessage(this.helpMessage,200);

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